package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Day} holds the {@link Media} which says the day number
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Day implements Media {
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
	TWELVE("12.wav"),
	THIRTEEN("13.wav"),
	FOURTEEN("14.wav"),
	FIFTEEN("15.wav"),
	SIXTEEN("16.wav"),
	SEVENTEEN("17.wav"),
	EIGHTEEN("18.wav"),
	NINETEEN("19.wav"),
	TWENTY("20.wav"),
	TWENTY_ONE("21.wav"),
	TWENTY_TWO("22.wav"),
	TWENTY_THREE("23.wav"),
	TWENTY_FOUR("24.wav"),
	TWENTY_FIVE("25.wav"),
	TWENTY_SIX("26.wav"),
	TWENTY_SEVEN("27.wav"),
	TWENTY_EIGHT("28.wav"),
	TWENTY_NINE("29.wav"),
	THIRTY("30.wav"),
	THIRTY_ONE("31.wav");
	
	private static final Day[] ENUMS = Day.values();
	private static final String BASE_PATH = "date-time/date/day";

	private String fileName;
	
	/**
	 * Constructor {@code Day}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Day(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code of} determines the {@link Day} for the given number
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param day the day to check
	 * @return the instance of Day
	 * @throws IllegalArgumentException
	 */
	public static Day of(int day) throws IllegalArgumentException {
		if (day < 0 || day > 31) {
			throw new IllegalArgumentException("Invalid value for day: " + day);
		}

		return ENUMS[day - 1];
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
