package com.edfx.rpi.app.machine.job;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.common.StreamUtils;
import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code IPJob} is executed when RPI receives the command <b>"IP"</b>. It
 * send the current IP Addresses that this machine has.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class IPJob extends ImmediateJob {

	private final Logger logger = RpiLogger.getLogger(getClass());

	/**
	 * Constructor {@code IPJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public IPJob() {
		super(JobName.IP);
	}

	/**
	 * Method {@code run}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String message = "Unable to determine.";

		try {
			message = "Your IP addresses are: " + getIPAddress();
		} catch (Throwable cause) {
			logger.error(cause);
		}

		setMessage(message);
		notifyUser();
	}

	/**
	 * Method {@code getIPAddress} gets the IP Addresses that this machine has.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the comma seperated IP Addresses.
	 * @throws UnknownHostException
	 * @throws SocketException
	 */
	private String getIPAddress() throws UnknownHostException, SocketException {
		ArrayList<String> ips = new ArrayList<>();

		StreamUtils.enumerationAsStream(NetworkInterface.getNetworkInterfaces()).filter(networkInterface -> {
			try {
				return networkInterface.isUp();
			} catch (Exception ignore) {
			}

			return false;
		}).forEach(networkInterface -> {
			StreamUtils.enumerationAsStream(networkInterface.getInetAddresses()).filter(inetAddress -> {
				return !inetAddress.isLoopbackAddress() & inetAddress instanceof Inet4Address;
			}).forEach(inetAddress -> {
				ips.add(inetAddress.getHostAddress());
			});
		});

		return ips.stream().collect(Collectors.joining(", "));
	}
}
