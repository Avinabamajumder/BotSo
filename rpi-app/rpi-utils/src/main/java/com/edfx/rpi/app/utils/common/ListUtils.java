package com.edfx.rpi.app.utils.common;

import java.util.AbstractList;
import java.util.List;

/**
 * Class {@code ListUtils} is the utility class for common operations on {@link List}
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class ListUtils {

	/**
	 * Constructor {@code ListUtils}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private ListUtils() {
		
	}
	
	/**
	 * Method {@code isEmpty} checks if the given list is {@code null} or the size of the list is zero
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param list the list to check
	 * @return true if empty
	 */
	public static <T> boolean isEmpty(final List<T> list) {
		return list == null || list.size() == 0;
	}
	
	/**
	 * Method {@code isNotEmpty} checks if the given list is not {@code null} or the size of the list is not zero
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param list the list to check
	 * @return true if not empty
	 */
	public static <T> boolean isNotEmpty(final List<T> list) {
		return !isEmpty(list);
	} 
	
	/**
	 * Method {@code asList} converts a String to list of {@link Character}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param string the String to convert
	 * @return an instance of List of Character 
	 */
	public static List<Character> asList(final String string) {
		return new AbstractList<Character>() {

			@Override
			public Character get(int index) {
				return string.charAt(index);
			}

			@Override
			public int size() {				
				return string.length();
			}
		};
	}
}
