package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Month} holds the {@link Media} which says the month name
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Month implements Media {

	JANUARY("1.wav"),
    FEBRUARY("2.wav"),
    MARCH("3.wav"),
    APRIL("4.wav"),
    MAY("5.wav"),
    JUNE("6.wav"),
    JULY("7.wav"),
    AUGUST("8.wav"),
    SEPTEMBER("9.wav"),
    OCTOBER("10.wav"),
    NOVEMBER("11.wav"),
    DECEMBER("12.wav");	

	private static final Month[] ENUMS = Month.values();
	private static final String BASE_PATH = "date-time/date/month";
	
	private String fileName;

	/**
	 * Constructor {@code Month}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Month(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code of} determines the {@link Month} for the given number
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param month the month to check
	 * @return the instance of Month 
	 * @throws IllegalArgumentException
	 */
	public static Month of(int month) throws IllegalArgumentException {
		if (month < 0 || month > 12) {
			throw new IllegalArgumentException("Invalid value for month: " + month);
		}

		return ENUMS[month - 1];
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
