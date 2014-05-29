package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Configuration} holds the {@link Media} releted to the Configuration Error
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Configuration implements Media {
	
	CONFIG_FILE_NOT_FOUND("config-file-not-found.wav"),
	UNABLE_TO_READ_CONFIG_FILE("unable-to-read-config-file.wav"),
	MALFORMED_CONFIG_FILE_FILE("malformed-config-file.wav"),
	UNKNOWN_CONFIG_ERROR("unknown-config-error.wav");
	
	private static final String BASE_PATH = "config";
	
	private String fileName;

	/**
	 * Constructor {@code Configuration}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Configuration(String fileName) {
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
