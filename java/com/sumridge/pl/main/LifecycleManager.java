package com.sumridge.pl.main;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.jms.listener.AbstractJmsListeningContainer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.sumridge.pl.control.PlRefreshControl;
import com.sumridge.pl.jms.MsdMessageDelegate;
import com.sumridge.pl.service.PlInitService;
import com.sumridge.pl.util.ThreadManager;

@Service("LifeCycleManager")
public class LifecycleManager {
	private static final transient Logger LOG = LoggerFactory
			.getLogger(LifecycleManager.class);

	@Autowired
	private LifeCycle[] lifeCycles;

	@PostConstruct
	public void init() {
		// runtime hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOG.info("Run shutdown hook");
				LifecycleManager.this.stop();

				// last attempt to stop all threads
				ThreadManager.stop();
			}
		});
	}

	@PostConstruct
	public void log() {
		LOG.info("LifeCycle: {}", ArrayUtils.toString(lifeCycles));
	}

	/**
	 * Start application
	 */
	public void start() {
		for (LifeCycle lifeCycle : lifeCycles) {
			lifeCycle.start();
		}
	}

	/**
	 * Stop application
	 */
	public void stop() {
		for (LifeCycle lifeCycle : lifeCycles) {
			lifeCycle.stop();
		}
	}

	public interface LifeCycle {
		public void start();

		public void stop();
	}

	@Component
	public static class LifeCycleImpl implements LifeCycle {
		@Resource
		protected CacheContainer cacheManager;

		@Resource
	    private PlInitService plInitService;
		@Resource
		private PlRefreshControl plRefreshControl;

		@Autowired(required = false)
		private AbstractJmsListeningContainer[] messageListenerContainers;

		public void start() {
			LOG.info("Starting Application Lifecycle..");

			this.cacheManager.start();

			this.plInitService.doInit();

			// start message listener containers
			if (!ArrayUtils.isEmpty(messageListenerContainers)) {
				for (AbstractJmsListeningContainer jlc : messageListenerContainers) {
				    if(!((SmartLifecycle)jlc).isAutoStartup())
				        jlc.start();
				}
			}
			
			try
            {
                Thread.sleep(60000);
            }
            catch (InterruptedException e)
            {
            }
			
            this.plRefreshControl.start();
		}

		public void stop() {
			LOG.info("Application Lifecycle shutdown..");

			// stop message listener containers
			if (!ArrayUtils.isEmpty(messageListenerContainers)) {
				for (AbstractJmsListeningContainer jlc : messageListenerContainers) {
					jlc.stop();
				}
			}

			this.cacheManager.stop();

			LOG.info("Application Shutdown complete");
		}
	}
}