package com.edfx.rpi.app.machine.job;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.tasks.BlinkLedTask;

/**
 * Class {@code TemperatureJob} is represents the Job which is executed when RPI
 * receives the command <b>temperature</b>. It reads the current ambient
 * temperature and sent that back to its master.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class TemperatureJob extends ImmediateJob {
	private final BlinkLedTask blinkLedTask = BlinkLedTask.INSTANCE;
	private final Logger logger = RpiLogger.getLogger(getClass());

	/**
	 * Constructor {@code TemperatureJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public TemperatureJob() {
		super(JobName.TEMPERATURE);
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
		play(CommandResponse.TEMPERATURE);

		startBlinkingLed();

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
			DecimalFormat formatter = new DecimalFormat("#.##");

			if (StringUtils.isNotBlank(output) && NumberUtils.isNumber(output)) {
				int temperatureInMili = Integer.parseInt(output);
				celsius = temperatureInMili / 1000d;
				fahrenheit = (celsius * 1.8) + 32;
			}

			message = "Current ambient temperature is: " + formatter.format(celsius) + "°C/ " + formatter.format(fahrenheit) + "°F.";
		} catch (Throwable cause) {
			logger.error(cause);
			message = "Unable to determine temperature";
		}

		stopBlinkingLed();

		setMessage(message);
		notifyUser();
	}

	/**
	 * Method {@code startBlinkingLed} starts the LED to blink when it takes the
	 * temperature.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void startBlinkingLed() {
		blinkLedTask.setFrequency(new String[] { ".25", ".25", "6" });
		blinkLedTask.run(true);
		rpiThreadFactory.newThread(blinkLedTask).start();
	}

	/**
	 * Method {@code stopBlinkingLed} stops the blinking of the LED.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void stopBlinkingLed() {
		blinkLedTask.run(false);
	}
}
