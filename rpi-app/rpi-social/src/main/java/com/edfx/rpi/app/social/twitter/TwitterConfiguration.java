package com.edfx.rpi.app.social.twitter;

import java.io.Serializable;

/**
 * Class {@code TwitterConfiguration}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class TwitterConfiguration implements Serializable {

	private static final long serialVersionUID = 6831606352709044610L;

	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private String userAccount;
	private String rpiAccount;

	/**
	 * Constructor {@code TwitterConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public TwitterConfiguration() {

	}

	/**
	 * Method {@code getConsumerKey} return the consumerKey
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the consumerKey
	 */
	public String getConsumerKey() {
		return consumerKey;
	}

	/**
	 * Method {@code setConsumerKey} set the consumerKey
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param consumerKey
	 *            the consumerKey to set
	 */
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	/**
	 * Method {@code getConsumerSecret} return the consumerSecret
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the consumerSecret
	 */
	public String getConsumerSecret() {
		return consumerSecret;
	}

	/**
	 * Method {@code setConsumerSecret} set the consumerSecret
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param consumerSecret
	 *            the consumerSecret to set
	 */
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	/**
	 * Method {@code getAccessToken} return the accessToken
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Method {@code setAccessToken} set the accessToken
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Method {@code getAccessTokenSecret} return the accessTokenSecret
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the accessTokenSecret
	 */
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	/**
	 * Method {@code setAccessTokenSecret} set the accessTokenSecret
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param accessTokenSecret
	 *            the accessTokenSecret to set
	 */
	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	/**
	 * Method {@code getUserAccount} return the userAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the userAccount
	 */
	public String getUserAccount() {
		return userAccount;
	}

	/**
	 * Method {@code setUserAccount} set the userAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param userAccount
	 *            the userAccount to set
	 */
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	/**
	 * Method {@code getRpiAccount} return the rpiAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the rpiAccount
	 */
	public String getRpiAccount() {
		return rpiAccount;
	}

	/**
	 * Method {@code setRpiAccount} set the rpiAccount
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param rpiAccount
	 *            the rpiAccount to set
	 */
	public void setRpiAccount(String rpiAccount) {
		this.rpiAccount = rpiAccount;
	}
}
