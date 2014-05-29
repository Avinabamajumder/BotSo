package com.edfx.rpi.app.utils.properties;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code PropertiesLoader} loads the {@link java.util.Properties} needed
 * for the RPI Application
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum PropertiesLoader {
	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());
	private final Lock lock = new ReentrantLock();
	private final Map<Properties, java.util.Properties> propertyMap = Collections.synchronizedMap(new EnumMap<>(Properties.class));

	/**
	 * 
	 * Class {@code Properties} holds the {@link java.util.Properties} files
	 * needed for this Application
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public enum Properties {
		TWITTER("twitter.properties"), GOOGLE("google.properties"), GMAIL("gmail.properties");

		private String fileName;

		/**
		 * Constructor {@code Properties}
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @param fileName
		 */
		private Properties(String fileName) {
			this.fileName = fileName;
		}

		/**
		 * Method {@code getFileName}
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @return
		 */
		public String getFileName() {
			return fileName;
		}
	}

	/**
	 * Constructor {@code PropertiesLoader}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private PropertiesLoader() {

	}

	/**
	 * Method {@code getProperties} gets and returns an instance of
	 * {@link java.util.Properties} for the given {@link Properties}. It also
	 * caches the loaded {@link java.util.Properties}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param properties
	 *            the instance of Properties
	 * @return an instance of java.util.Properties
	 */
	public java.util.Properties getProperties(Properties properties) {
		try {
			lock.lock();

			java.util.Properties utilProperties = propertyMap.get(properties);

			if (Objects.isNull(utilProperties)) {
				utilProperties = new java.util.Properties();
				utilProperties.load(getClass().getResourceAsStream(properties.getFileName()));
				propertyMap.put(properties, utilProperties);
			}

			return utilProperties;
		} catch (Throwable cause) {
			logger.error("An execption occured on loading the Properties: " + properties.getFileName() + ". Reason: " + cause.getMessage(), cause);
		} finally {
			lock.unlock();
		}

		return null;
	}
}
