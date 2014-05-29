package com.edfx.rpi.app.utils.common;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Class {@code StreamUtils} is the utility class for common operations on
 * {@link Stream}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class StreamUtils {

	/**
	 * Constructor {@code StreamUtils}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private StreamUtils() {

	}

	/**
	 * Method {@code enumerationAsStream} converts an {@link Enumeration} into {@link Stream}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param enumeration the Enumeration to convert
	 * @return the instance of Stream 
	 */
	public static <T> Stream<T> enumerationAsStream(Enumeration<T> enumeration) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
			public T next() {
				return enumeration.nextElement();
			}

			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}
		}, Spliterator.ORDERED), false);
	}
}
