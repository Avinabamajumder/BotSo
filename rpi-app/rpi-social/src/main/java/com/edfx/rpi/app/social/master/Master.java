package com.edfx.rpi.app.social.master;

/**
 * Class {@code Master}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class Master {

	private String googleAccount;
	private String twitterAccount;

	/**
	 * Constructor {@code Master}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public Master() {

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
