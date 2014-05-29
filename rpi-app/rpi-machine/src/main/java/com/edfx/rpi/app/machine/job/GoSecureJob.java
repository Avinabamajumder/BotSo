package com.edfx.rpi.app.machine.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code GoSecureJob} represents the Job which is executed as the
 * response of <b>"go secure"</b> command. It turns on or off the motion
 * detector. While it is on and if there is any intruder alert then it captures
 * three images and upload the images to Google Drive and send the links to it's
 * master.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class GoSecureJob extends ImmediateJob {

	private final Logger logger = RpiLogger.getLogger(getClass());
	private final AtomicBoolean running = new AtomicBoolean(true);
	private Process process;

	/**
	 * Constructor {@code GoSecureJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public GoSecureJob() {
		super(JobName.GO_SECURE);
	}

	/**
	 * Method {@code takeImages} takes the images
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void takeImages() {
		File[] files = null;
		play(CommandResponse.INTRUDER_ALERT);

		try {
			String shell = "/bin/bash";
			String script = SCRIPT_DIR + "/take3Pics.sh";
			String[] command = new String[] { shell, script };

			logger.info("Executing script: " + script);

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process process = processBuilder.start();
			process.waitFor();
			int exitValue = process.exitValue();
			logger.info("Script executed. Exit value: " + exitValue);
			process.destroy();

			files = new File[] { new File(STORE_DIR, "left.jpg"), new File(STORE_DIR, "center.jpg"), new File(STORE_DIR, "right.jpg") };
		} catch (Throwable cause) {
			logger.error(cause);
		}

		String message = StringUtils.EMPTY;

		if (Objects.nonNull(files)) {
			String url = uploadFiles(files);

			if (StringUtils.isNotBlank(url)) {
				StringBuilder messageBuilder = new StringBuilder("Please click the below links to view the images: ");
				messageBuilder.append(url);
				message = messageBuilder.toString();
			}
		}

		if (StringUtils.isBlank(message)) {
			message = "Unable to take photos.";
		}

		setMessage(message);
		notifyUser();
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
		try {
			setMessage("Room is secure, searching for intruders.");
			sendAcknowledgement();

			play(CommandResponse.GO_SECURE);

			while (isRunning()) {
				String shell = "/bin/bash";
				String script = SCRIPT_DIR + "/startSecure.sh";
				String[] command = new String[] { shell, script };

				logger.info("Executing script: " + script);

				ProcessBuilder processBuilder = new ProcessBuilder(command);
				process = processBuilder.start();

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

				if (StringUtils.contains(outputBuilder, "MOTION_DETECTED")) {
					process.destroy();
					logger.info("Motion Detected.");

					setMessage("Intruder alert, Sending Images soon.");
					notifyUser();

					takeImages();

					TimeUnit.SECONDS.sleep(30);
				}
			}
		} catch (Throwable cause) {
			logger.error(cause);
		}
	}

	/**
	 * Method {@code isRunning} checks if the secure mode is running.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return true if secure mode running.
	 */
	public boolean isRunning() {
		return this.running.get();
	}

	/**
	 * Method {@code setRunning} sets the running mode of the job
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param running
	 *            is the state to set
	 */
	public void setRunning(boolean running) {
		this.running.set(running);
	}

	/**
	 * Method {@code killProcess} kills the motion sensor process.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void killProcess() {
		process.destroy();
	}

	/**
	 * Method {@code sendAcknowledgement} is used to send acknowledgement on job
	 * receive.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public abstract void sendAcknowledgement();

	/**
	 * Method {@code uploadFiles} uploads the image files in Google Drive
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param files
	 *            to be uploaded
	 * @return the link to the file
	 */
	public abstract String uploadFiles(File[] files);
}
