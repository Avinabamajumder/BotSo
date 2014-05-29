package com.edfx.rpi.app.machine.job;

import java.io.File;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code SweepRoomJob} is executed when RPI receives the command
 * <b>"sweep room"</b>. Its records a video, uploads the video in Google Drive
 * and send the link to the master.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class SweepRoomJob extends QueuingJob {

	private final Logger logger = RpiLogger.getLogger(getClass());

	private boolean reEnableSecure;

	/**
	 * Constructor {@code SweepRoomJob}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param reEnableSecure
	 */
	public SweepRoomJob(boolean reEnableSecure) {
		super(JobName.SWEEP_ROOM);
		this.reEnableSecure = reEnableSecure;
	}

	/**
	 * Method {@code run}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		play(CommandResponse.SWEEP_ROOM);

		beginExecution();
		setMessage("Recording started.");
		sendAcknowledgement();
		File file = null;

		try {
			String shell = "/bin/bash";
			String script = SCRIPT_DIR + "/takeSweepVid.sh";
			String[] command = new String[] { shell, script };

			logger.info("Executing script: " + script);

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process process = processBuilder.start();
			process.waitFor();
			int exitValue = process.exitValue();
			logger.info("Script executed. Exit value: " + exitValue);
			process.destroy();

			file = new File(STORE_DIR, "sweepVideo.mpg");
		} catch (Throwable cause) {
			logger.error(cause);
		}

		String message = StringUtils.EMPTY;

		if (Objects.nonNull(file)) {
			String url = uploadVideo(file);

			if (StringUtils.isNotBlank(url)) {
				StringBuilder messageBuilder = new StringBuilder("Click the following link to view the video: ");
				messageBuilder.append(url);
				message = messageBuilder.toString();
			}
		}

		if (StringUtils.isBlank(message)) {
			message = "Unable to record video.";
		}

		endExecution();
		setMessage(message);
		notifyUser();

		if (reEnableSecure) {
			startSecure();
		}
	}

	/**
	 * Method {@code sendAcknowledgement} is used to send acknowledgement on job receive.
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public abstract void sendAcknowledgement();

	/**
	 * Method {@code uploadVideo} uploads the video file in Google Drive
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param file to be uploaded 
	 * @return the link to the file
	 */
	public abstract String uploadVideo(File file);

	/**
	 * Method {@code startSecure} motion sensor while this job is running.
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public abstract void startSecure();
}
