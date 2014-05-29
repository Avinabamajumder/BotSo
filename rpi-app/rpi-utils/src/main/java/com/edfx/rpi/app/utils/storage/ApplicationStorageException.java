package com.edfx.rpi.app.utils.storage;

/**
 * Class {@code ApplicationStorageException} is an exception which is thrown if
 * there is any issue releted to the {@link ApplicationStorageManager}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class ApplicationStorageException extends RuntimeException {

	private static final long serialVersionUID = -7491554471498169774L;

	/**
	 * Constructor {@code ApplicationStorageException}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public ApplicationStorageException() {
		super();
	}

	/**
	 * Constructor {@code ApplicationStorageException}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 */
	public ApplicationStorageException(String message) {
		super(message);
	}	
}
