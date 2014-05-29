package com.edfx.rpi.app.utils.config;

/**
 * Class {@code Configuration}
 * @author Tapas Bose
 * @since RPI V1.0
 */
public final class Configuration {
	public final String userGmailAccount;
	public final String rpiGmailAccount;
	public final String rpiGmailPassword;
	public final String userTwitterAccount;
	public final String rpiTwitterAccount;
	
	/**
	 * Constructor {@code Configuration}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param userGmailAccount
	 * @param rpiGmailAccount
	 * @param rpiGmailPassword
	 * @param userTwitterAccount
	 * @param rpiTwitterAccount
	 */
	Configuration(String userGmailAccount, String rpiGmailAccount, String rpiGmailPassword, String userTwitterAccount, String rpiTwitterAccount) {
		this.userGmailAccount = userGmailAccount;
		this.rpiGmailAccount = rpiGmailAccount;
		this.rpiGmailPassword = rpiGmailPassword;
		this.userTwitterAccount=userTwitterAccount;
		this.rpiTwitterAccount=rpiTwitterAccount;
	}		
}