package com.sumridge.pl.jms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sumridge.pl.bean.PlPropertyBean;
import com.sumridge.pl.bean.PlSignalBean;
import com.sumridge.pl.bean.PositionBean;
import com.sumridge.pl.control.PlRefreshControl;
import com.sumridge.pl.dao.TradeBookDAO;
import com.sumridge.pl.dao.VVMsdDAO;
import com.sumridge.pl.util.Constant;
import com.sumridge.pl.util.Constant.Signal;
import com.sumridge.pl.util.ThreadManager;
import com.sumridge.xml.jaxb.common.SecurityIdType;
import com.sumridge.xml.jaxb.position.Position;

@Component
public class PositionMessageDelegate
{
    public static Logger LOG = LoggerFactory.getLogger(PositionMessageDelegate.class);

    @Resource
	protected CacheContainer cacheManager;
    
    @Resource
	protected TradeBookDAO tradeBookDAO;

    @Resource
	private PlRefreshControl plRefreshControl;
    
    @Resource
	protected VVMsdDAO vVMsdDAO;
    
    private ExecutorService messageHandlerExecutor;
    @PostConstruct
    public void start()
    {
        LOG.info("Starting Position Message Delegate");

        // polling in separate thread
        messageHandlerExecutor = Executors.newSingleThreadExecutor(ThreadManager.threadFactory);
    }
    @PreDestroy
    public void stop()
    {
        LOG.info("Stop Message Delegate");
        //handleMessage(new ShutdownSignal());

        // shutdown message handling thread
        messageHandlerExecutor.shutdown();

        // wait till tasks to complete
        try
        {
            if (!messageHandlerExecutor.awaitTermination(1, TimeUnit.SECONDS))
            {
                messageHandlerExecutor.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            LOG.info("Message Delegate interrupted: {}", e.getMessage());
            messageHandlerExecutor.shutdownNow();
        }

        LOG.info("Message Delegate Shutdown complete");
    }

    public class ShutdownSignal
    {
    }

    /**
     * Delegates messages to all listeners hooked on to that message in the listener map.
     * 
     * @param message
     */
    public void handleMessage(Object message)
    {
        try
        {
            if (message == null)
                return;

            messageHandlerExecutor.submit(new MessageHandlerTask(message));

        }
        catch (Throwable t)
        {
            LOG.error("Exception in Message Delegate: ", t);
        }
    }

    protected class MessageHandlerTask implements Runnable
    {
        private Object entity;

        public MessageHandlerTask(Object entity)
        {
            this.entity = entity;
        }

        public void run()
        {
        	try {

        		Position pos = (Position) entity;
        		String traderAccount = pos.getTraderAccount();
        		
        		if(traderAccount == null)
        		{
        		    LOG.warn(pos.getPositionId() + " traderAccount is null ???");
        		    return;
        		}
        		
        		String securityId = "";
        		String isin = null;
        		for(SecurityIdType type : pos.getSecurity().getSecurityIds()) {
        			if(type.getType() == 2) {
        				securityId = type.getValue();
        			} else if (type.getType() == 5) {
                    	isin = type.getValue();
                    }
        		}
        		
        		PlPropertyBean plProperty = (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(securityId);
        		if(plProperty == null || "".equals(plProperty.getProduct()))  {
                    vVMsdDAO.buildVVMsd(securityId);
                    plProperty = (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(securityId);
                }
        		
        		//override securityId
        		if(plProperty != null && plProperty.getSecId() != null)
        		    securityId = plProperty.getSecId();
        		
        		String key = traderAccount + Constant.TAG + securityId;
        		PositionBean bean = (PositionBean)cacheManager.getCache(Constant.POSITION_CACHE).get(key);
        		if(bean != null) {
        			bean.setCurrentQuantity(pos.getCurrentQuantity());
        			bean.setOpenQuantity(pos.getOpenQuantity());
        			bean.setOpenPrice(pos.getOpenPrice());
        			bean.setIsin(isin);
        			
        			if(plProperty != null)
        			    bean.setCurrency(plProperty.getCurrency());
        		} else {
        		    bean = new PositionBean();

                    bean.setCurrentQuantity(pos.getCurrentQuantity());
        			bean.setOpenQuantity(pos.getOpenQuantity());
        			bean.setOpenPrice(pos.getOpenPrice());
        			bean.setTraderId(pos.getTraderId());
        			bean.setActivityDays(pos.getActivityDays());
        			bean.setCreateDays(pos.getCreateDays());
        			
        			bean.setSecurityId(securityId);
        			bean.setIsin(isin);
        			bean.setTraderAccount(traderAccount);

                    if(plProperty != null)
                        bean.setCurrency(plProperty.getCurrency());
                    
                    cacheManager.getCache(Constant.POSITION_CACHE).put(key, bean);
        		}
        		
        		if(!cacheManager.getCache(Constant.ACCOUNT_CACHE).containsKey(traderAccount)) {
        			tradeBookDAO.buildTradeBook(traderAccount);
        		}
        		
        		//add refresh signal
        		//if(plRefreshControl.isValid()) {
        			PlSignalBean signal = new PlSignalBean();
            		signal.setProcessObject(bean);
            		signal.setSignal(Signal.Position);
            		signal.setAggerate(bean.getSecurityId()+ bean.getTraderAccount());
            		plRefreshControl.addSignal(signal);
        		//}

        	} catch (Throwable e) {
				
				LOG.error("Exception in Message Delegate: ", e);
			}
        }
    }
}
