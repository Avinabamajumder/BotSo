package com.edfx.rpi.app.utils.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code RpiThreadFactory} is {@link ThreadFactory} implementation for RPI 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum RpiThreadFactory implements ThreadFactory {
	INSTANCE;
	
	private final AtomicInteger poolNumber = new AtomicInteger(1);
	private final Logger logger = RpiLogger.getLogger(getClass());
	private final ThreadGroup threadGroup;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	/**
	 * Constructor {@code RpiThreadFactory}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private RpiThreadFactory() {
		SecurityManager securityManager = System.getSecurityManager();
		threadGroup = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "RpiPool-" + poolNumber.getAndIncrement() + "-Thread-";
		
	}

	/**
	 * Method {@code newThread}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param runnable
	 * @return
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(threadGroup, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			public void uncaughtException(Thread thread, Throwable cause) {
				logger.error(cause.getMessage(), cause);
			}
		});
		
		return thread;
	}
}
