package com.edfx.rpi.app.social.google;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.social.master.Master;
import com.edfx.rpi.app.social.master.MasterProcessor;
import com.edfx.rpi.app.utils.config.Configuration;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.properties.PropertiesLoader;
import com.edfx.rpi.app.utils.properties.PropertiesLoader.Properties;
import com.edfx.rpi.app.utils.properties.UnableToLoadPropertiesException;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;

/**
 * Class {@code GoogleManager} is the core class which is used application wide
 * to use various services provided by Google API. These includes the
 * <ul>
 * <li>GMail</li>
 * <li>Google Drive API</li>
 * <li>URL Shortener API</li>
 * </ul>
 * It is also an {@link Observer} of {@link GmailClient}.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum GoogleManager implements Observer {

	INSTANCE;
	
	private final ApplicationStorageManager applicationStorageManager = ApplicationStorageManager.INSTANCE;
	private final GoogleServiceProvider googleServiceProvider = GoogleServiceProvider.INSTANCE;
	
	private final Logger logger = RpiLogger.getLogger(getClass());
	
	private GoogleConfiguration configuration;
	private GmailClient gmailClient;	
	
	private String[] tokens;
	
	/**
	 * Constructor {@code GoogleManager}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private GoogleManager() {
		
	}	
	
	/**
	 * Method {@code setConfiguration} set the configuration.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(GoogleConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Method {@code loadGoogleConfiguration} loads the
	 * {@link GoogleConfiguration} from the persistent file storage.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the GoogleConfiguration
	 * @see ApplicationStorageManager
	 */
	private GoogleConfiguration loadGoogleConfiguration() {
		return (GoogleConfiguration) applicationStorageManager.readGoogleConfig();
	}
	
	/**
	 * Method {@code validateGoogleConfiguration} validates the consistency of
	 * {@link GoogleConfiguration}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 * @return
	 */
	private boolean validateGoogleConfiguration(GoogleConfiguration configuration) {
		return StringUtils.isNotBlank(configuration.getApplicationName()) &
				StringUtils.isNotBlank(configuration.getRpiGmailAccount()) &
				StringUtils.isNotBlank(configuration.getRpiGmailPassword()) &
				StringUtils.isNotBlank(configuration.getUserGmailAccount());
	}
	
	/**
	 * Method {@code validateGoogleServiceCredential} validates the OAuth 2.0
	 * Google API {@link Credential} instance. Basically it calls the URL
	 * Shortener Service to shorten URL of <a
	 * href="http://google.com">Google</a>. If the {@link Credential} is invalid
	 * then it will throw Exception.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param credential
	 *            the Credential to be validated
	 * @return a string URL shorten by URL Shortener Service
	 * @throws IOException
	 */
	private String validateGoogleServiceCredential(Credential credential) throws IOException {
		return UrlshortenerClient.INSTANCE.shrotenUrl(googleServiceProvider.getUrlshortener(credential), "http://google.com");
	}
	
	/**
	 * Method {@code isConfigured} checks if the RPI Application is configured
	 * to use Google Services
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return true if it is configured
	 */
	public boolean isConfigured() {
		GoogleConfiguration configuration = loadGoogleConfiguration();
		
		if(Objects.isNull(configuration)) {
			return false;
		} else {
			if(!validateGoogleConfiguration(configuration)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Method {@code configureForFirstTime} is used to configure the RPI
	 * Application to use Google Services when the Application is launched first
	 * time or when it is reseted.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 *            the instance of Configuration
	 * @see Configuration
	 */
	public void configureForFirstTime(Configuration configuration) {
		java.util.Properties properties = PropertiesLoader.INSTANCE.getProperties(Properties.GOOGLE);
		
		if(Objects.isNull(properties)) {
			throw new UnableToLoadPropertiesException("Properties file cannot be loaded: " + Properties.GOOGLE.getFileName());
		}
		
		String appName = properties.getProperty("appName");
		
		GoogleConfiguration googleConfiguration = new GoogleConfiguration();
		googleConfiguration.setApplicationName(appName);
		googleConfiguration.setRpiGmailAccount(configuration.rpiGmailAccount);
		googleConfiguration.setRpiGmailPassword(configuration.rpiGmailPassword);
		googleConfiguration.setUserGmailAccount(configuration.userGmailAccount);
		
		setConfiguration(googleConfiguration);
		applicationStorageManager.writeGoogleConfig(googleConfiguration);	
		initialize(googleConfiguration);
	}
	
	/**
	 * Method {@code getAuthorizationUrl} loads the Authorization URL for Google
	 * Services from the Property File and returns it to the caller.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the Authorization URL of Google Services
	 */
	public String getAuthorizationUrl() {
		java.util.Properties properties = PropertiesLoader.INSTANCE.getProperties(Properties.GOOGLE);
		
		if(Objects.isNull(properties)) {
			throw new UnableToLoadPropertiesException("Properties file cannot be loaded: " + Properties.GOOGLE.getFileName());
		}
		
		String authUrl = properties.getProperty("authUrl");
		return authUrl;
	}
	
	/**
	 * Method {@code initialize} initializes the {@link GoogleManager} with the
	 * given {@link GoogleConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 *            is the instance of GoogleConfiguration
	 */
	public void initialize(GoogleConfiguration configuration) {
		if(Objects.isNull(configuration)) {
			configuration = loadGoogleConfiguration();
			setConfiguration(configuration);
		}
		
		gmailClient = new GmailClient(configuration);
		gmailClient.addObserver(this);				
	}
	
	/**
	 * Method {@code initialize} initializes the {@link GoogleManager} when it
	 * is already preconfigured.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void initialize() {
		initialize(null);
	}
	
	/**
	 * Method {@code update} is called when the observed object, in this case
	 * the instance {@link #gmailClient} of {@link GmailClient} is changed.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param observable
	 *            the observable object
	 * @param arguments
	 *            an argument passed to the notifyObservers method
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object arguments) {
		if(observable instanceof GmailClient) {
			tokens = (String[]) arguments;
		}
	}
		
	/**
	 * Method {@code getTokens} return the tokens.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the tokens
	 */
	public String[] getTokens() {
		return tokens;
	}

	/**
	 * Method {@code setTokens} set the tokens.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param tokens
	 *            the tokens to set
	 */
	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	/**
	 * Method {@code startMailReceiver} starts the mail receiver of
	 * {@link GmailClient}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void startMailReceiver() {
		gmailClient.startMailReceiver();
	}

	/**
	 * 
	 * Method {@code stopMailReceiver} stops the mail receiver of
	 * {@link GmailClient}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void stopMailReceiver() {
		gmailClient.stopMailReceiver();
	}	
	
	/**
	 * Method {@code initializeServiceProvider} initializes the
	 * {@link GoogleServiceProvider} with the given Authorization Code
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param authorizationCode
	 *            is Authorization Code which is needed to initialize the
	 *            GoogleServiceProvider
	 * @see GoogleServiceProvider
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void initializeServiceProvider(String authorizationCode) throws IOException, GeneralSecurityException {
		GoogleConfiguration configuration = loadGoogleConfiguration();
		configuration.setAuthorizationCode(authorizationCode);
		applicationStorageManager.writeGoogleConfig(configuration);
		setConfiguration(configuration);

		applicationStorageManager.deleteGoogleCredentialDir();

		googleServiceProvider.initialize(configuration.getApplicationName(), applicationStorageManager.getGoogleCredentialDir());
		Credential credential = googleServiceProvider.getCredential(authorizationCode, configuration.getRpiGmailAccount());

		logger.info("Validating Google.");
		String url = validateGoogleServiceCredential(credential);
		logger.info(url);
	}
	
	/**
	 * 
	 * Method {@code initializeServiceProvider} initializes the
	 * {@link GoogleServiceProvider} when the Application is already configured
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see GoogleServiceProvider
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void initializeServiceProvider() throws IOException, GeneralSecurityException {
		googleServiceProvider.initialize(configuration.getApplicationName(), applicationStorageManager.getGoogleCredentialDir());
		googleServiceProvider.getCredential(configuration.getAuthorizationCode(), configuration.getRpiGmailAccount());
	}
	
	/**
	 * Method {@code uploadImages} uploads images in Google Drive by
	 * {@link DriveClient}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param files
	 *            are the image files need to be uploaded
	 * @param albumName
	 *            is the name of the album where the images will be placed
	 * @return the URL of the album in which the images has been uploaded
	 * @see DriveClient
	 * @see GoogleServiceProvider
	 * @throws IOException
	 */
	public String uploadImages(File[] files, String albumName) throws IOException {
		Credential credential = googleServiceProvider.getCredential(configuration.getAuthorizationCode(), configuration.getRpiGmailAccount());
		Drive drive = googleServiceProvider.getDrive(credential);
		String link = DriveClient.INSTANCE.uploadImages(drive, files, albumName, getGoogleUserAccount());
		logger.info("Url of the album: " + link);

		return link;
	}
	
	/**
	 * Method {@code uploadVideo} uploads video in Google Drive by
	 * {@link DriveClient}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param video
	 *            is the video file needs to be uploaded
	 * @return the URL of the uploaded video
	 * @see DriveClient
	 * @see GoogleServiceProvider
	 * @throws IOException
	 */
	public String uploadVideo(File video) throws IOException {
		Credential credential = googleServiceProvider.getCredential(configuration.getAuthorizationCode(), configuration.getRpiGmailAccount());
		Drive drive = googleServiceProvider.getDrive(credential);
		String link = DriveClient.INSTANCE.uploadVideo(drive, video, getGoogleUserAccount());
		logger.info("Url of the album: " + link);

		return link;
	}
	
	/**
	 * Method {@code getShortenUrl} returns the short representation of the URL
	 * which has been passed to it by calling the {@link UrlshortenerClient}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param longUrl
	 *            is the URL to be shorten
	 * @return the short form of the URL
	 * @see UrlshortenerClient
	 * @see GoogleServiceProvider
	 * @throws IOException
	 */
	public String getShortenUrl(String longUrl) throws IOException {
		Credential credential = googleServiceProvider.getCredential(configuration.getAuthorizationCode(), configuration.getRpiGmailAccount());
		return UrlshortenerClient.INSTANCE.shrotenUrl(googleServiceProvider.getUrlshortener(credential), longUrl);
	}
	
	/**
	 * 
	 * Method {@code getGoogleUserAccount} returns the Google Account of the
	 * current Master from {@link MasterProcessor}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the username of the Google Account of current master
	 * @see MasterProcessor
	 * @see Master
	 */
	public String getGoogleUserAccount() {
		Master master = MasterProcessor.INSTANCE.getMaster();
		return master.getGoogleAccount();
	}
	
	/**
	 * Method {@code sendConfigurationMail} is used to send the Configurational
	 * Mail for Twitter and Google Authorization Tokens.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterAuthorizationUrl
	 *            is the Authorization URL for Twitter
	 * @param googleAuthorizationUrl
	 *            is the Authorization URL for Google
	 * @param rpiGoggleHandle
	 *            is the Google Handle who needs to be Authorize, in our case it
	 *            is the RPI's Google Account.
	 * @param rpiTwitterHandle
	 *            is the Twitter Handle who needs to be Authorize, in our case
	 *            it is the RPI's Twitter Account.
	 */
	public void sendConfigurationMail(String twitterAuthorizationUrl, String googleAuthorizationUrl, String rpiGoggleHandle, String rpiTwitterHandle) {
		StringBuilder messageBuilder = new StringBuilder("Hello,");
		messageBuilder.append("<br/>");
		messageBuilder.append("Please click this link to logout from twitter, if you are logged in currently: ");
		messageBuilder.append("https://twitter.com/logout");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Click this Url: ").append(twitterAuthorizationUrl).append(" to authorize your Rpi Home Buddy ").append(rpiTwitterHandle).append(" for Twitter services.");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Click this Url: ").append(googleAuthorizationUrl).append(" to authorize your Rpi Home Buddy ").append(rpiGoggleHandle).append(" for Google services.");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Send me the Twitter Authorization Pin and Google Authorization Pin by replying to this mail in the following format:");
		messageBuilder.append("<br/>");
		messageBuilder.append("Rpi Google Pin: your_pin");
		messageBuilder.append("<br/>");
		messageBuilder.append("Rpi Twitter Pin: your_pin");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Thank you.");

		gmailClient.sendMail("RPI Authorization for Google and Twitter", messageBuilder.toString());
	}
	
	/**
	 * 
	 * Method {@code sendConfigurationMailForGoogle} is used to send the
	 * Configurational Mail for Google Authorization Tokens.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param googleAuthorizationUrl
	 *            is the Authorization URL for Google
	 * @param rpiGoggleHandle
	 *            is the Google Handle who needs to be Authorize, in our case it
	 *            is the RPI's Google Account.
	 */
	public void sendConfigurationMailForGoogle(String googleAuthorizationUrl, String rpiGoggleHandle) {
		StringBuilder messageBuilder = new StringBuilder("Hello,");
		messageBuilder.append("<br/>");
		messageBuilder.append("Click this Url: ").append(googleAuthorizationUrl).append(" to authorize your Rpi Home Buddy ").append(rpiGoggleHandle).append(" for Google services.");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Send me the Google Authorization Pin by replying to this mail in the following format:");
		messageBuilder.append("<br/>");
		messageBuilder.append("Rpi Google Pin: your_pin");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Thank you.");
		
		gmailClient.sendMail("RPI Authorization for Google", messageBuilder.toString());
	}
	
	/**
	 * Method {@code sendConfigurationMailForTwitter} is used to send the
	 * Configurational Mail for Twitter Authorization Tokens.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterAuthorizationUrl
	 *            is the Authorization URL for Twitter
	 * @param rpiTwitterHandle
	 *            is the Twitter Handle who needs to be Authorize, in our case
	 *            it is the RPI's Twitter Account.
	 */
	public void sendConfigurationMailForTwitter(String twitterAuthorizationUrl, String rpiTwitterHandle) {
		StringBuilder messageBuilder = new StringBuilder("Hello,");
		messageBuilder.append("<br/>");
		messageBuilder.append("Please click this link to logout from twitter, if you are logged in currently: ");
		messageBuilder.append("https://twitter.com/logout");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");	
		messageBuilder.append("Click this Url: ").append(twitterAuthorizationUrl).append(" to authorize your Rpi Home Buddy ").append(rpiTwitterHandle).append(" for Twitter services.");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Send me the Twitter Authorization Pin by replying to this mail in the following format:");
		messageBuilder.append("<br/>");
		messageBuilder.append("Rpi Twitter Pin: your_pin");
		messageBuilder.append("<br/>");
		messageBuilder.append("<br/>");
		messageBuilder.append("Thank you.");
		
		gmailClient.sendMail("RPI Authorization for Twitter", messageBuilder.toString());
	}
	
	/**
	 * Method {@code sendDoFollowMail} is used to send mail to the given
	 * recipient by requesting him/her to follow his/her RPI Handle in Twitter
	 * when the Twitter Manager fails to send Direct Message to the recipient.
	 * This mail also contains the message which was failed.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 *            is the Direct Message which wasn't send
	 * @param recipient
	 *            is email address to whom the mail will be sent to
	 * @param rpiTwitterAccount
	 *            the Twitter Handle of RPI
	 */
	public void sendDoFollowMail(String message, String recipient, String rpiTwitterAccount) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Hello,");
		messageBuilder.append("<br/>");
		messageBuilder.append("Failed to send following message:");
		messageBuilder.append("<br/>");
		messageBuilder.append(message);
		messageBuilder.append("<br/>");
		messageBuilder.append("Please follow ").append(rpiTwitterAccount).append(" in twitter.");
		messageBuilder.append("<br/>");
		messageBuilder.append("Thank you.");
		gmailClient.sendMail("RPI Communication Error", messageBuilder.toString(), recipient);
	}
}
