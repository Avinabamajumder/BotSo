package com.edfx.rpi.app.utils.audio.media;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class {@code Year} holds the {@link Media} which says the year
 * @author Tapas Bose
 * @since RPI V1.0
 */
public final class Year implements Media {

	private static final Map<Integer, Year> CACHE = Collections.synchronizedMap(new HashMap<>());
	private static final String BASE_PATH = "date-time/date/year";
	private static final Lock LOCK = new ReentrantLock();

	private String fileName;

	/**
	 * Constructor {@code Year}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 */
	private Year(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Method {@code of} determines the {@link Year} for the given number
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param year the year to check
	 * @return the instance of Year 
	 */
	public static Year of(int year) {
		LOCK.lock();

		try {
			Year cachedYear = CACHE.get(Integer.valueOf(year));

			if (Objects.isNull(cachedYear)) {
				cachedYear = new Year(year + ".wav");
				CACHE.put(Integer.valueOf(year), cachedYear);
			}

			return cachedYear;
		} finally {
			LOCK.unlock();
		}
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
