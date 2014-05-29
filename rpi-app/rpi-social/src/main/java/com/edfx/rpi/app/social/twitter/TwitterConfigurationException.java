package com.edfx.rpi.app.social.twitter;

/**
 * Class {@code TwitterConfigurationException}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class TwitterConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1689458078484824414L;

	/**
	 * Constructor {@code TwitterConfigurationException}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 */
	public TwitterConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor {@code TwitterConfigurationException}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param cause
	 */
	public TwitterConfigurationException(Throwable cause) {
		super(cause);
	}
}
