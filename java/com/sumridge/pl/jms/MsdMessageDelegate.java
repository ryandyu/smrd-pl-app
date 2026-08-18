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
import com.sumridge.pl.control.PlRefreshControl;
import com.sumridge.pl.dao.TradeBookDAO;
import com.sumridge.pl.dao.VVMsdDAO;
import com.sumridge.pl.util.Constant.Signal;
import com.sumridge.pl.util.ThreadManager;
import com.sumridge.xml.jaxb.common.SecurityIdType;
import com.sumridge.xml.jaxb.msd.MsdAction;

@Component
public class MsdMessageDelegate {
	public static Logger LOG = LoggerFactory.getLogger(MsdMessageDelegate.class);

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
	public void start() {
		LOG.info("Starting MSD Message Delegate");

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

				MsdAction msd = (MsdAction) entity;

				String securityId = "";
				for (SecurityIdType type : msd.getSecurityIds().getSecurityIds()) {
					if (type.getType() == 2) {
						securityId = type.getValue();
						break;
					}
				}

				vVMsdDAO.buildVVMsd(securityId);

				// add refresh signal
				PlSignalBean signal = new PlSignalBean();
				signal.setProcessObject(securityId);
				signal.setSignal(Signal.MSD);
				signal.setAggerate(securityId);
				plRefreshControl.addSignal(signal);

			} catch (Throwable e) {

				LOG.error("Exception in Message Delegate: ", e);
			}
		}
	}
}
