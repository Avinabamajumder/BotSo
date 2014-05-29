package com.edfx.rpi.app.service.application;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.ConfigurationException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.social.Communicator;
import com.edfx.rpi.app.social.google.GoogleManager;
import com.edfx.rpi.app.social.twitter.TwitterManager;
import com.edfx.rpi.app.utils.audio.AudioPlayer;
import com.edfx.rpi.app.utils.audio.media.Commons;
import com.edfx.rpi.app.utils.audio.media.Day;
import com.edfx.rpi.app.utils.audio.media.Hour;
import com.edfx.rpi.app.utils.audio.media.Media;
import com.edfx.rpi.app.utils.audio.media.Minute;
import com.edfx.rpi.app.utils.audio.media.Month;
import com.edfx.rpi.app.utils.audio.media.Period;
import com.edfx.rpi.app.utils.audio.media.Year;
import com.edfx.rpi.app.utils.config.Configuration;
import com.edfx.rpi.app.utils.config.ConfigurationManager;
import com.edfx.rpi.app.utils.config.WifiConfiguration;
import com.edfx.rpi.app.utils.config.speaker.ConfigurationExceptionSpeaker;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.tasks.BlinkLedTask;
import com.edfx.rpi.app.utils.tasks.ConnectivityCheckingTask;
import com.edfx.rpi.app.utils.tasks.ShutdownTask;
import com.edfx.rpi.app.utils.tasks.WifiConfigureTask;
import com.edfx.rpi.app.utils.thread.RpiThreadFactory;

