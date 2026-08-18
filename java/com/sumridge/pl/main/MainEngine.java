package com.sumridge.pl.main;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Engine with the main method - starting point of the application. Starts all lifecycles with the manager.
 * 
 */
@Component
public class MainEngine
{
    private static final transient Logger LOG = LoggerFactory.getLogger(MainEngine.class);

    public static void main(String args[])
    {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e)
            {
                LOG.error("Uncaught exception ", e);
            }
        });

        if (!ArrayUtils.isEmpty(args))
        {
            start(args);
        }
    }

    public static void start(String[] args)
    {
        LOG.info("Starting application.. ");

        if (args.length >= 1)
        {
            ApplicationContext context = getContext(args[0]);

            LifecycleManager lifeCycleManager = (LifecycleManager) context.getBean("LifeCycleManager");
            lifeCycleManager.start();
        }
        else
        {
            LOG.error("No contexts to load {}", ArrayUtils.toString(args));
        }

    }

    public void stop()
    {
        System.exit(0);
    }

    private static ApplicationContext getContext(String commaSeparatedStr)
    {
        LOG.info("Loading ApplicationContext {}", commaSeparatedStr);
        String[] contexts = StringUtils.split(commaSeparatedStr, ",");

        return new ClassPathXmlApplicationContext(contexts);
    }

}
