package com.edfx.rpi.app.social.command;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Class {@code CommandProcessor} processes the command
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum CommandProcessor {

	INSTANCE;

	/**
	 * Constructor {@code CommandProcessor}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private CommandProcessor() {

	}

	/**
	 * Method {@code getCommand} returns the command after processing the text
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param text
	 *            the text to process
	 * @return an instance of Command
	 */
	public Command getCommand(String text) {
		if (StringUtils.isBlank(text)) {
			return null;
		}

		final String instruction = StringUtils.lowerCase(StringUtils.normalizeSpace(text.replaceAll("[^a-zA-Z\\d\\s]", "")));

		Command command = Command.getCommands().stream().filter(new Predicate<Command>() {

			@Override
			public boolean test(Command command) {
				ArrayList<String> tokens = command.getTokens();

				if (tokens.contains(instruction)) {
					return true;
				}

				for (String token : tokens) {
					Pattern pattern = Pattern.compile(token);
					Matcher matcher = pattern.matcher(instruction);

					if (matcher.matches()) {
						return true;
					}
				}

				return false;
			}
		}).findFirst().orElse(null);

		return command;
	}
}
