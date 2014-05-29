package com.edfx.rpi.app.machine.job;

import java.io.File;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code TakeThreeJob} is executed when RPI gets the command <b>tak
 * three</b>. It takes three images, uploads then in Google Drive in a album and
 * send the link to the album to the master.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class TakeThreeJob extends QueuingJob {
	private final Logger logger = RpiLogger.getLogger(getClass());

	private boolean reEnableSecure;

	/**
	 * Constructor {@code TakeThreeJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param reEnableSecure
	 */
	public TakeThreeJob(boolean reEnableSecure) {
		super(JobName.TAKE_THREE);
		this.reEnableSecure = reEnableSecure;
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
		play(CommandResponse.TAKE_THREE);

		beginExecution();
		setMessage("Taking Images, sending you links soon.");
		sendAcknowledgement();
		File[] files = null;

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

		endExecution();
		setMessage(message);
		notifyUser();

		if (reEnableSecure) {
			startSecure();
		}
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

	/**
	 * Method {@code startSecure} starts the motion sensor while this job is running.
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public abstract void startSecure();
}