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

import com.sumridge.pl.bean.PlSignalBean;
import com.sumridge.pl.bean.PriceBean;
import com.sumridge.pl.bean.RiskCurveBean;
import com.sumridge.pl.control.PlRefreshControl;
import com.sumridge.pl.util.Constant;
import com.sumridge.pl.util.Constant.Signal;
import com.sumridge.pl.util.ThreadManager;
import com.sumridge.xml.jaxb.price.Price;
import com.sumridge.xml.jaxb.price.CurveRiskType;

@Component
public class PriceMessageDelegate {
	public static Logger LOG = LoggerFactory.getLogger(PriceMessageDelegate.class);

	@Resource
	protected CacheContainer cacheManager;

	@Resource
	private PlRefreshControl plRefreshControl;

	private ExecutorService messageHandlerExecutor;
	@PostConstruct
	public void start() {
		LOG.info("Starting Price Message Delegate");

		// polling in separate thread
		messageHandlerExecutor = Executors.newSingleThreadExecutor(ThreadManager.threadFactory);
	}
	@PreDestroy
	public void stop() {
		LOG.info("Stop Message Delegate");
		//handleMessage(new ShutdownSignal());

		// shutdown message handling thread
		messageHandlerExecutor.shutdown();

		// wait till tasks to complete
		try {
			if (!messageHandlerExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
				messageHandlerExecutor.shutdownNow();
			}
		} catch (InterruptedException e) {
			LOG.info("Message Delegate interrupted: {}", e.getMessage());
			messageHandlerExecutor.shutdownNow();
		}

		LOG.info("Message Delegate Shutdown complete");
	}

	public class ShutdownSignal {
	}

	/**
	 * Delegates messages to all listeners hooked on to that message in the
	 * listener map.
	 * 
	 * @param message
	 */
	public void handleMessage(Object message) {
		try {
			if (message == null)
				return;

			messageHandlerExecutor.submit(new MessageHandlerTask(message));

		} catch (Throwable t) {
			LOG.error("Exception in Message Delegate: ", t);
		}
	}

	protected class MessageHandlerTask implements Runnable {
		private static final int REFRESH_VALUE = 1;
		private Object entity;

		public MessageHandlerTask(Object entity) {
			this.entity = entity;
		}

		public void run() {
			try {

				Price pce = (Price) entity;
				
				if(pce.getMarks() == null || pce.getMarks().getPrcType() == 0 
				   || Double.isInfinite(pce.getMarks().getPrice()) || Double.isNaN(pce.getMarks().getPrice())
				   || pce.getMarks().getPrice() < 1.e-8)
				    return;
				
//				if (pce.getMarks() != null) {
					String key = pce.getCusip();
					PriceBean bean = (PriceBean) cacheManager.getCache(Constant.PRICE_CACHE).get(key);
					//boolean refresh = false;
					if (bean != null) {
/*						if (Math.abs(bean.getPlPrice() - pce.getMarks().getPrice()) >= REFRESH_VALUE) {
							refresh = true;
						}*/

					} else {
						bean = new PriceBean();
						cacheManager.getCache(Constant.PRICE_CACHE).put(key, bean);
						//refresh = true;
					}
					setPrice(pce, bean);

					//if (refresh) {
						// add refresh signal
						//if (plRefreshControl.isValid()) {
							PlSignalBean signal = new PlSignalBean();
							signal.setProcessObject(bean);
							signal.setSignal(Signal.Price);
							signal.setAggerate(bean.getCusip());
							plRefreshControl.addSignal(signal);
							
						//}

					//}

//				}

			} catch (Throwable e) {

				LOG.error("Exception in Message Delegate: ", e);
			}
		}

		private void setPrice(Price pce, PriceBean bean) {

			bean.setAccount(pce.getAccount());
			bean.setCusip(pce.getCusip());
			bean.setOffice(pce.getOffice());
			bean.setCr01(Double.isNaN(pce.getMarks().getCr01()) ? 0 : pce.getMarks().getCr01());
			bean.setDv01(Double.isNaN(pce.getMarks().getDv01()) ? 0 : pce.getMarks().getDv01());
			bean.setPlPrice(pce.getMarks().getPrice());  //sometime plPrice is 0, so I use price instead
			
			if(pce.getMarks().getDv01Curve() != null)
			{
			    if(bean.getDv01Curve() == null)
			        bean.setDv01Curve(new RiskCurveBean());
                RiskCurveBean.copyFrom(bean.getDv01Curve(), pce.getMarks().getDv01Curve());
            } else {
                bean.setDv01Curve(null);
            }
			
			if(pce.getMarks().getCr01Curve() != null)
			{
			    if(bean.getCr01Curve() == null)
			        bean.setCr01Curve(new RiskCurveBean());
                RiskCurveBean.copyFrom(bean.getCr01Curve(), pce.getMarks().getCr01Curve());
			} else {
			    bean.setCr01Curve(null);
			}
		}
	}
}
