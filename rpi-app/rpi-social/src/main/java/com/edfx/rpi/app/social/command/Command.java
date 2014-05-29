package com.edfx.rpi.app.social.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.logger.RpiLogger;

/**
 * Class {@code Command}
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Command {

	ARE_YOU_THERE("are-you-there.dictionary", new ArrayList<String>()),
	GO_SECURE("go-secure.dictionary", new ArrayList<String>()),
	GO_TO_SLEEP("go-to-sleep.dictionary", new ArrayList<String>()),
	STOP_SECURE("stop-secure.dictionary", new ArrayList<String>()),
	SWEEP_ROOM("sweep-room.dictionary", new ArrayList<String>()),
	TAKE_THREE("take-three.dictionary", new ArrayList<String>()),	
	TEMPERATURE("temperature.dictionary", new ArrayList<String>()),
	CHANGE_MASTER("change-master.dictionary", new ArrayList<String>()),
	CANCEL_CHANGE_MASTER("cancel-change-master.dictionary", new ArrayList<String>()),
	IP("ip.dictionary", new ArrayList<String>()),
	REBOOT("reboot.dictionary", new ArrayList<String>());
		
	private final static Logger LOGGER = RpiLogger.getLogger(Command.class);
	private final static Set<Command> COMMANDS = Collections.unmodifiableSet(new HashSet<Command>() {

		private static final long serialVersionUID = 4956239571125261800L;

		{
			addAll(Arrays.asList(values()).stream().map(new Function<Command, Command>() {

				@Override
				public Command apply(Command command) {
					String filename = command.getFilename();
					ArrayList<String> tokens = command.getTokens();
					InputStream stream = Command.class.getResourceAsStream(filename);
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
						for (;;) {
							String line = reader.readLine();
							
							if (StringUtils.isBlank(line)) {
								break;
							}
							
							tokens.add(StringUtils.lowerCase(StringUtils.normalizeSpace(line.replaceAll("[^a-zA-Z\\d\\s]", ""))));
						}
					} catch (IOException e) {
						LOGGER.error(e);
					}

					return command;
				}
			}).collect(Collectors.toSet()));
		}
	});
	
	private String filename;
	private ArrayList<String> tokens;
	
	/**
	 * Constructor {@code Command}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param filename
	 * @param tokens
	 */
	private Command(String filename, ArrayList<String> tokens) {
		this.filename = filename;
		this.tokens = tokens;
	}	
	
	/**
	 * Method {@code getFilename} return the filename
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Method {@code getTokens} return the tokens
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the tokens
	 */
	public ArrayList<String> getTokens() {
		return tokens;
	}

	/**
	 * Method {@code getCommands}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	public static Set<Command> getCommands() {
		return COMMANDS;
	}
}
