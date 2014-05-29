package com.edfx.rpi.app.utils.tasks;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.config.WifiConfiguration;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;

/**
 * Class {@code WifiConfigureTask} configures the WIFI
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum WifiConfigureTask {

	INSTANCE;

	private Logger logger = RpiLogger.getLogger(getClass());
	
	private WifiConfigureTask() {

	}

	/**
	 * Method {@code configure}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 */
	public void configure(WifiConfiguration configuration) {			
		try {
			
			if (Objects.nonNull(configuration) && StringUtils.isNotBlank(configuration.ssid)) {
				String shell = "/bin/bash";
				String script = ApplicationStorageManager.INSTANCE.getScriptDirectory() + "/setupWifi.sh";
				String[] command = StringUtils.isNotBlank(configuration.password) ? 
						new String[] { shell, script, configuration.ssid, configuration.password } : 
						new String[] { shell, script, configuration.ssid };

				logger.info("Executing script: " + script);

				ProcessBuilder processBuilder = new ProcessBuilder(command);
				Process process = processBuilder.start();
				process.waitFor();
				int exitValue = process.exitValue();
				logger.info("Command executed. Exit value: " + exitValue);
				process.destroy();
			}			
		} catch (Throwable cause) {
			logger.error(cause);
		}
	}
}
