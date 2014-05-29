package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code CommandResponse} holds the {@link Media} which are the response of Command
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum CommandResponse implements Media {

	GO_SECURE("going-secure.wav"),
	GO_TO_SLEEP("going-to-sleep.wav"),
	INTRUDER_ALERT("intruder-alert.wav"),
	TEMPERATURE("measuring-temperature.wav"),
	STOP_SECURE("stop-secure.wav"),
	TAKE_THREE("taking-images.wav"),
	SWEEP_ROOM("taking-video.wav"),
	ARE_YOU_THERE("yes-I-am.wav");
	
	private static final String BASE_PATH = "command";

	private String fileName;

	/**
	 * Constructor {@code CommandResponse}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private CommandResponse(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code getPath}
	 * 
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
