package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Commons} holds the common {@link Media}
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Commons implements Media {
	
	INTERNET_CONNECTED("internet-connected.wav"),
	INTERNET_NOT_CONNECTED("internet-not-connected.wav"),
	NETWORK_CONNECTED("network-is-connected.wav"),		
	IP_ADDRESS_PREFIX("ip-address-prefix.wav"),
	THANK_YOU("thank-you.wav"),
	CONFIGURATION_SUCCESSFULL("configuration-successful.wav"),
	NOT_CONFIGURED("not-configured.wav"),
	CURRENT_DATE_TIME("current-date-time.wav"),
	AWAKE("awake.wav");
	
	private static final String BASE_PATH = "commons";
			
	private String fileName;

	/**
	 * Constructor {@code Commons}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Commons(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code getPath}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 * @see com.edfx.rpi.app.utils.audio.media.Media#getPath()
	 */
	@Override
	public String getPath() {
		return BASE_PATH + "/" + fileName;
	}
}
