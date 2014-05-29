package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Media}
 * @author Tapas Bose
 * @since RPI V1.0
 */
@FunctionalInterface
public interface Media {

	/**
	 * Method {@code getPath} returns the path to the media
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the file path
	 */
	public abstract String getPath();
}
