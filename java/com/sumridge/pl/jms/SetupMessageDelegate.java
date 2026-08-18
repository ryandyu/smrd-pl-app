package com.sumridge.pl.jms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sumridge.pl.bean.PlPropertyBean;
import com.sumridge.pl.bean.PlSignalBean;
import com.sumridge.pl.control.PlRefreshControl;
import com.sumridge.pl.dao.VVMsdDAO;
import com.sumridge.pl.util.Constant;
import com.sumridge.pl.util.Constant.Signal;
import com.sumridge.pl.util.ThreadManager;
import com.sumridge.xml.jaxb.setup.Setup;

@Component
public class SetupMessageDelegate {
	public static Logger LOG = LoggerFactory.getLogger(SetupMessageDelegate.class);

	@Resource
	protected CacheContainer cacheManager;

	@Resource
	protected VVMsdDAO vVMsdDAO;

	@Resource
	private PlRefreshControl plRefreshControl;

	private ExecutorService messageHandlerExecutor;

	@PostConstruct
	public void start() {
		LOG.info("Starting Setup Message Delegate");

		// polling in separate thread
		messageHandlerExecutor = Executors.newSingleThreadExecutor(ThreadManager.threadFactory);
	}

	@PreDestroy
	public void stop() {
		LOG.info("Stop Message Delegate");
		// handleMessage(new ShutdownSignal());

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
		private Object entity;

		public MessageHandlerTask(Object entity) {
			this.entity = entity;
		}

		public void run() {
			try {

				Setup setup = (Setup) entity;

				if (StringUtils.isNotBlank(setup.getUltimateBenchmark())
				        || StringUtils.isNotBlank(setup.getHedgeSector())
                        || StringUtils.isNotBlank(setup.getPriceCcy())
				        || setup.getCreditSector() != null) {
				    
					String key = setup.getCusip();
					PlPropertyBean bean = (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(key);
//					if (bean == null || "".equals(bean.getProduct())) {
//						vVMsdDAO.buildVVMsd(key);
//	                    bean = (PlPropertyBean) cacheManager.getCache(Constant.PROPERTY_CACHE).get(key);
//					}

					if (bean != null) {
						if (!StringUtils.equals(bean.getUltimateBenchmark(), setup.getUltimateBenchmark())
						        || !StringUtils.equals(bean.getHedgeSector(), setup.getHedgeSector())
                                || !StringUtils.equals(bean.getCurrency(), setup.getPriceCcy())
						        || bean.getCreditSector() != setup.getCreditSector()) {
							bean.setHedgeSector(setup.getHedgeSector());
							bean.setUltimateBenchmark(setup.getUltimateBenchmark());
							bean.setBenchmark(setup.getBenchmark());
							bean.setCreditSector(setup.getCreditSector());
							
							if(setup.getPriceCcy() != null)
							    bean.setCurrency(setup.getPriceCcy());
							
							sendSignal(bean);
						} 
					} else {
						bean = new PlPropertyBean();
						bean.setCusip(setup.getCusip());
						bean.setProduct("");
						bean.setTicker("");
						bean.setCoupon("-32678");
						bean.setMaturity("1900-01-01");
						bean.setFactor(1.0);
						
						bean.setHedgeSector(setup.getHedgeSector());
						bean.setUltimateBenchmark(setup.getUltimateBenchmark());
						bean.setBenchmark(setup.getBenchmark());
						bean.setCreditSector(setup.getCreditSector());
                        
						if(setup.getPriceCcy() != null)
                            bean.setCurrency(setup.getPriceCcy());

                        cacheManager.getCache(Constant.PROPERTY_CACHE).put(setup.getCusip(), bean);
						sendSignal(bean);
					}
				}

			} catch (Throwable e) {

				LOG.error("Exception in Message Delegate: ", e);
			}
		}

		public void sendSignal(PlPropertyBean bean) {
			PlSignalBean signal = new PlSignalBean();
			signal.setProcessObject(bean);
			signal.setSignal(Signal.Setup);
			signal.setAggerate(bean.getCusip());
			plRefreshControl.addSignal(signal);
		}
	}
}
