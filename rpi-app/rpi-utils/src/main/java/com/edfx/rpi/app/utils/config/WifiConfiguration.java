package com.edfx.rpi.app.utils.config;

/**
 * Class {@code WifiConfiguration}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class WifiConfiguration {

	public final String ssid;
	public final String password;

	/**
	 * Constructor {@code WifiConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param ssid
	 * @param password
	 */
	public WifiConfiguration(String ssid, String password) {
		this.ssid = ssid;
		this.password = password;
	}
}
