package com.edfx.rpi.app.social.google;

/**
 * Class {@code GoogleConfigurationException} represents the configurational
 * exception regarding Google Services.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class GoogleConfigurationException extends RuntimeException {

	private static final long serialVersionUID = -8564568128312282534L;

	/**
	 * Constructor {@code GoogleConfigurationException}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param cause
	 */
	public GoogleConfigurationException(Throwable cause) {
		super(cause);
	}

}
