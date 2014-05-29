package com.edfx.rpi.app.utils.tasks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;

/**
 * Class {@code BlinkLedTask} executes the {@link Process}
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum BlinkLedTask implements Runnable {

	INSTANCE;
	
	private final Logger logger = RpiLogger.getLogger(getClass());
	private final Lock lock = new ReentrantLock();
	private final AtomicBoolean run = new AtomicBoolean(false);
	private String[] frequency;
	
	/**
	 * Constructor {@code BlinkLedTask}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private BlinkLedTask() {
		
	}

	/**
	 * Method {@code resetLed} resets the LED
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @throws Exception
	 */
	private void resetLed() throws Exception {
		String shell = "/bin/bash";
		String script = ApplicationStorageManager.INSTANCE.getScriptDirectory() +  "/blinkLEDReset.sh";						
		String[] command = new String[]{shell, script};
		
		logger.info("Executing script: " + script);
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);			
		Process process = processBuilder.start();		
		process.waitFor();	
		int exitValue = process.exitValue();
		
		logger.info("Script executed. Exit value: " + exitValue);
		
		process.destroy();
	}
	
	/**
	 * Method {@code blinkLed} blinks the LED
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @throws Exception
	 */
	private void blinkLed() throws Exception {
		String shell = "/bin/bash";
		String script = ApplicationStorageManager.INSTANCE.getScriptDirectory() + "/blinkLED.sh";						
		String[] command = new String[]{shell, script, frequency[0], frequency[1], frequency[2]};
		
		logger.info("Executing script: " + script);
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);			
		Process process = processBuilder.start();		
		process.waitFor();	
		int exitValue = process.exitValue();
		
		logger.info("Script executed. Exit value: " + exitValue);
		
		process.destroy();
	}
	
	/**
	 * Method {@code run}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		lock.lock();
		
		try {
			while(run.get()){
				try {
					resetLed();
					blinkLed();
				} catch (Throwable cause) {
					logger.error(cause);
					
					try {
						resetLed();				
					} catch (Throwable ignore) {
					}
				} 
			}
		} finally {
			lock.unlock();
		}		
	}	
	
	/**
	 * Method {@code run}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param run
	 */
	public void run(boolean run){
		this.run.set(run);
	}
	
	/**
	 * Method {@code isRunning}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	public boolean isRunning() {
		return this.run.get();
	}
	
	/**
	 * Method {@code setFrequency} sets the blinking frequency  
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param frequency
	 */
	public void setFrequency(String[] frequency) {
		this.frequency = frequency;
	}
}
