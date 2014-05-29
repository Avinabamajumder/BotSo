package com.edfx.rpi.app.utils.tasks;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code ShutdownTask} shutdown the RPI
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum ShutdownTask {

	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());

	/**
	 * Constructor {@code ShutdownTask}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private ShutdownTask() {

	}

	/**
	 * Method {@code shutDown} shutdown the RPI
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param reboot
	 */
	public void shutDown(boolean reboot) {
		try {			
			String[] command = new String[] { "service", "jetty", "stop" };
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process process = processBuilder.start();
			process.waitFor();
			int exitValue = process.exitValue();
			logger.info("Command executed. Exit value: " + exitValue);
			process.destroy();
		} catch (Throwable cause) {
			logger.error(cause);
		}
		
		Runnable shutdownRunnable = () -> {
			try {
				String[] command = new String[] { "sudo", reboot ? "reboot" : "poweroff" };
				ProcessBuilder processBuilder = new ProcessBuilder(command);
				Process process = processBuilder.start();
				process.waitFor();
			} catch (Throwable cause) {
				logger.error(cause);
			}
		};
		
		Thread shutDownThread = new Thread(shutdownRunnable);		
		Runtime.getRuntime().addShutdownHook(shutDownThread);
	}
}