/**
 * Class {@code RpiServletContextListener} is responsible for the
 * <ul>
 * <li>Initialization of the components</li>
 * <li>Initialization of the Social Connections</li>
 * <li>Resource and Services allocation</li>
 * </ul>
 * at the time when the context is initialized. It also validates the
 * Configuration which is required for the communication between RPI and its
 * Master. <br/>
 * When the context is destroyed it freed up the Services and the Resources.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
@WebListener
public class RpiServletContextListener implements ServletContextListener {

	private final Logger logger = RpiLogger.getLogger(getClass());

	private final RpiThreadFactory rpiThreadFactory = RpiThreadFactory.INSTANCE;
	private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(rpiThreadFactory);
	private final ConfigurationManager configurationManager = ConfigurationManager.INSTANCE;
	private final TwitterManager twitterManager = TwitterManager.INSTANCE;
	private final GoogleManager googleManager = GoogleManager.INSTANCE;
	private final Communicator communicator = Communicator.INSTANCE;
	private final ConnectivityCheckingTask connectivityCheckingTask = ConnectivityCheckingTask.INSTANCE;
	private final BlinkLedTask blinkLedTask = BlinkLedTask.INSTANCE;
	private final AudioPlayer audioPlayer = AudioPlayer.INSTANCE;

	/**
	 * 
	 * Constructor {@code RpiServletContextListener}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public RpiServletContextListener() {

	}

	/**
	 * Method {@code contextInitialized} performs the initialization of RPI.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param event
	 *            the ServletContextEvent containing the ServletContext that is
	 *            being initialized
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("RPI initialization started...");

		boolean computerIsConnectedToNetwork = connectivityCheckingTask.isConnectedToNetworkWithSpeech();
		logger.info("Network Status: " + (computerIsConnectedToNetwork ? "Connected." : "Not connected."));

		// service.scheduleWithFixedDelay(connectivityCheckingTask, 0, 10,
		// TimeUnit.SECONDS);

		initialize(computerIsConnectedToNetwork);
		logger.info("RPI initialization completed...");
	}

	/**
	 * 
	 * Method {@code contextDestroyed} performs the clean up of the resources
	 * and shutdown various {@link ExecutorService} instances.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param event
	 *            the ServletContextEvent containing the ServletContext that is
	 *            being initialized
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("RPI shutdown process started...");

		if (!service.isTerminated()) {
			service.shutdownNow();
		}

		connectivityCheckingTask.shutdown();
		twitterManager.shutdown();
		communicator.stopCommunication();

		logger.info("RPI shutdown process completed...");
	}

	/**
	 * Method {@code initialize} awaits at the time of initialization until the
	 * RPI is connected to the network. Then it calls {@link #initialize()}
	 * method to perform the other tasks.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param computerIsConnectedToNetwork
	 *            tells if RPI is connected to network or not
	 */
	private void initialize(final boolean computerIsConnectedToNetwork) {
		if (!computerIsConnectedToNetwork) {
			rpiThreadFactory.newThread(() -> {
				while (true) {
					boolean isConnected = connectivityCheckingTask.isConnected();

					if (isConnected) {
						break;
					}
				}

				initialize();
			}).start();
		} else {
			initialize();
		}
	}

	/**
	 * Method {@code initialize} performs the actual initialization. It checks
	 * the RPI needs to be reset. If it is need to be reset the reset it. <br/>
	 * Otherwise checks if the Social Media is configured or not. If it is not
	 * configured then configures it. <br/>
	 * Else initialize the Social Media components viz, Google and Twitter. <br/>
	 * It also sounds appropriate messages during the initialization or
	 * configuration.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void initialize() {
		logger.info("Checking Application configuration...");
		
		checkAndConfigureWifi();

		if (configurationManager.checkReset()) {
			logger.info("Resetting configuration.");
			configurationManager.reset();
		}

		try {
			boolean twitterConfigured = twitterManager.isConfigured();
			boolean googleConfigured = googleManager.isConfigured();

			if (twitterConfigured & googleConfigured) {
				logger.info("Google manager is configured.");
				logger.info("Twitter manager is configured.");

				googleManager.initialize();
				googleManager.initializeServiceProvider();

				twitterManager.initialize();

				communicator.startCommunication();

				speakAwake();
			} else {
				speakNotConfigured();

				logger.info("Google manager is not configured.");
				logger.info("Twitter manager is not configured.");

				Configuration configuration = getConfiguration();
				configurationManager.deleteConfigFile();

				blinkLedTask.setFrequency(new String[] { "2", "2", "4" });
				blinkLedTask.run(true);
				Thread blinkingLedThread = rpiThreadFactory.newThread(blinkLedTask);
				blinkingLedThread.start();

				googleManager.configureForFirstTime(configuration);

				String googleAuthenticationUrl = googleManager.getAuthorizationUrl();
				String twitterAuthenticationUrl = twitterManager.getAuthorizationUrl();

				final String rpiGoggleHandle = configuration.rpiGmailAccount;
				final String rpiTwitterHandle = configuration.rpiTwitterAccount;

				googleManager.sendConfigurationMail(twitterAuthenticationUrl, googleAuthenticationUrl, rpiGoggleHandle, rpiTwitterHandle);
				googleManager.startMailReceiver();

				final ScheduledExecutorService awaitingAuthPinService = Executors.newSingleThreadScheduledExecutor();

				final AtomicBoolean googleConfiguredFirstTime = new AtomicBoolean(false);
				final AtomicBoolean twitterConfiguredFirstTime = new AtomicBoolean(false);

				awaitingAuthPinService.scheduleWithFixedDelay(() -> {
					logger.info("Waiting for Authorization Pins...");

					if (!blinkLedTask.isRunning()) {
						blinkLedTask.setFrequency(new String[] { "2", "2", "4" });
						blinkLedTask.run(true);
						Thread blinkingLedThreadInner = rpiThreadFactory.newThread(blinkLedTask);
						blinkingLedThreadInner.start();
					}

					try {
						String[] tokens = googleManager.getTokens();

						if (Objects.nonNull(tokens)) {
							logger.info("Pin found.");
							String googleToken = tokens[0];
							String twitterToken = tokens[1];

							if (StringUtils.isNotBlank(googleToken)) {
								logger.info("Google Pin Received: " + googleToken);
								logger.info("Configuring Google for the first time.");

								try {
									googleManager.initializeServiceProvider(googleToken);
									googleConfiguredFirstTime.set(true);
								} catch (Throwable cause) {
									logger.error(cause);
								}
							}

							if (StringUtils.isNotBlank(twitterToken)) {
								logger.info("Twitter Pin Received: " + twitterToken);
								logger.info("Configuring Twitter for the first time.");

								try {
									twitterManager.configureForFirstTime(configuration, twitterToken);
									twitterConfiguredFirstTime.set(true);
								} catch (Throwable cause) {
									logger.error(cause);
								}
							}

							if (twitterConfiguredFirstTime.get() && googleConfiguredFirstTime.get()) {
								try {
									logger.info("Application configured to use Google and Twitter services.");
									googleManager.stopMailReceiver();
									awaitingAuthPinService.shutdown();

									blinkLedTask.run(false);

									logger.info("Configuration done.");
									speakConfigured();
									speakAwake();
								} catch (Throwable ignore) {
								}
							} else {
								if (!googleConfiguredFirstTime.get()) {
									logger.info("Google configuration failed. Unable to authorize token: " + googleToken);
									String googleReAuthenticationUrl = googleManager.getAuthorizationUrl();
									googleManager.sendConfigurationMailForGoogle(googleReAuthenticationUrl, rpiGoggleHandle);
								} else if (!twitterConfiguredFirstTime.get()) {
									logger.info("Twitter configuration failed. Unable to authorize token: " + twitterToken);
									String twitterReAuthenticationUrl = twitterManager.getAuthorizationUrl();
									googleManager.sendConfigurationMailForTwitter(twitterReAuthenticationUrl, rpiTwitterHandle);
								} else {
									logger.info("Google configuration failed. Unable to authorize token: " + googleToken);
									logger.info("Twitter configuration failed. Unable to authorize token: " + twitterToken);
									String googleReAuthenticationUrl = googleManager.getAuthorizationUrl();
									String twitterReAuthenticationUrl = twitterManager.getAuthorizationUrl();

									googleManager.sendConfigurationMail(googleReAuthenticationUrl, twitterReAuthenticationUrl, rpiGoggleHandle, rpiTwitterHandle);
								}
							}

							googleManager.setTokens(null);
						} else {
							logger.info("No pin found yet.");
						}
					} catch (Throwable cause) {
						logger.error(cause.getMessage(), cause);
					}
				}, 0, 10, TimeUnit.SECONDS);
			}
		} catch (Throwable cause) {
			logger.error(cause.getMessage(), cause);
		}
	}

	/**
	 * Method {@code checkAndConfigureWifi}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void checkAndConfigureWifi() {		
		rpiThreadFactory.newThread(()->{
			WifiConfiguration wifiConfiguration = configurationManager.getWifiConfiguration();
			
			if(Objects.nonNull(wifiConfiguration)) {
				WifiConfigureTask.INSTANCE.configure(wifiConfiguration);
				configurationManager.deleteWifiConfigFile();
			}
		}).start();
	}

	/**
	 * Method {@code speakConfigured} speaks the configuration successful
	 * message.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void speakConfigured() {
		rpiThreadFactory.newThread(() -> {
			audioPlayer.play(Commons.CONFIGURATION_SUCCESSFULL);
		}).start();
	}

	/**
	 * Method {@code speakAwake} speaks the awake message.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void speakAwake() {
		rpiThreadFactory.newThread(() -> {
			List<Media> medias = new ArrayList<>();
			medias.add(Commons.AWAKE);
			medias.add(Commons.CURRENT_DATE_TIME);

			LocalDateTime now = LocalDateTime.now();

			int dayOfMonth = now.get(ChronoField.DAY_OF_MONTH);
			Day day = Day.of(dayOfMonth);
			medias.add(day);

			int monthOfYear = now.get(ChronoField.MONTH_OF_YEAR);
			Month month = Month.of(monthOfYear);
			medias.add(month);

			int year = now.get(ChronoField.YEAR);
			medias.add(Year.of(year));

			int hourOfDay = now.get(ChronoField.CLOCK_HOUR_OF_AMPM);
			Hour hour = Hour.of(hourOfDay);
			medias.add(hour);

			int minuteOfHour = now.get(ChronoField.MINUTE_OF_HOUR);
			Minute minute = Minute.of(minuteOfHour);
			medias.add(minute);

			int ampmOfDay = now.get(ChronoField.AMPM_OF_DAY);
			Period period = Period.of(ampmOfDay);
			medias.add(period);

			audioPlayer.play(medias, 100);
		}).start();
	}

	/**
	 * Method {@code speakNotConfigured} speaks the not configured message.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void speakNotConfigured() {
		rpiThreadFactory.newThread(() -> {
			audioPlayer.play(Commons.NOT_CONFIGURED);
		}).start();
	}

	/**
	 * Method {@code getConfiguration} retrieves the {@link Configuration} from
	 * {@link ConfigurationManager}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the Configuration instance of the RPI Application
	 * @see Configuration
	 * @see ConfigurationManager
	 */
	private Configuration getConfiguration() {
		Configuration configuration = null;

		try {
			configuration = configurationManager.getConfiguration();
		} catch (Throwable cause) {
			handleConfigurationException(cause);
		}

		return configuration;
	}

	/**
	 * Method {@code handleConfigurationException} handles the
	 * {@link ConfigurationException}. It delegates this exception instance to
	 * the {@link ConfigurationExceptionSpeaker}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param cause
	 *            is the instance of various types of ConfigurationException
	 * @see ConfigurationException
	 */
	private void handleConfigurationException(Throwable cause) {
		ConfigurationExceptionSpeaker.INSTANCE.speak(cause);
		abort();
	}

	/**
	 * Method {@code abort} execute a {@link Process} which shutdown the running
	 * server.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void abort() {
		ShutdownTask.INSTANCE.shutDown(false);
	}
}