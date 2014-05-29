package com.edfx.rpi.app.utils.config.speaker;

import com.edfx.rpi.app.utils.audio.AudioPlayer;
import com.edfx.rpi.app.utils.audio.media.Configuration;
import com.edfx.rpi.app.utils.audio.media.Media;
import com.edfx.rpi.app.utils.config.exception.ConfigurationException;
import com.edfx.rpi.app.utils.config.exception.ConfigurationFileNotFoundException;
import com.edfx.rpi.app.utils.config.exception.MalformedConfigurationFileException;
import com.edfx.rpi.app.utils.config.exception.UnableToReadConfigurationFileException;

/**
 * Class {@code ConfigurationExceptionSpeaker} speaks the
 * {@link ConfigurationException} messages.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 * @see ConfigurationException
 */
public enum ConfigurationExceptionSpeaker {

	INSTANCE;

	/**
	 * Constructor {@code ConfigurationExceptionSpeaker}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private ConfigurationExceptionSpeaker() {

	}

	/**
	 * Method {@code speak} speaks the error message related to the
	 * {@link ConfigurationException}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param exception
	 *            the instance to check and speak
	 */
	public void speak(Throwable exception) {
		if (exception instanceof ConfigurationException) {
			if (exception instanceof ConfigurationFileNotFoundException) {
				play(Configuration.CONFIG_FILE_NOT_FOUND);
			} else if (exception instanceof UnableToReadConfigurationFileException) {
				play(Configuration.UNABLE_TO_READ_CONFIG_FILE);
			} else if (exception instanceof MalformedConfigurationFileException) {
				play(Configuration.MALFORMED_CONFIG_FILE_FILE);
			}
		} else {
			play(Configuration.UNKNOWN_CONFIG_ERROR);
		}
	}

	private void play(Media media) {
		AudioPlayer.INSTANCE.play(media);
	}
}
