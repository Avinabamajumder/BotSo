package com.edfx.rpi.app.social.twitter;

import org.apache.log4j.Logger;

import twitter4j.ConnectionLifeCycleListener;

import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.tasks.BlinkLedTask;
import com.edfx.rpi.app.utils.tasks.ConnectivityCheckingTask;
import com.edfx.rpi.app.utils.thread.RpiThreadFactory;

/**
 * Class {@code ConnectionListener}
 * @author Tapas Bose
 * @since RPI V1.0
 */
final class ConnectionListener implements ConnectionLifeCycleListener {
	private final Logger logger = RpiLogger.getLogger(getClass());
	private final BlinkLedTask blinkLedTask = BlinkLedTask.INSTANCE;

	/**
	 * Method {@code onConnect}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see twitter4j.ConnectionLifeCycleListener#onConnect()
	 */
	@Override
	public void onConnect() {
		logger.info("Twitter connected.");

		if (blinkLedTask.isRunning()) {
			blinkLedTask.run(false);
		}
	}

	/**
	 * Method {@code onDisconnect}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see twitter4j.ConnectionLifeCycleListener#onDisconnect()
	 */
	@Override
	public void onDisconnect() {
		logger.info("Twitter disconnected.");
		boolean computerIsConnectedToNetwork = ConnectivityCheckingTask.INSTANCE.isConnectedToNetworkWithSpeech();

		if (!computerIsConnectedToNetwork) {
			blinkLedTask.setFrequency(new String[] { ".5", ".5", "4" });
			blinkLedTask.run(true);

			RpiThreadFactory.INSTANCE.newThread(blinkLedTask).start();
		}
	}

	/**
	 * Method {@code onCleanUp}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see twitter4j.ConnectionLifeCycleListener#onCleanUp()
	 */
	@Override
	public void onCleanUp() {

	}	
}