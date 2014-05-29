package com.edfx.rpi.app.utils.properties;

/**
 * Class {@code UnableToLoadPropertiesException} is an exception which is thrown
 * when RPI is unable to load a Properties file.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class UnableToLoadPropertiesException extends RuntimeException {

	private static final long serialVersionUID = 1850660111339018781L;

	/**
	 * Constructor {@code UnableToLoadPropertiesException}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 */
	public UnableToLoadPropertiesException(String message) {
		super(message);
	}
}
