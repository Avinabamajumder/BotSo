package com.edfx.rpi.app.social.twitter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.edfx.rpi.app.social.google.GoogleManager;
import com.edfx.rpi.app.social.master.Master;
import com.edfx.rpi.app.social.master.MasterProcessor;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.properties.PropertiesLoader;
import com.edfx.rpi.app.utils.properties.PropertiesLoader.Properties;
import com.edfx.rpi.app.utils.properties.UnableToLoadPropertiesException;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;
import com.edfx.rpi.app.utils.thread.RpiThreadFactory;

/**
 * Class {@code TwitterManager}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum TwitterManager {
	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());
	private final ApplicationStorageManager applicationStorageManager = ApplicationStorageManager.INSTANCE;
	private final RpiThreadFactory rpiThreadFactory = RpiThreadFactory.INSTANCE;
	private final MasterProcessor masterProcessor = MasterProcessor.INSTANCE;

	private Twitter twitter;
	private TwitterStream twitterStream;
	private TwitterConfiguration configuration;
	private DirectMessageListener directMessageListener;

	/**
	 * Class {@code TwitterManagerConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private enum TwitterManagerConfiguration {
		CONSUMER_KEY("consumerKey", "Consumer Key is needed."), 
		CONSUMER_SECRET("consumerSecret", "Consumer Secret is needed.");

		private String key;
		private String message;

		/**
		 * Constructor {@code TwitterManagerConfiguration}
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @param key
		 * @param message
		 */
		private TwitterManagerConfiguration(String key, String message) {
			this.key = key;
			this.message = message;
		}
	}

	/**
	 * Constructor {@code TwitterManager}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private TwitterManager() {

	}

	/**
	 * Method {@code loadTwitterConfiguration} loads
	 * {@link TwitterConfiguration} from storage
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of TwitterConfiguration
	 * @see ApplicationStorageManager
	 */
	private TwitterConfiguration loadTwitterConfiguration() {
		return (TwitterConfiguration) applicationStorageManager.readTwitterConfig();
	}

	/**
	 * Method {@code getConfigurationProperties} returns a value of the twitter
	 * configuration for the given {@link TwitterManagerConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param properties
	 * @param configuration
	 *            an instance of TwitterManagerConfiguration
	 * @return the value of the configuration
	 */
	private static String getConfigurationProperties(java.util.Properties properties, TwitterManagerConfiguration configuration) {
		String value = properties.getProperty(configuration.key);

		if (StringUtils.isBlank(value)) {
			throw new TwitterConfigurationException(configuration.message);
		}

		return value;
	}

	/**
	 * Method {@code setConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 *            to set
	 */
	private void setConfiguration(TwitterConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Method {@code validateTwitterConfiguration} validates the
	 * {@link TwitterConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 * @return
	 */
	private boolean validateTwitterConfiguration(TwitterConfiguration configuration) {
		return StringUtils.isNotBlank(configuration.getAccessToken()) & 
				StringUtils.isNotBlank(configuration.getAccessTokenSecret()) & 
				StringUtils.isNotBlank(configuration.getConsumerKey()) & 
				StringUtils.isNotBlank(configuration.getConsumerSecret()) & StringUtils.isNotBlank(configuration.getUserAccount()) & StringUtils.isNotBlank(configuration.getRpiAccount());
	}

	/**
	 * Method {@code getCurrentDateAndTime}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	private String getCurrentDateAndTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MMMM/yyyy hh:mm:ss a");
		return formatter.format(new Date());
	}

	/**
	 * Method {@code getAuthorizationUrl} generates and returns the
	 * Authorization URL of the Twitter
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the Authorization URL
	 * @throws TwitterException
	 */
	public String getAuthorizationUrl() throws TwitterException {
		java.util.Properties properties = PropertiesLoader.INSTANCE.getProperties(Properties.TWITTER);

		if (Objects.isNull(properties)) {
			throw new UnableToLoadPropertiesException("Properties file cannot be loaded: " + Properties.TWITTER.getFileName());
		}

		String consumerKey = getConfigurationProperties(properties, TwitterManagerConfiguration.CONSUMER_KEY);
		String consumerSecret = getConfigurationProperties(properties, TwitterManagerConfiguration.CONSUMER_SECRET);

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(consumerKey);
		configurationBuilder.setOAuthConsumerSecret(consumerSecret);

		Configuration configuration = configurationBuilder.build();
		TwitterFactory twitterFactory = new TwitterFactory(configuration);
		Twitter twitter = twitterFactory.getInstance();
		this.twitter = twitter;

		RequestToken requestToken = twitter.getOAuthRequestToken();

		applicationStorageManager.writeRequestToken(requestToken);

		return requestToken.getAuthenticationURL();
	}

	/**
	 * Method {@code sendDirectMessage} sends a direct message with the given
	 * message to the given user
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param screenName
	 *            the name of user to send
	 * @param message
	 *            the message to send
	 */
	public void sendDirectMessage(String screenName, String message) {
		sendDirectMessage(screenName, message, false);
	}

	/**
	 * Method {@code sendDirectMessage} sends a direct message with the given
	 * message to the given user. The the message is sent to a user who don't
	 * follow this user then the parameter {@code poll} needs to be set to true.
	 * It will starts a polling to check if the first user follows this user.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param screenName
	 *            the name of user to send
	 * @param message
	 *            the message to send
	 * @param poll
	 *            whether to poll or not
	 */
	private void sendDirectMessage(String screenName, String message, boolean poll) {
		try {
			final Master master = StringUtils.isBlank(screenName) ? masterProcessor.getMaster() : masterProcessor.getMaster(screenName);
			final String masterScreenName = StringUtils.isBlank(screenName) ? master.getTwitterAccount() : screenName;
			final String botso = getConfiguration().getRpiAccount();

			MasterBotsoRelationship relationship = getRelationship(masterScreenName, botso);

			if (Objects.nonNull(relationship)) {
				boolean canDMBothWay = relationship.masterFollowBotso & relationship.botsoFollowMaster;

				if (canDMBothWay) {
					logger.info("Sending direct message to: " + masterScreenName + ". Message is: " + message);
					twitter.sendDirectMessage(masterScreenName, message);
				} else {
					if (!relationship.botsoFollowMaster) {
						try {
							twitter.createFriendship(getMasterScreenName());
						} catch (Throwable cause) {
							logger.error("Unable to create friendship. Reason is: " + cause.getMessage(), cause);
						}
					}

					if (!relationship.masterFollowBotso) {
						GoogleManager.INSTANCE.sendDoFollowMail(message, master.getGoogleAccount(), botso);

						if (poll) {
							ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(rpiThreadFactory);
							service.scheduleWithFixedDelay(() -> {
								try {
									MasterBotsoRelationship friendShip = getRelationship(masterScreenName, botso);

									if (friendShip.masterFollowBotso) {
										twitter.sendDirectMessage(masterScreenName, message);
										service.shutdown();
									}
								} catch (Throwable cause) {
									logger.error(cause.getMessage(), cause);
								}
							}, 0, 15, TimeUnit.SECONDS);
						}
					}
				}
			}
		} catch (Throwable cause) {
			logger.error("Unable to send direct message. Reason is: " + cause.getMessage(), cause);
		}
	}

	/**
	 * Method {@code sendDirectMessageOnInit} sends direct message to master on
	 * component initialization
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 *            the message to send
	 */
	public void sendDirectMessageOnInit(String message) {
		sendDirectMessage(null, message, true);
	}

	/**
	 * Method {@code sendDirectMessageOnChangeMaster} sends direct message on
	 * master change
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param primaryMaster
	 *            screen name of primary master
	 * @param secondaryMaster
	 *            screen name of secondary master
	 * @param primaryMasterMessage
	 *            message to send to primary master
	 * @param secondaryMasterMessage
	 *            message to send to secondary master
	 */
	public void sendDirectMessageOnChangeMaster(String primaryMaster, String secondaryMaster, String primaryMasterMessage, String secondaryMasterMessage) {
		try {
			sendDirectMessage(primaryMaster, primaryMasterMessage, false);

			final String botso = getConfiguration().getRpiAccount();

			MasterBotsoRelationship relationship = getRelationship(secondaryMaster, botso);

			if (Objects.nonNull(relationship)) {
				boolean canDMBothWay = relationship.masterFollowBotso & relationship.botsoFollowMaster;

				if (canDMBothWay) {
					logger.info("Sending direct message to: " + secondaryMaster + ". Message is: " + secondaryMasterMessage);
					twitter.sendDirectMessage(secondaryMaster, secondaryMasterMessage);
				} else {
					if (!relationship.botsoFollowMaster) {
						try {
							twitter.createFriendship(secondaryMaster);
						} catch (Throwable cause) {
							logger.error("Unable to create friendship. Reason is: " + cause.getMessage(), cause);
						}
					}

					if (!relationship.masterFollowBotso) {
						sendDirectMessage(primaryMaster, "Please ask " + secondaryMaster + " to follow me.", false);

						ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(rpiThreadFactory);
						service.scheduleWithFixedDelay(() -> {
							try {
								MasterBotsoRelationship friendShip = getRelationship(secondaryMaster, botso);

								if (friendShip.masterFollowBotso) {
									twitter.sendDirectMessage(secondaryMaster, secondaryMasterMessage);
									service.shutdown();
								}
							} catch (Throwable cause) {
								logger.error(cause.getMessage(), cause);
							}
						}, 0, 30, TimeUnit.SECONDS);
					}
				}
			}
		} catch (Throwable cause) {

		}
	}

	/**
	 * Method {@code sendDirectMessage} sends direct message
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 *            the message to send
	 */
	public void sendDirectMessage(String message) {
		sendDirectMessage(getMasterScreenName(), message);
	}

	/**
	 * Method {@code sendConfiguredMessage} sends direct message if configured
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void sendConfiguredMessage() {
		sendDirectMessageOnInit("I am configured.");
	}

	/**
	 * Method {@code sendWakeUpMessage} sends direct message when wakes up
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void sendWakeUpMessage() {
		String message = "I am awake. Current date and time is: " + getCurrentDateAndTime();
		sendDirectMessageOnInit(message);
	}

	/**
	 * Method {@code sendGoingToSleepMessage} sends direct message when goes to
	 * sleep
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void sendGoingToSleepMessage() {
		sendDirectMessage(getMasterScreenName(), "Going off to sleep.");
	}

	/**
	 * Method {@code getMasterScreenName} returns the current master's screen
	 * name
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	private String getMasterScreenName() {
		Master master = masterProcessor.getMaster();
		return master.getTwitterAccount();
	}

	/**
	 * Method {@code shutdown} shutdown the {@link TwitterManager}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void shutdown() {
		logger.info("Shutting down Twitter manager...");

		try {
			sendGoingToSleepMessage();
			twitterStream.shutdown();
		} catch (Throwable ignore) {

		}
	}

	/**
	 * Method {@code isConfigured} checks if {@link TwitterManager} is
	 * configured or not
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return true if configured
	 */
	public boolean isConfigured() {
		TwitterConfiguration configuration = loadTwitterConfiguration();

		if (Objects.isNull(configuration)) {
			return false;
		} else {
			if (!validateTwitterConfiguration(configuration)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Method {@code configureForFirstTime} configures {@link TwitterManager}
	 * for the first time use
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 *            the instance of the Configuration
	 * @param twitterPin
	 *            the Twitter Authorization PIN
	 * @see com.edfx.rpi.app.utils.config.Configuration
	 * @throws TwitterConfigurationException
	 */
	public void configureForFirstTime(com.edfx.rpi.app.utils.config.Configuration configuration, String twitterPin) throws TwitterConfigurationException {
		java.util.Properties properties = PropertiesLoader.INSTANCE.getProperties(Properties.TWITTER);

		if (Objects.isNull(properties)) {
			throw new UnableToLoadPropertiesException("Properties file cannot be loaded: " + Properties.TWITTER.getFileName());
		}

		RequestToken requestToken = (RequestToken) ApplicationStorageManager.INSTANCE.readRequestToken();

		String consumerKey = getConfigurationProperties(properties, TwitterManagerConfiguration.CONSUMER_KEY);
		String consumerSecret = getConfigurationProperties(properties, TwitterManagerConfiguration.CONSUMER_SECRET);

		AccessToken accessToken = null;

		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, twitterPin);
		} catch (Throwable cause) {
			TwitterConfigurationException exception = new TwitterConfigurationException(cause);
			exception.initCause(cause);
			throw exception;
		}

		if (Objects.nonNull(accessToken)) {
			logger.info("Access token recived: " + accessToken.getToken());
			logger.info("Access token secret recieved: " + accessToken.getTokenSecret());

			applicationStorageManager.deleteRequestToken();

			TwitterConfiguration twitterConfiguration = new TwitterConfiguration();
			twitterConfiguration.setAccessToken(accessToken.getToken());
			twitterConfiguration.setAccessTokenSecret(accessToken.getTokenSecret());
			twitterConfiguration.setConsumerKey(consumerKey);
			twitterConfiguration.setConsumerSecret(consumerSecret);
			twitterConfiguration.setUserAccount(configuration.userTwitterAccount);
			twitterConfiguration.setRpiAccount(configuration.rpiTwitterAccount);

			applicationStorageManager.writeTwitterConfig(twitterConfiguration);
			initialize(twitterConfiguration);
			sendConfiguredMessage();
		}
	}

	/**
	 * Method {@code initialize} initializes the {@link TwitterManager} with the
	 * given {@link TwitterConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterConfiguration
	 *            an instance of TwitterConfiguration
	 * @see TwitterConfiguration
	 */
	public void initialize(TwitterConfiguration twitterConfiguration) {
		if (Objects.isNull(twitterConfiguration)) {
			twitterConfiguration = loadTwitterConfiguration();
			setConfiguration(configuration);
		}

		setConfiguration(twitterConfiguration);

		String consumerKey = twitterConfiguration.getConsumerKey();
		String consumerSecret = twitterConfiguration.getConsumerSecret();
		String accessToken = twitterConfiguration.getAccessToken();
		String accessTokenSecret = twitterConfiguration.getAccessTokenSecret();

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(consumerKey);
		configurationBuilder.setOAuthConsumerSecret(consumerSecret);
		configurationBuilder.setOAuthAccessToken(accessToken);
		configurationBuilder.setOAuthAccessTokenSecret(accessTokenSecret);

		Configuration configuration = configurationBuilder.build();
		TwitterFactory twitterFactory = new TwitterFactory(configuration);
		twitter = twitterFactory.getInstance();

		TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(configuration);
		twitterStream = twitterStreamFactory.getInstance();

		twitterStream.addConnectionLifeCycleListener(new ConnectionListener());

		twitterStream.addListener(directMessageListener = new DirectMessageListener(twitterConfiguration.getUserAccount()));
		twitterStream.user();

		Master secondaryMaster = MasterProcessor.INSTANCE.getSecondaryMaster();

		if (Objects.nonNull(secondaryMaster)) {
			directMessageListener.setSecondaryTwitterAccount(secondaryMaster.getTwitterAccount());
		}

		sendWakeUpMessage();
	}

	/**
	 * Method {@code initialize} initializes the {@link TwitterManager}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void initialize() {
		initialize(null);
	}

	/**
	 * Method {@code getConfiguration} returns the instance of
	 * {@link TwitterConfiguration}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of TwitterConfiguration
	 */
	public TwitterConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Method {@code setSecondaryTwitterAccount} sets the secondary master's
	 * account
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param newHander
	 *            the secondary master's twitter handler
	 */
	public void setSecondaryTwitterAccount(String newHander) {
		directMessageListener.setSecondaryTwitterAccount(newHander);
	}

	/**
	 * Method {@code getRelationship} returns the relationship between the
	 * matser and the botso
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param master
	 *            the master's twitter screen name
	 * @param botso
	 *            the bosto's twitter screen name
	 * @return an instance of MasterBotsoRelationship
	 * @see MasterBotsoRelationship
	 */
	public MasterBotsoRelationship getRelationship(String master, String botso) {
		try {
			Relationship relationship = twitter.showFriendship(master, botso);
			MasterBotsoRelationship masterBotsoRelationship = new MasterBotsoRelationship(relationship.isTargetFollowedBySource(), relationship.isTargetFollowingSource());
			return masterBotsoRelationship;
		} catch (Throwable cause) {
			logger.error(cause.getMessage(), cause);
		}

		return null;
	}

	/**
	 * Method {@code validate} checks if the given screen name is a valid
	 * Twitter Screen name
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param screenName
	 *            screen name to check
	 * @return true is valid
	 */
	public boolean validate(String screenName) {
		try {
			User user = twitter.showUser(screenName);

			if (Objects.nonNull(user)) {
				return StringUtils.endsWithIgnoreCase(user.getScreenName(), screenName);
			}

		} catch (Throwable ignore) {
		}

		return false;
	}
}
