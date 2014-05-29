package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Period} holds the {@link Media} which says the period am and pm
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Period implements Media {

	AM("0.wav"),
	PM("1.wav");

	private static final Period[] ENUMS = Period.values();
	private static final String BASE_PATH = "date-time/time/am-pm";
	
	private String fileName;

	/**
	 * Constructor {@code Period}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Period(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code of} determines the {@link Period} for the given number
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param period the period to check
	 * @return the instance of Period 
	 * @throws IllegalArgumentException
	 */
	public static Period of(int period) throws IllegalArgumentException {
		if (period < 0 || period > 1) {
			throw new IllegalArgumentException("Invalid value for period: " + period);
		}

		return ENUMS[period];
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
