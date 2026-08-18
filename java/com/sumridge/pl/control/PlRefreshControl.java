package com.sumridge.pl.control;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.sumridge.pl.bean.PlDetailBean;
import com.sumridge.pl.bean.PlSignalBean;
import com.sumridge.pl.processor.PlRefreshProcessorFactory;
import com.sumridge.pl.service.PlMessagePushService;
import com.sumridge.pl.service.PlResultService;
import com.sumridge.pl.util.Constant.Signal;
import com.sumridge.pl.util.ThreadManager;

@Controller
public class PlRefreshControl
{
    @Resource
    protected LinkedBlockingQueue<PlSignalBean> signalBuffer;
    @Resource
    protected PlMessagePushService plMessagePushService;
    @Resource
    private PlResultService plResultService;
   
    protected ExecutorService messageHandlerExecutor;
    
    public static Logger LOG = LoggerFactory.getLogger(PlRefreshControl.class);
    
    private boolean isValid;
    private boolean stopped;

    public void addSignal(PlSignalBean signal)
    {
        signalBuffer.add(signal);

        if (signalBuffer.size() > 5000)
            LOG.warn("update pending size " + signalBuffer.size());
    }

    /**
     * @return the isValid
     */
    public boolean isValid()
    {
        return isValid;
    }

    /**
     * @param isValid the isValid to set
     */
    public void setValid(boolean isValid)
    {
        this.isValid = isValid;
    }

    public void start()
    {
        LOG.info("Starting Message rebuilder");

        // polling in separate thread
        messageHandlerExecutor = Executors.newSingleThreadExecutor(ThreadManager.threadFactory);
        messageHandlerExecutor.submit(new RebuilderTask());
    }

    @PreDestroy
    public void stop()
    {
        LOG.info("Stop Message rebuilder");
        
        stopped = true;
        
        // shutdown message handling thread
        messageHandlerExecutor.shutdown();

        // wait till tasks to complete
        try
        {
            if (!messageHandlerExecutor.awaitTermination(1 * 60, TimeUnit.SECONDS))
            {
                messageHandlerExecutor.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            LOG.info("Message rebuilder interrupted: {}", e.getMessage());
            messageHandlerExecutor.shutdownNow();
        }

        LOG.info("Message rebuilder Shutdown complete");
    }

    protected class RebuilderTask implements Runnable
    {

        @SuppressWarnings("unchecked")
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    if (signalBuffer.size() > 0)
                    {
                        List<PlSignalBean> lst = new LinkedList<PlSignalBean>();

                        signalBuffer.drainTo(lst);
                        signalBuffer.clear();

                        Map<String, PlSignalBean> map = new LinkedHashMap<String, PlSignalBean>();
                        for (PlSignalBean signal : lst)
                        {
                            map.put(signal.aggerate(), signal);
                        }

                        List<PlDetailBean> result = new ArrayList<PlDetailBean>();
                        for (PlSignalBean signal : map.values())
                        {
                            List<PlDetailBean> obj = PlRefreshProcessorFactory.createPlugin(signal.getSignal()).doProcess(signal.getProcessObject(), plResultService);
                            if (signal.getSignal() == Signal.Total)
                            {
                                List<PlDetailBean> tlt = (List<PlDetailBean>) signal.getProcessObject();

                                LOG.info(">>> force to pub missing price items " + (tlt != null ? tlt.size() : 0));

                                if (tlt != null && tlt.size() > 0)
                                    result.addAll(tlt);
                            }
                            else
                            {
                                result.addAll(obj);
                            }
                        }

                        map.clear();

                        if (result != null && result.size() > 0)
                        {
                            plMessagePushService.pushMsg(result);
                        }
                        else
                        {
                            plMessagePushService.pushAll();
                        }
                    }

                    Thread.sleep(1000);

                    if (stopped)
                    {
                        break;
                    }
                }
                catch (Throwable e)
                {
                    LOG.error("Exception in rebuild: ", e);
                }
            }
        }
    }
}
