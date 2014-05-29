package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Hour} holds the {@link Media} which says the hour number
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Hour implements Media {

	ONE("1.wav"),
	TWO("2.wav"),
	THREE("3.wav"),
	FOUR("4.wav"),
	FIVE("5.wav"),
	SIX("6.wav"),
	SEVEN("7.wav"),
	EIGHT("8.wav"),
	NINE("9.wav"),
	TEN("10.wav"),
	ELEVEN("11.wav"),
	TWELVE("12.wav");	

	private static final Hour[] ENUMS = Hour.values();
	private static final String BASE_PATH = "date-time/time/hour";
	
	private String fileName;

	/**
	 * Constructor {@code Hour}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Hour(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code of} determines the {@link Hour} for the given number
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param hour the hour to check
	 * @return the instance of Hour
	 * @throws IllegalArgumentException
	 */
	public static Hour of(int hour) throws IllegalArgumentException {
		if (hour < 1 || hour > 12) {
			throw new IllegalArgumentException("Invalid value for hour: " + hour);
		}

		return ENUMS[hour - 1];
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
