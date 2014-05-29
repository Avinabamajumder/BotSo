package com.edfx.rpi.app.social.google;

import java.io.Serializable;

/**
 * Class {@code GoogleConfiguration} is a POJO which holds the various
 * configurational values needed by {@link GoogleManager} and other classes
 * associated with {@link GoogleManager} to communicate with Google services.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class GoogleConfiguration implements Serializable {

	private static final long serialVersionUID = 1899185305638873302L;

	public String userGmailAccount;
	public String rpiGmailAccount;
	public String rpiGmailPassword;
	public String applicationName;
	public String authorizationCode;

	/**
	 * Constructor {@code GoogleConfiguration}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public GoogleConfiguration() {

	}

	/**
	 * Method {@code getUserGmailAccount} return the userGmailAccount.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the userGmailAccount
	 */
	public String getUserGmailAccount() {
		return userGmailAccount;
	}

	/**
	 * Method {@code setUserGmailAccount} set the userGmailAccount.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param userGmailAccount
	 *            the userGmailAccount to set
	 */
	public void setUserGmailAccount(String userGmailAccount) {
		this.userGmailAccount = userGmailAccount;
	}

	/**
	 * Method {@code getRpiGmailAccount} return the rpiGmailAccount.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the rpiGmailAccount
	 */
	public String getRpiGmailAccount() {
		return rpiGmailAccount;
	}

	/**
	 * Method {@code setRpiGmailAccount} set the rpiGmailAccount.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param rpiGmailAccount
	 *            the rpiGmailAccount to set
	 */
	public void setRpiGmailAccount(String rpiGmailAccount) {
		this.rpiGmailAccount = rpiGmailAccount;
	}

	/**
	 * Method {@code getRpiGmailPassword} return the rpiGmailPassword.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the rpiGmailPassword
	 */
	public String getRpiGmailPassword() {
		return rpiGmailPassword;
	}

	/**
	 * Method {@code setRpiGmailPassword} set the rpiGmailPassword.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param rpiGmailPassword
	 *            the rpiGmailPassword to set
	 */
	public void setRpiGmailPassword(String rpiGmailPassword) {
		this.rpiGmailPassword = rpiGmailPassword;
	}

	/**
	 * Method {@code getApplicationName} return the applicationName.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Method {@code setApplicationName} set the applicationName.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param applicationName
	 *            the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Method {@code getAuthorizationCode} return the authorizationCode.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the authorizationCode
	 */
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	/**
	 * Method {@code setAuthorizationCode} set the authorizationCode.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param authorizationCode
	 *            the authorizationCode to set
	 */
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
}
