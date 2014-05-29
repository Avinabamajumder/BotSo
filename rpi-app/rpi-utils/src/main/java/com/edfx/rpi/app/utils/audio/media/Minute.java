package com.edfx.rpi.app.utils.audio.media;

/**
 * Class {@code Minute} holds the {@link Media} which says the minute number
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Minute implements Media {

	ZERO("0.wav"),
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
	THIRTY_ONE("31.wav"),
	THIRTY_TWO("32.wav"),
	THIRTY_THREE("33.wav"),
	THIRTY_FOUR("34.wav"),
	THIRTY_FIVE("35.wav"),
	THIRTY_SIX("36.wav"),
	THIRTY_SEVEN("37.wav"),
	THIRTY_EIGHT("38.wav"),
	THIRTY_NINE("39.wav"),
	FORTY("40.wav"),
	FORTY_ONE("41.wav"),
	FORTY_TWO("42.wav"),
	FORTY_THREE("43.wav"),
	FORTY_FOUR("44.wav"),
	FORTY_FIVE("45.wav"),
	FORTY_SIX("46.wav"),
	FORTY_SEVEN("47.wav"),
	FORTY_EIGHT("48.wav"),
	FORTY_NINE("49.wav"),
	FIFTY("50.wav"),
	FIFTY_ONE("51.wav"),
	FIFTY_TWO("52.wav"),
	FIFTY_THREE("53.wav"),
	FIFTY_FOUR("54.wav"),
	FIFTY_FIVE("55.wav"),
	FIFTY_SIX("56.wav"),
	FIFTY_SEVEN("57.wav"),
	FIFTY_EIGHT("58.wav"),
	FIFTY_NINE("59.wav");	

	private static final Minute[] ENUMS = Minute.values();
	private static final String BASE_PATH = "date-time/time/minute";
	
	private String fileName;

	/**
	 * Constructor {@code Minute}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Minute(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code of} determines the {@link Minute} for the given number
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param minute the minute to check
	 * @return the instance of Minute 
	 * @throws IllegalArgumentException
	 */
	public static Minute of(int minute) throws IllegalArgumentException {
		if (minute < 0 || minute > 59) {
			throw new IllegalArgumentException("Invalid value for minute: " + minute);
		}

		return ENUMS[minute];
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
