package com.edfx.rpi.app.machine.job;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code AreYouThereJob} represents the Job which is executed as the
 * response of <b>"are you there"</b> command. It sends a response to its master along with the following information:
 * <ul>
 * 	<li>Current Temperature</li>
 * <li>Currently running Job</li>
 * <li>If the secure mode is on or not</li>
 * </ul>
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class AreYouThereJob extends ImmediateJob {

	private final Logger logger = RpiLogger.getLogger(getClass());

	private final JobName currentJob;
	private final boolean isSecure;

	/**
	 * Constructor {@code AreYouThereJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param currentJob
	 * @param isSecure
	 */
	public AreYouThereJob(final JobName currentJob, final boolean isSecure) {
		super(JobName.ARE_YOU_THERE);
		this.currentJob = currentJob;
		this.isSecure = isSecure;
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
		play(CommandResponse.ARE_YOU_THERE);

		String currentJobDescription = getCurrentJobDescription();
		String temperatureMessage = getTemperature();

		StringBuilder messageBuilder = new StringBuilder("Yes, I am. ");
		messageBuilder.append(temperatureMessage);
		messageBuilder.append(StringUtils.isNotBlank(currentJobDescription) ? (" Currently executing: " + currentJobDescription) : StringUtils.EMPTY);
		messageBuilder.append(" Secure Status: ").append(isSecure ? "ON" : "OFF.");

		setMessage(messageBuilder.toString());
		notifyUser();
	}

	/**
	 * Method {@code getTemperature} reads the temperature and returns to the
	 * caller.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the temperature
	 */
	private String getTemperature() {
		String message = StringUtils.EMPTY;

		try {
			String shell = "/bin/bash";
			String script = SCRIPT_DIR + "/showTemp.sh";
			String[] command = new String[] { shell, script };

			logger.info("Executing script: " + script);

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

			int exitValue = process.exitValue();
			logger.info("Script executed. Exit value: " + exitValue);
			process.destroy();

			String output = outputBuilder.toString();
			Pattern pattern = Pattern.compile("(T|t)(=)(-)*[(0-9)]*");
			Matcher matcher = pattern.matcher(outputBuilder.toString());

			if (matcher.find()) {
				String reading = matcher.group();
				output = reading.split("(T|t)(=)")[1];
			}

			double celsius = 0;
			double fahrenheit = 0;

			if (StringUtils.isNotBlank(output) && NumberUtils.isNumber(output)) {
				int temperatureInMili = Integer.parseInt(output);
				celsius = temperatureInMili / 1000d;
				fahrenheit = (celsius * 1.8) + 32;
			}

			message = "Current ambient temperature is: " + celsius + "°C/ " + fahrenheit + "°F.";
		} catch (Throwable cause) {
			logger.error(cause);
			message = "Unable to determine temperature";
		}

		return message;
	}

	/**
	 * Method {@code getCurrentJobDescription} gets the description of the
	 * current {@link QueuingJob}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the description
	 * @see QueuingJob
	 */
	private String getCurrentJobDescription() {
		if (Objects.nonNull(currentJob) && currentJob.getJobType() == JobType.QUEUING) {
			return currentJob.getDescriptipn();
		}

		return StringUtils.EMPTY;
	}
}
