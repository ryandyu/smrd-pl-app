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
import com.sumridge.pl.bean.TradeBean;
import com.sumridge.pl.control.PlRefreshControl;
import com.sumridge.pl.dao.TradeBookDAO;
import com.sumridge.pl.dao.VVMsdDAO;
import com.sumridge.pl.util.Constant;
import com.sumridge.pl.util.Constant.Signal;
import com.sumridge.pl.util.ThreadManager;
import com.sumridge.xml.jaxb.common.SecurityIdType;
import com.sumridge.xml.jaxb.trade.Trade;

@Component
public class TradeMessageDelegate
{
    public static Logger LOG = LoggerFactory.getLogger(TradeMessageDelegate.class);

    @Resource
	protected CacheContainer cacheManager;
    
    @Resource
	private PlRefreshControl plRefreshControl;
    
    @Resource
	protected TradeBookDAO tradeBookDAO;
    
    @Resource
	protected VVMsdDAO vVMsdDAO;
    
    private ExecutorService messageHandlerExecutor;
    @PostConstruct
    public void start()
    {
        LOG.info("Starting Trade Message Delegate");

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

        		Trade trade = (Trade) entity;
        		String traderAccount = trade.getTraderAccount();
        		String securityId = "";
        		String isin = null;
                for(SecurityIdType type : trade.getSecurity().getSecurityIds()) {
                    if(type.getType() == 2) {
                        securityId = type.getValue();
                    } else if (type.getType() == 5) {
                    	isin = type.getValue();
                    }
                }

                PlPropertyBean plProperty = (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(securityId);
                if(plProperty == null || "".equals(plProperty.getProduct())) {
                    vVMsdDAO.buildVVMsd(securityId);
                    plProperty = (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(securityId);
                }
                
                //override securityId
                if(plProperty != null && plProperty.getSecId() != null)
                    securityId = plProperty.getSecId();

                String key = traderAccount+ Constant.TAG + securityId;
        		TradeBean tra = new TradeBean();
        		tra.setTradeId(trade.getTradeId());
    			tra.setBuySellInd(trade.getBuySellInd());
    			tra.setPrice(trade.getPrice());
    			tra.setQuantity(trade.getQuantity());
    			tra.setSecurityId(securityId);
    			tra.setTraderAccount(traderAccount);
    			tra.setStatusInd(trade.getStatusInd());
    			tra.setTradeDate(trade.getTradeDate());
    			tra.setIsin(isin);
    			tra.setPrincipal(trade.getPrincipal());
    			tra.setTradeCcy(trade.getCurrency() != null ? trade.getCurrency() : Constant.REPORT_CCY);
    			tra.setSettleCcy(trade.getSettleCurrency() != null ? trade.getSettleCurrency() : Constant.REPORT_CCY);
    			tra.setSettleFxRate(trade.getSettleCurrencyFxRate() != null ? trade.getSettleCurrencyFxRate() : 1.0); //quoteCcy --> settleCcy
    			
    			if(Math.abs(tra.getSettleFxRate()) < 1.0e-16)
    			    tra.setSettleFxRate(1);
    			
    			PositionBean bean = (PositionBean)cacheManager.getCache(Constant.POSITION_CACHE).get(key);
    			
    			if(bean == null)
    			{
    			    LOG.warn("trade is ahead of position : " + key + ", try again");
    			    Thread.sleep(10*1000);
    			    bean = (PositionBean)cacheManager.getCache(Constant.POSITION_CACHE).get(key);
    			}
    			
        		if(bean != null) {
        			if(bean.getIsin() == null) {
        				bean.setIsin(isin);
        			}
        			bean.getTradeList().put(tra.getTradeId(), tra);
        		} else {
        		    LOG.warn("trade is ahead of position : " + key);
        		    
        			bean = new PositionBean();

                    bean.setSecurityId(securityId);
        			bean.setTraderAccount(traderAccount);
        			bean.getTradeList().put(tra.getTradeId(), tra);
        			bean.setIsin(isin);
      			    bean.setCurrency(tra.getTradeCcy());

      			    cacheManager.getCache(Constant.POSITION_CACHE).put(key, bean);
        		}

        		if(!cacheManager.getCache(Constant.ACCOUNT_CACHE).containsKey(traderAccount)) {
                    tradeBookDAO.buildTradeBook(traderAccount);
                }
        		
        		if(!Constant.REPORT_CCY.equals(tra.getSettleCcy()) && bean.getStlCurrencies().add(tra.getSettleCcy()))
                    cacheManager.getCache(Constant.POSITION_CACHE).put(key, bean);
        		
                //add refresh signal
                //if (plRefreshControl.isValid()) {
                    PlSignalBean signal = new PlSignalBean();
                    signal.setProcessObject(tra);
                    signal.setSignal(Signal.Trade);
                    signal.setAggerate(tra.getSecurityId()+tra.getTraderAccount());
                    plRefreshControl.addSignal(signal);
                //}

			} catch (Throwable e) {
				
				LOG.error("Exception in Message Delegate: ", e);
			}
        }
    }
}
