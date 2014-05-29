package com.edfx.rpi.app.utils.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.audio.AudioPlayer;
import com.edfx.rpi.app.utils.audio.media.Commons;
import com.edfx.rpi.app.utils.common.StreamUtils;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.thread.RpiThreadFactory;

/**
 * Class {@code ConnectivityCheckingTask} performs the tasks necessary to check
 * Internet connectivity
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum ConnectivityCheckingTask implements Runnable {
	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(ConnectivityCheckingTask.class);

	private final AudioPlayer audioPlayer = AudioPlayer.INSTANCE;
	private final AtomicBoolean connected = new AtomicBoolean(true);
	private final AtomicBoolean lastConnectivityStatus = new AtomicBoolean(true);
	private final AtomicBoolean said = new AtomicBoolean(false);
	private final Lock lock = new ReentrantLock();
	private final BlinkLedTask blinkLedTask = BlinkLedTask.INSTANCE;

	private final String google = "www.google.com";
	private final String twitter = "www.twitter.com";
	private final String googlePublicDns = "8.8.8.8";

	private final Condition condition = lock.newCondition();
	private volatile boolean tested = false;

	/**
	 * Constructor {@code ConnectivityCheckingTask}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private ConnectivityCheckingTask() {

	}

	private Thread blinkingLedThread;

	/**
	 * Method {@code run}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		lock.lock();

		try {
			logger.info("Performing Scheduled Network Test.");
			boolean computerIsConnectedToNetwork = isConnectedToNetwork();
			connected.set(computerIsConnectedToNetwork);

			if (!computerIsConnectedToNetwork) {
				try {
					audioPlayer.play(Commons.INTERNET_NOT_CONNECTED);
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}
			} else {
				try {
					if (!said.get()) {
						audioPlayer.play(Commons.NETWORK_CONNECTED);
						said.set(true);
					}
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}
			}

			boolean previouslyConnected = lastConnectivityStatus.get();

			if (!previouslyConnected & computerIsConnectedToNetwork) {
				try {
					audioPlayer.play(Commons.INTERNET_CONNECTED);
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}

			}

			lastConnectivityStatus.set(computerIsConnectedToNetwork);

			if (!computerIsConnectedToNetwork) {
				logger.info("Network Status: Not connected.");
			}

			try {
				if (Objects.nonNull(blinkingLedThread)) {
					if (connected.get()) {
						blinkLedTask.run(false);
						blinkingLedThread = null;
					}
				} else {
					if (!connected.get()) {
						blinkLedTask.setFrequency(new String[] { ".5", ".5", "4" });
						blinkLedTask.run(true);
						blinkingLedThread = RpiThreadFactory.INSTANCE.newThread(blinkLedTask);
						blinkingLedThread.start();
					}
				}
			} catch (Throwable cause) {
				logger.error(cause.getMessage(), cause);
			}

			tested = true;
			condition.signalAll();
		} finally {
			lock.unlock();
		}

	}

	/**
	 * Method {@code isConnected}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	public boolean isConnected() {
		lock.lock();

		try {
			while (!tested) {
				condition.await();
			}
		} catch (InterruptedException cause) {
			logger.error(cause);
		} finally {
			lock.unlock();
		}

		return connected.get();
	}

	/**
	 * Method {@code isConnectedToNetworkWithSpeech} checks if RPI is connected
	 * to the Internet and speaks appropriate messages
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return true is connected
	 */
	public boolean isConnectedToNetworkWithSpeech() {
		lock.lock();
		boolean computerIsConnectedToNetwork = false;

		try {
			try {
				String[] command = new String[] { "fping", twitter };
				ProcessBuilder processBuilder = new ProcessBuilder(command);
				Process process = processBuilder.start();

				StringBuilder outputBuilder = new StringBuilder();
				InputStream inputStream = process.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				String line = StringUtils.EMPTY;

				while ((line = bufferedReader.readLine()) != null) {
					outputBuilder.append(line);
					outputBuilder.append("\n");
				}

				process.waitFor();
				process.destroy();

				computerIsConnectedToNetwork = StringUtils.containsIgnoreCase(outputBuilder, "alive");
			} catch (Throwable cause) {

			}

			connected.set(computerIsConnectedToNetwork);

			if (!computerIsConnectedToNetwork) {
				try {
					audioPlayer.play(Commons.INTERNET_NOT_CONNECTED);
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}
			} else {
				try {
					if (!said.get()) {
						audioPlayer.play(Commons.NETWORK_CONNECTED);
						said.set(true);
					}
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}
			}

			boolean previouslyConnected = lastConnectivityStatus.get();

			if (!previouslyConnected & computerIsConnectedToNetwork) {
				try {
					audioPlayer.play(Commons.INTERNET_CONNECTED);
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}

			}

			lastConnectivityStatus.set(computerIsConnectedToNetwork);
		} finally {
			lock.unlock();
		}

		return computerIsConnectedToNetwork;
	}

	/**
	 * Method {@code isConnectedToNetwork} checks if RPI is connected
	 * to the Internet
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return true is connected
	 */
	public boolean isConnectedToNetwork() {
		boolean isAnyInterfaceUp = performNetworkInterfaceCheck();

		boolean googleIsConnected = performSocketConnectivity(google);
		boolean twitterIsConnected = performSocketConnectivity(twitter);

		boolean socketConnetivity = googleIsConnected & twitterIsConnected;

		boolean pingTest = performPingTest();

		logger.info("Interface check: " + isAnyInterfaceUp);
		logger.info("Socket: " + socketConnetivity);
		logger.info("Ping: " + pingTest);

		return isAnyInterfaceUp & socketConnetivity & pingTest;
	}

	/**
	 * Method {@code performNetworkInterfaceCheck} 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return 
	 */
	private boolean performNetworkInterfaceCheck() {
		try {
			boolean isAnyInterfaceUp = StreamUtils.enumerationAsStream(NetworkInterface.getNetworkInterfaces()).map(this::getNetworkInterfaceInformation).collect(Collectors.toList()).stream().anyMatch(up -> up == true);

			return isAnyInterfaceUp;
		} catch (Throwable cause) {
			logger.error(cause);
		}

		return false;
	}

	/**
	 * Method {@code getNetworkInterfaceInformation}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param networkInterface
	 * @return
	 */
	private boolean getNetworkInterfaceInformation(NetworkInterface networkInterface) {
		try {
			return networkInterface.isUp();
		} catch (Throwable cause) {
			logger.error(cause);
		}

		return false;
	}

	/**
	 * Method {@code performSocketConnectivity}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param address
	 * @return
	 */
	private boolean performSocketConnectivity(String address) {
		Socket socket = null;

		try {
			socket = new Socket();
			return isConnected(socket, address);
		} finally {
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (Throwable ignore) {
				}
			}
		}
	}

	/**
	 * Method {@code isConnected}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param socket
	 * @param address
	 * @return
	 */
	private boolean isConnected(Socket socket, String address) {
		try {
			InetSocketAddress inetSocketAddress = new InetSocketAddress(address, 80);
			socket.connect(inetSocketAddress);
			return true;
		} catch (Throwable cause) {
			logger.error(cause);
		}

		return false;
	}

	/**
	 * Method {@code performPingTest}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	private boolean performPingTest() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "1", googlePublicDns);
			Process process = processBuilder.start();
			int returnValue = process.waitFor();
			return returnValue == 0;
		} catch (Throwable cause) {
			logger.error(cause);
		}

		return false;
	}

	/**
	 * Method {@code shutdown}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void shutdown() {
		if (Objects.nonNull(blinkingLedThread)) {
			blinkingLedThread = null;
		}

		blinkLedTask.run(false);
	}
}
