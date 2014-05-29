package com.edfx.rpi.app.utils.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.common.ListUtils;
import com.edfx.rpi.app.utils.config.exception.ConfigurationException;
import com.edfx.rpi.app.utils.config.exception.ConfigurationFileNotFoundException;
import com.edfx.rpi.app.utils.config.exception.MalformedConfigurationFileException;
import com.edfx.rpi.app.utils.config.exception.UnableToReadConfigurationFileException;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;

/**
 * Class {@code ConfigurationManager} manages the {@link Configuration} of RPI
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum ConfigurationManager {

	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());
	private final ApplicationStorageManager applicationStorageManager = ApplicationStorageManager.INSTANCE;

	/**
	 * Constructor {@code ConfigurationManager}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private ConfigurationManager() {

	}

	/**
	 * Method {@code getConfiguration} read the rpi-config.txt, validates it and
	 * returns an instance of {@link Configuration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of Configuration
	 * @throws ConfigurationException
	 */
	public Configuration getConfiguration() throws ConfigurationException {
		File file = getConfigFile();
		Path configFilePath = file.toPath();

		if (!Files.exists(configFilePath)) {
			throw new ConfigurationFileNotFoundException();
		}

		logger.info("Config file found.");

		List<String> lines = null;

		try {
			lines = Files.readAllLines(configFilePath);
		} catch (Throwable cause) {
			throw new UnableToReadConfigurationFileException();
		}

		if (ListUtils.isEmpty(lines)) {
			throw new MalformedConfigurationFileException();
		}

		Map<String, String> configurationMap = new HashMap<>();

		for (String line : lines) {
			if (StringUtils.isBlank(line)) {
				continue;
			}

			if (line.contains("=")) {
				String[] pair = line.split("=");
				configurationMap.put(pair[0], pair[1]);
			}
		}

		String userGmailAccount = configurationMap.get("userGmail");
		validate(userGmailAccount);

		String rpiGmailAccount = configurationMap.get("rpiGmail");
		validate(rpiGmailAccount);

		String rpiGmailPassword = configurationMap.get("rpiPass");
		validate(rpiGmailPassword);

		String userTwitterAccount = configurationMap.get("userHandle");
		validate(userTwitterAccount);

		String rpiTwitterAccount = configurationMap.get("rpiHandle");
		validate(rpiTwitterAccount);

		return new Configuration(userGmailAccount, rpiGmailAccount, rpiGmailPassword, userTwitterAccount, rpiTwitterAccount);
	}

	/**
	 * Method {@code getWifiConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	public WifiConfiguration getWifiConfiguration() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(getWifiConfigFile()));			
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		String ssid = prop.getProperty("wifiSSID");
		String password  = prop.getProperty("wifiPass");
		
		return new WifiConfiguration(ssid, password);
	}

	/**
	 * Method {@code validate} validates the configuration for the given key
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param key
	 */
	private void validate(String key) {
		if (StringUtils.isBlank(key)) {
			throw new MalformedConfigurationFileException();
		}
	}

	/**
	 * Method {@code getConfigFile} get the config file and return
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the config file
	 */
	private File getConfigFile() {
		return new File("/boot/rpi-config.txt");
	}

	/**
	 * Method {@code getWifiConfigFile}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	private File getWifiConfigFile() {
		return new File("/boot/wifi-config.txt");
	}

	/**
	 * Method {@code deleteConfigFile} deletes the config file
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void deleteConfigFile() {
		File file = getConfigFile();
		deleteFile(file);	
	}
	
	/**
	 * Method {@code deleteWifiConfigFile}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void deleteWifiConfigFile() {
		File file = getWifiConfigFile();
		deleteFile(file);		
	}
	
	/**
	 * Method {@code deleteFile}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param file
	 */
	private void deleteFile(File file) {
		try {
			String fullPath = file.getAbsolutePath();
			logger.info("Deleting " + fullPath);
			ProcessBuilder processBuilder = new ProcessBuilder("sudo", "rm", fullPath);
			Process process = processBuilder.start();
			process.waitFor();
			int exitValue = process.exitValue();
			logger.info("Deleted " + fullPath + ". Process exited with: " + exitValue);
			process.destroy();
		} catch (Throwable cause) {
			logger.error(cause);
		}
	}

	/**
	 * Method {@code checkReset} tests if the RPI needs to be reset
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return true if needed
	 */
	public boolean checkReset() {
		File file = getConfigFile();
		return file.exists();
	}

	/**
	 * Method {@code reset} reset the RPI Configurations
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see ApplicationStorageManager
	 */
	public void reset() {
		applicationStorageManager.deleteGoogleConfig();
		applicationStorageManager.deleteTwitterConfig();
		applicationStorageManager.deleteGoogleCredentialDir();
		applicationStorageManager.deleteSecondaryConfig();
	}
}
