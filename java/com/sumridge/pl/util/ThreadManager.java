package com.sumridge.pl.util;

import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows management of threads in the application, if created in given thread group.
 * 
 */

public class ThreadManager
{

    private static final transient Logger LOG = LoggerFactory.getLogger(ThreadManager.class);

    private static ThreadGroup THREAD_GROUP = new ThreadGroup("THREAD_GROUP") {

        @Override
        public void uncaughtException(Thread t, Throwable e)
        {
            LOG.error("Exception in thread " + t, e);
            super.uncaughtException(t, e);
        }

    };

    public static ThreadFactory threadFactory = new ThreadFactory() {
        public Thread newThread(Runnable r)
        {
        	Thread t = new Thread(THREAD_GROUP, r);
        	t.setDaemon(true);
            return t;
        }
    };

    public static void showThreads()
    {

        Thread[] threads = new Thread[] {};
        THREAD_GROUP.enumerate(threads);

        StringBuffer buff = new StringBuffer("Threads: \n");

        if (threads.length > 0)
        {
            for (Thread thread : threads)
            {
                buff.append(thread.getName() + "\n");
            }
        }
        buff.trimToSize();
        LOG.info(buff.toString());
    }

    public static void stop()
    {

        Thread[] threads = new Thread[] {};
        THREAD_GROUP.enumerate(threads);

        if (threads.length > 0)
        {
            for (Thread thread : threads)
            {
                LOG.debug("Thread : {}", thread.getName());
                thread.interrupt();
            }
        }
    }

}