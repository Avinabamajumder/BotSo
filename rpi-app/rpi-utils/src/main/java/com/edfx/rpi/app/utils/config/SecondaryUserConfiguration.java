package com.edfx.rpi.app.utils.config;

import java.io.Serializable;

/**
 * Class {@code SecondaryUserConfiguration} is POJO which represents the
 * configuration for secondary user
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class SecondaryUserConfiguration implements Serializable {

	private static final long serialVersionUID = 5109258132566380296L;

	private boolean active;
	private String googleAccount;
	private String twitterAccount;

	/**
	 * Constructor {@code SecondaryUserConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public SecondaryUserConfiguration() {

	}

	/**
	 * Method {@code isActive} return the active
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Method {@code setActive} set the active
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Method {@code getGoogleAccount} return the googleAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the googleAccount
	 */
	public String getGoogleAccount() {
		return googleAccount;
	}

	/**
	 * Method {@code setGoogleAccount} set the googleAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param googleAccount
	 *            the googleAccount to set
	 */
	public void setGoogleAccount(String googleAccount) {
		this.googleAccount = googleAccount;
	}

	/**
	 * Method {@code getTwitterAccount} return the twitterAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the twitterAccount
	 */
	public String getTwitterAccount() {
		return twitterAccount;
	}

	/**
	 * Method {@code setTwitterAccount} set the twitterAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterAccount
	 *            the twitterAccount to set
	 */
	public void setTwitterAccount(String twitterAccount) {
		this.twitterAccount = twitterAccount;
	}
}
