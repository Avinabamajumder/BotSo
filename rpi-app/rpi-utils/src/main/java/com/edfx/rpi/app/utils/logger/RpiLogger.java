package com.edfx.rpi.app.utils.logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Class {@code RpiLogger} is the global Logger for RPI
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class RpiLogger {
	private static final Lock LOCK = new ReentrantLock();
	private static final AtomicBoolean CONFIGURED = new AtomicBoolean(Boolean.FALSE);

	/**
	 * Constructor {@code RpiLogger}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private RpiLogger() {

	}

	/**
	 * Method {@code getLogger} returns the Logger for the given class
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param clazz
	 *            the class for which the Logger is requested
	 * @return an instance of Logger
	 * @see Logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		try {
			LOCK.lock();
			boolean configured = CONFIGURED.get();

			if (!configured) {
				Logger rootLogger = Logger.getRootLogger();
				rootLogger.removeAllAppenders();

				ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d{yyy-MM-dd hh:mm:ss}=> %m%n"), "System.out");
				rootLogger.addAppender(consoleAppender);

				CONFIGURED.set(Boolean.TRUE);
			}
		} catch (Throwable cause) {
			cause.printStackTrace();
		} finally {
			LOCK.unlock();
		}

		return Logger.getLogger(clazz);
	}
}
