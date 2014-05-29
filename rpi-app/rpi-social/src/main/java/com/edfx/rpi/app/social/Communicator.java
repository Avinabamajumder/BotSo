package com.edfx.rpi.app.social;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.machine.MachineController;
import com.edfx.rpi.app.machine.job.AreYouThereJob;
import com.edfx.rpi.app.machine.job.GoSecureJob;
import com.edfx.rpi.app.machine.job.GoToSleepJob;
import com.edfx.rpi.app.machine.job.IPJob;
import com.edfx.rpi.app.machine.job.JobName;
import com.edfx.rpi.app.machine.job.RebootJob;
import com.edfx.rpi.app.machine.job.SweepRoomJob;
import com.edfx.rpi.app.machine.job.TakeThreeJob;
import com.edfx.rpi.app.machine.job.TemperatureJob;
import com.edfx.rpi.app.social.command.Command;
import com.edfx.rpi.app.social.command.CommandProcessor;
import com.edfx.rpi.app.social.google.GoogleManager;
import com.edfx.rpi.app.social.master.Master;
import com.edfx.rpi.app.social.master.MasterProcessor;
import com.edfx.rpi.app.social.master.MasterType;
import com.edfx.rpi.app.social.twitter.TwitterConfiguration;
import com.edfx.rpi.app.social.twitter.TwitterManager;
import com.edfx.rpi.app.utils.audio.AudioPlayer;
import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.config.SecondaryUserConfiguration;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;
import com.edfx.rpi.app.utils.thread.RpiThreadFactory;

/**
 * Class {@code Communicator} is responsible to convey message to the machine
 * layer and return the response
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum Communicator implements Observer {
	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());
	private final ApplicationStorageManager applicationStorageManager = ApplicationStorageManager.INSTANCE;

	private final GoogleManager googleManager = GoogleManager.INSTANCE;
	private final TwitterManager twitterManager = TwitterManager.INSTANCE;
	private final MachineController machineController = MachineController.INSTANCE;
	private final CommandProcessor commandProcessor = CommandProcessor.INSTANCE;
	private final MasterProcessor masterProcessor = MasterProcessor.INSTANCE;

	private final ExecutorService immediateJobExecutor = machineController.getImmediateJobExecutor();
	private final ExecutorService queueingJobExecutor = machineController.getQueueingJobExecutor();

	private final AtomicBoolean secureModeRunning = new AtomicBoolean(false);

	private final AtomicBoolean changeMasterRunningForPrimaryGiveControl = new AtomicBoolean(false);
	private final AtomicBoolean changeMasterRunningForPrimaryTakeControl = new AtomicBoolean(false);
	private final AtomicBoolean changeMasterRunningForSecondaryGiveControl = new AtomicBoolean(false);

	private final AtomicBoolean changeMasterWhereSecondaryMasterExists = new AtomicBoolean(false);

	private final AtomicInteger changeMasterStep = new AtomicInteger(0);

	private GoSecureJob goSecureJob;
	private JobName currentJob;

	private SecondaryUserConfiguration secondaryUserConfiguration;

	/**
	 * Constructor {@code Communicator}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private Communicator() {

	}

	/**
	 * Method {@code sayHello} says Hello
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void sayHello() {
		AreYouThereJob areYouThereJob = new AreYouThereJob(getCurrentJob(), isSecure()) {

			@Override
			public void notifyUser() {
				twitterManager.sendDirectMessage(getMessage());
			}
		};

		immediateJobExecutor.execute(areYouThereJob);
	}

	/**
	 * Method {@code startSecure} starts the motion sensor
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void startSecure() {
		applicationStorageManager.addSecureLock();
		setSecure(true);

		goSecureJob = new GoSecureJob() {

			@Override
			public void notifyUser() {
				twitterManager.sendDirectMessage(getMessage());
			}

			@Override
			public void sendAcknowledgement() {
				twitterManager.sendDirectMessage(getMessage());
			}

			@Override
			public String uploadFiles(File[] files) {
				try {
					String url = googleManager.uploadImages(files, "RPI Album: Go Secure");
					String shortUrl = googleManager.getShortenUrl(url);
					return shortUrl;
				} catch (Throwable cause) {
					logger.error(cause);
				}

				return StringUtils.EMPTY;
			}
		};

		goSecureJob.setRunning(true);
		immediateJobExecutor.execute(goSecureJob);
	}

	/**
	 * Method {@code stopSecure} stops the motion sensor
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param stopped
	 */
	private void stopSecure(boolean stopped) {
		if (Objects.nonNull(goSecureJob)) {
			goSecureJob.setRunning(false);
			goSecureJob.killProcess();
			setSecure(false);

			if (stopped) {
				applicationStorageManager.deleteSecureLock();

				RpiThreadFactory.INSTANCE.newThread(() -> {
					AudioPlayer.INSTANCE.play(CommandResponse.STOP_SECURE);
				}).start();

				twitterManager.sendDirectMessage("Monitoring stopped.");
			}

			goSecureJob = null;
		}
	}

	/**
	 * Method {@code pasueSecure} pause the motion sensor
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void pasueSecure() {
		stopSecure(false);
	}

	/**
	 * Method {@code persistSecure} persist the secure state
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void persistSecure() {
		applicationStorageManager.addSecureLock();
	}

	/**
	 * Method {@code goToSleep} made RPI to sleep
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void goToSleep() {
		GoToSleepJob goToSleepJob = new GoToSleepJob() {

			@Override
			public void notifyUser() {
				
			}
		};

		queueingJobExecutor.execute(goToSleepJob);
	}
	
	/**
	 * Method {@code reboot}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void reboot() {
		RebootJob rebootJob = new RebootJob() {
			
			@Override
			public void notifyUser() {
				
			}
		};

		queueingJobExecutor.execute(rebootJob);
	}

	/**
	 * Method {@code sweepRoom} captures video
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param sendAcknowledgement
	 * @param reEnableSecure
	 */
	private void sweepRoom(final boolean sendAcknowledgement, final boolean reEnableSecure) {
		SweepRoomJob sweepRoomJob = new SweepRoomJob(reEnableSecure) {

			@Override
			public void sendAcknowledgement() {
				if (sendAcknowledgement) {
					twitterManager.sendDirectMessage(getMessage());
				}
			}

			@Override
			public void notifyUser() {
				twitterManager.sendDirectMessage(getMessage());
			}

			@Override
			public String uploadVideo(File file) {
				try {
					String url = googleManager.uploadVideo(file);
					String shortUrl = googleManager.getShortenUrl(url);
					return shortUrl;
				} catch (Throwable cause) {
					logger.error(cause);
				}

				return StringUtils.EMPTY;
			}

			@Override
			public void startSecure() {
				Communicator.INSTANCE.startSecure();
			}
		};

		sweepRoomJob.addObserver(this);
		queueingJobExecutor.execute(sweepRoomJob);
	}

	/**
	 * Method {@code takeThree} takes three images
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param sendAcknowledgement
	 * @param reEnableSecure
	 */
	private void takeThree(final boolean sendAcknowledgement, final boolean reEnableSecure) {
		TakeThreeJob takeThreeJob = new TakeThreeJob(reEnableSecure) {

			@Override
			public void sendAcknowledgement() {
				if (sendAcknowledgement) {
					twitterManager.sendDirectMessage(getMessage());
				}
			}

			@Override
			public void notifyUser() {
				twitterManager.sendDirectMessage(getMessage());
			}

			@Override
			public String uploadFiles(File[] files) {
				try {
					String url = googleManager.uploadImages(files, "RPI Album: Take Three");
					String shortUrl = googleManager.getShortenUrl(url);
					return shortUrl;
				} catch (Throwable cause) {
					logger.error(cause);
				}

				return StringUtils.EMPTY;
			}

			@Override
			public void startSecure() {
				Communicator.INSTANCE.startSecure();
			}
		};

		takeThreeJob.addObserver(this);
		queueingJobExecutor.execute(takeThreeJob);
	}

	/**
	 * Method {@code takeTemperature} takes temperature
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void takeTemperature() {
		TemperatureJob temperatureJob = new TemperatureJob() {

			@Override
			public void notifyUser() {
				twitterManager.sendDirectMessage(getMessage());
			}
		};

		immediateJobExecutor.execute(temperatureJob);
	}

	/**
	 * Method {@code getIp} gets the IP
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void getIp() {
		IPJob ipJob = new IPJob() {

			@Override
			public void notifyUser() {
				twitterManager.sendDirectMessage(getMessage());
			}
		};

		immediateJobExecutor.execute(ipJob);
	}

	/**
	 * Method {@code processCommand} process the command
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param command the command to process
	 */
	private void processCommand(final Command command) {
		if (command == Command.ARE_YOU_THERE) {
			sayHello();
		} else if (command == Command.GO_SECURE) {
			if (isSecure()) {
				twitterManager.sendDirectMessage("Already secure.");
			} else {
				startSecure();
			}
		} else if (command == Command.GO_TO_SLEEP) {
			if (Objects.nonNull(currentJob)) {
				if (currentJob == JobName.TAKE_THREE) {
					twitterManager.sendDirectMessage("Will process \"Go to sleep\" after executing \"Take 3\".");
				} else if (currentJob == JobName.SWEEP_ROOM) {
					twitterManager.sendDirectMessage("Will process \"Go to Sleep\" after executing \"Sweep Room\".");
				}
			}

			if (isSecure()) {
				persistSecure();
			}

			goToSleep();
		} else if (command == Command.REBOOT) {
			if (Objects.nonNull(currentJob)) {
				if (currentJob == JobName.TAKE_THREE) {
					twitterManager.sendDirectMessage("Will process \"Reboot\" after executing \"Take 3\".");
				} else if (currentJob == JobName.SWEEP_ROOM) {
					twitterManager.sendDirectMessage("Will process \"Reboot\" after executing \"Sweep Room\".");
				}
			}

			if (isSecure()) {
				persistSecure();
			}

			reboot();
		} else if (command == Command.STOP_SECURE) {
			if (isSecure()) {
				stopSecure(true);
			} else {
				twitterManager.sendDirectMessage("Secure mode not started.");
			}
		} else if (command == Command.SWEEP_ROOM) {
			if (Objects.nonNull(currentJob)) {
				if (currentJob == JobName.TAKE_THREE) {
					twitterManager.sendDirectMessage("Will process \"Sweep Room\" after executing \"Take 3\" .");
				} else if (currentJob == JobName.SWEEP_ROOM) {
					twitterManager.sendDirectMessage("Already sweeping room.");
				}
			}

			boolean sendAcknowledgement = true;
			boolean reEnableSecure = false;

			if (isSecure()) {
				sendAcknowledgement = false;
				reEnableSecure = true;
				twitterManager.sendDirectMessage("Secured mode paused, recording started.");
				pasueSecure();
			}

			sweepRoom(sendAcknowledgement, reEnableSecure);
		} else if (command == Command.TAKE_THREE) {
			if (Objects.nonNull(currentJob)) {
				if (currentJob == JobName.TAKE_THREE) {
					twitterManager.sendDirectMessage("Already taking 3.");
				} else if (currentJob == JobName.SWEEP_ROOM) {
					twitterManager.sendDirectMessage("Will process \"Take 3\" after executing \"Sweep Room\".");
				}
			}

			boolean sendAcknowledgement = true;
			boolean reEnableSecure = false;

			if (isSecure()) {
				sendAcknowledgement = false;
				reEnableSecure = true;
				twitterManager.sendDirectMessage("Secured mode paused, taking images, sending them soon.");
				pasueSecure();
			}

			takeThree(sendAcknowledgement, reEnableSecure);
		} else if (command == Command.TEMPERATURE) {
			takeTemperature();
		} else if (command == Command.IP) {
			getIp();
		}
	}

	/**
	 * Method {@code eraseSecondaryMasterDetails}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void eraseSecondaryMasterDetails() {
		try {
			secondaryUserConfiguration = null;

			try {
				SecondaryUserConfiguration secondaryUserConfiguration = SecondaryUserConfiguration.class.cast(applicationStorageManager.readSecondaryConfig());
				secondaryUserConfiguration.setActive(false);
				applicationStorageManager.writeSecondaryConfig(secondaryUserConfiguration);
			} catch (Throwable ignore) {

			}

			twitterManager.setSecondaryTwitterAccount(null);
		} catch (Throwable cause) {
			logger.error(cause.getMessage(), cause);
		}

	}

	/**
	 * Method {@code endChangeMasterInstructionFromPrimaryMasterToGiveControl}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void endChangeMasterInstructionFromPrimaryMasterToGiveControl() {
		secondaryUserConfiguration.setActive(true);

		applicationStorageManager.writeSecondaryConfig(secondaryUserConfiguration);
		changeMasterRunningForPrimaryGiveControl.set(false);
		changeMasterStep.set(0);

		TwitterConfiguration twitterConfiguration = twitterManager.getConfiguration();
		String primaryMaster = twitterConfiguration.getUserAccount();
		String secondaryMaster = secondaryUserConfiguration.getTwitterAccount();

		twitterManager.setSecondaryTwitterAccount(secondaryMaster);
		String primaryMasterMessage = "Thanks, I will now communicate with the secondary master: " + secondaryMaster;
		String secondaryMasterMessage = "Hello, You are my new master, I am ready to receive commands.";
		twitterManager.sendDirectMessageOnChangeMaster(primaryMaster, secondaryMaster, primaryMasterMessage, secondaryMasterMessage);

		if (changeMasterWhereSecondaryMasterExists.get()) {
			changeMasterWhereSecondaryMasterExists.set(false);
		}
	}

	/**
	 * Method {@code processGoogleAccount}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param instruction
	 */
	private void processGoogleAccount(String instruction) {
		if (isAValidEmail(instruction)) {
			if (isNotSameGoogleAccount(instruction)) {
				secondaryUserConfiguration.setGoogleAccount(instruction);
				twitterManager.sendDirectMessage("Got it, now tell me secondary masters Twitter handle.");

				if (changeMasterWhereSecondaryMasterExists.get()) {
					changeMasterStep.set(3);
				} else {
					changeMasterStep.set(2);
				}
			} else {
				twitterManager.sendDirectMessage("Google handle: " + instruction + " is already assign to Primary Master. Please provide a different Google handle.");
			}
		} else {
			twitterManager.sendDirectMessage("\"" + instruction + "\" is not a valid Google handle.");
		}
	}

	/**
	 * Method {@code processTwitterAccount}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param instruction
	 */
	private void processTwitterAccount(String instruction) {
		boolean isAValidTwitterAccount = twitterManager.validate(instruction);

		if (isAValidTwitterAccount) {
			if (isNotSameTwitterAccount(instruction)) {
				if (!isItMe(instruction)) {
					secondaryUserConfiguration.setTwitterAccount(instruction);
					endChangeMasterInstructionFromPrimaryMasterToGiveControl();
				} else {
					twitterManager.sendDirectMessage("You are kidding right!! :-). I cannot make myself as my master.");
				}
			} else {
				twitterManager.sendDirectMessage("Twitter handle: " + instruction + " is already assign to Primary Master. Please provide a different Twitter handle.");
			}
		} else {
			twitterManager.sendDirectMessage("Unable to validate Twitter account.");
		}
	}

	/**
	 * Method {@code processChangeMasterInstructionFromPrimaryMasterToGiveControl}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param instruction
	 */
	private void processChangeMasterInstructionFromPrimaryMasterToGiveControl(String instruction) {
		instruction = StringUtils.trim(instruction);

		if (changeMasterWhereSecondaryMasterExists.get()) {
			if (changeMasterStep.get() == 1) {
				if (StringUtils.equalsIgnoreCase(instruction, "yes")) {
					endChangeMasterInstructionFromPrimaryMasterToGiveControl();
				} else if (StringUtils.equalsIgnoreCase(instruction, "no")) {
					changeMasterStep.set(2);
					secondaryUserConfiguration = new SecondaryUserConfiguration();
					twitterManager.sendDirectMessage("OK, Tell me secondary masters Google handle.");
				} else {
					twitterManager.sendDirectMessage("I didn't understand. Please respond in \"Yes\" or \"No\".");
				}
			} else if (changeMasterStep.get() == 2) {
				processGoogleAccount(instruction);
			} else if (changeMasterStep.get() == 3) {
				processTwitterAccount(instruction);
			}
		} else {
			if (changeMasterStep.get() == 1) {
				processGoogleAccount(instruction);
			} else if (changeMasterStep.get() == 2) {
				processTwitterAccount(instruction);
			}
		}
	}

	/**
	 * Method {@code processChangeMasterInstructionFromPrimaryMasterToTakeControl}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param instruction
	 */
	private void processChangeMasterInstructionFromPrimaryMasterToTakeControl(String instruction) {
		Master primaryMaster = masterProcessor.getPrimaryMaster();
		Master secondaryMaster = masterProcessor.getSecondaryMaster();

		String primaryMasterScreenName = primaryMaster.getTwitterAccount();

		if (StringUtils.equalsIgnoreCase(instruction, "yes")) {
			String primaryMasterMessage = "Bye, my primary master is back. It was nice talking to you.";
			String secondaryMasterMessage = "OK, I am ready to communicate with you again.";
			twitterManager.sendDirectMessageOnChangeMaster(secondaryMaster.getTwitterAccount(), primaryMasterScreenName, primaryMasterMessage, secondaryMasterMessage);
			changeMasterRunningForPrimaryTakeControl.set(false);
			eraseSecondaryMasterDetails();
		} else if (StringUtils.equalsIgnoreCase(instruction, "no")) {
			changeMasterRunningForPrimaryTakeControl.set(false);
			twitterManager.sendDirectMessage(primaryMasterScreenName, "OK, See you later.");
		} else {
			twitterManager.sendDirectMessage(primaryMasterScreenName, "I didn't understand. I am still communicating with " + secondaryMaster.getTwitterAccount() + ", are you sure you want me to communicate with you?.");
		}
	}

	/**
	 * Method {@code processChangeMasterInstructionFromSecondaryMasterToGiveControl}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param instruction
	 */
	private void processChangeMasterInstructionFromSecondaryMasterToGiveControl(String instruction) {
		Master primaryMaster = masterProcessor.getPrimaryMaster();
		Master secondaryMaster = masterProcessor.getSecondaryMaster();

		String primaryMasterScreenName = primaryMaster.getTwitterAccount();

		if (StringUtils.equalsIgnoreCase(instruction, "yes")) {
			eraseSecondaryMasterDetails();
			changeMasterRunningForSecondaryGiveControl.set(false);
			String primaryMasterMessage = "OK, Bye, See you later.";
			String secondaryMasterMessage = "Hello, I am ready to communicate with you again.";
			twitterManager.sendDirectMessageOnChangeMaster(secondaryMaster.getTwitterAccount(), primaryMasterScreenName, primaryMasterMessage, secondaryMasterMessage);
		} else if (StringUtils.equalsIgnoreCase(instruction, "no")) {
			changeMasterRunningForSecondaryGiveControl.set(false);
			twitterManager.sendDirectMessage("OK.");
		} else {
			twitterManager.sendDirectMessage(primaryMasterScreenName, "I didn't understand. I am still communicating with " + secondaryMaster.getTwitterAccount() + ", are you sure you want me to communicate with you?.");
		}
	}

	/**
	 * Method {@code isAValidEmail}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param mail
	 * @return
	 */
	private boolean isAValidEmail(String mail) {
		try {
			String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher matcher = pattern.matcher(mail);
			return matcher.matches();
		} catch (Throwable cause) {
			logger.error(cause.getMessage(), cause);
		}

		return false;
	}

	/**
	 * Method {@code isNotSameGoogleAccount}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param googleAccount
	 * @return
	 */
	private boolean isNotSameGoogleAccount(String googleAccount) {
		Master master = masterProcessor.getPrimaryMaster();
		return !StringUtils.equalsIgnoreCase(master.getGoogleAccount(), googleAccount);
	}

	/**
	 * Method {@code isNotSameTwitterAccount}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterAccount
	 * @return
	 */
	private boolean isNotSameTwitterAccount(String twitterAccount) {
		Master master = masterProcessor.getPrimaryMaster();
		return !StringUtils.equalsIgnoreCase(master.getTwitterAccount(), twitterAccount);
	}

	/**
	 * Method {@code isItMe}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterAccount
	 * @return
	 */
	private boolean isItMe(String twitterAccount) {
		TwitterConfiguration configuration = twitterManager.getConfiguration();
		return StringUtils.equalsIgnoreCase(configuration.getRpiAccount(), twitterAccount);
	}

	/**
	 * Method {@code communicate}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param instruction
	 * @param screenName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void communicate(String instruction, String screenName) throws IOException, InterruptedException {
		MasterType masterType = masterProcessor.getMasterType(screenName);
		MasterType currentMasterType = masterProcessor.getCurrentMasterType();

		Command command = commandProcessor.getCommand(instruction);

		if (Objects.nonNull(masterType)) {
			if (Objects.nonNull(currentMasterType) && masterType != currentMasterType && Objects.nonNull(command) && command != Command.CHANGE_MASTER && command != Command.CANCEL_CHANGE_MASTER) {
				Master currentMaster = masterProcessor.getMaster();
				twitterManager.sendDirectMessage(screenName, "Currently I am communicationg with " + currentMaster.getTwitterAccount() + ".");
				return;
			}

			// primary master
			if (masterType == MasterType.PRIMARY) {
				if (Objects.nonNull(command)) {
					if (changeMasterRunningForPrimaryGiveControl.get()) {
						// primary master is giving control

						if (command == Command.CHANGE_MASTER) {
							// requested for Change Master while running Change
							// Master

							String messageDescribingCurrentStep = StringUtils.EMPTY;

							if (changeMasterWhereSecondaryMasterExists.get()) {
								if (changeMasterStep.get() == 1) {
									messageDescribingCurrentStep = " confirmation.";
								} else if (changeMasterStep.get() == 2) {
									messageDescribingCurrentStep = " secondary master's Google handle.";
								} else {
									messageDescribingCurrentStep = " secondary master's Twitter handle.";
								}
							} else {
								messageDescribingCurrentStep = changeMasterStep.get() == 1 ? " secondary master's Google handle." : " secondary master's Twitter handle.";
							}

							twitterManager.sendDirectMessage("Already processing, waiting for " + messageDescribingCurrentStep);
						} else if (command == Command.CANCEL_CHANGE_MASTER) {
							// requested for Cancel Change Master

							changeMasterRunningForPrimaryGiveControl.set(false);
							changeMasterStep.set(0);
							twitterManager.sendDirectMessage("OK, See you later.");
							eraseSecondaryMasterDetails();
						} else {
							// other commands will not work

							twitterManager.sendDirectMessage("Sorry, cannot process the command, you need to complete or cancel the change master process first.");
						}

						return;
					}

					if (changeMasterRunningForPrimaryTakeControl.get()) {
						// primary master is taking control

						if (command == Command.CHANGE_MASTER) {
							// requested for Change Master while running Change
							// Master
							twitterManager.sendDirectMessage(screenName, "Already processing, waiting for confirmation.");
						} else if (command == Command.CANCEL_CHANGE_MASTER) {
							changeMasterRunningForPrimaryTakeControl.set(false);
							twitterManager.sendDirectMessage(screenName, "OK, See you later.");
						} else {
							// other commands will not work

							twitterManager.sendDirectMessage(screenName, "Sorry, cannot process the command, you need to complete or cancel the change master process first.");
						}

						return;
					}

					if (command == Command.CHANGE_MASTER) {
						// requested for Change Master

						if (changeMasterRunningForSecondaryGiveControl.get()) {
							twitterManager.sendDirectMessage(screenName, "Already processing change master on secondary master's request.");
							return;
						}

						if (currentMasterType == MasterType.PRIMARY) {
							// primary master requested it for giving the
							// control.

							changeMasterRunningForPrimaryGiveControl.set(true);
							changeMasterStep.set(1);

							if (Objects.nonNull(secondaryUserConfiguration)) {
								secondaryUserConfiguration = null;
							}

							secondaryUserConfiguration = SecondaryUserConfiguration.class.cast(applicationStorageManager.readSecondaryConfig());

							if (Objects.isNull(secondaryUserConfiguration)) {
								secondaryUserConfiguration = new SecondaryUserConfiguration();
								twitterManager.sendDirectMessage("Understood, tell me secondary masters Google handle.");
							} else {
								changeMasterWhereSecondaryMasterExists.set(true);
								twitterManager.sendDirectMessage("Understood, do you want to give control to " + secondaryUserConfiguration.getTwitterAccount() + " or a new master?");
							}
						} else {
							// primary master requested it for taking the
							// control.

							changeMasterRunningForPrimaryTakeControl.set(true);

							Master secondaryMaster = masterProcessor.getSecondaryMaster();
							Master primaryMaster = masterProcessor.getPrimaryMaster();

							if (Objects.nonNull(secondaryMaster)) {
								twitterManager.sendDirectMessage(primaryMaster.getTwitterAccount(), "I am currently communicating with " + secondaryMaster.getTwitterAccount() + ", are you sure you want me to communicate with you?");
							}
						}
					} else {
						processCommand(command);
					}
				} else {
					if (changeMasterRunningForPrimaryGiveControl.get()) {
						// start communication

						processChangeMasterInstructionFromPrimaryMasterToGiveControl(instruction);
					} else if (changeMasterRunningForPrimaryTakeControl.get()) {
						// start communication

						processChangeMasterInstructionFromPrimaryMasterToTakeControl(instruction);
					} else {
						twitterManager.sendDirectMessage("Unable to understand command: " + instruction);
					}
				}
			} else { // secondary master
				if (Objects.nonNull(command)) {
					if (changeMasterRunningForSecondaryGiveControl.get()) {
						// secondary master is giving control

						if (command == Command.CHANGE_MASTER) {
							twitterManager.sendDirectMessage("Already processing, waiting for confirmation.");
						} else if (command == Command.CANCEL_CHANGE_MASTER) {
							changeMasterRunningForSecondaryGiveControl.set(false);
							twitterManager.sendDirectMessage("OK, See you later.");
						} else {
							// other commands will not work

							twitterManager.sendDirectMessage("Sorry, cannot process the command, you need to complete or cancel the change master process first.");
						}

						return;
					}

					if (command == Command.CHANGE_MASTER) {
						if (changeMasterRunningForPrimaryTakeControl.get()) {
							twitterManager.sendDirectMessage("Already processing change master on primary master's request.");
							return;
						}

						changeMasterRunningForSecondaryGiveControl.set(true);
						twitterManager.sendDirectMessage("Are you sure? If I change master, I won’t be able to communicate with you.");
					} else {
						processCommand(command);
					}
				} else {
					if (changeMasterRunningForSecondaryGiveControl.get()) {
						// start communication

						processChangeMasterInstructionFromSecondaryMasterToGiveControl(instruction);
					} else {
						twitterManager.sendDirectMessage("Unable to understand command: " + instruction);
					}
				}
			}
		}
	}

	/**
	 * Method {@code update}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param observable
	 * @param arguemnts
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object arguemnts) {
		if (Objects.nonNull(arguemnts)) {
			currentJob = JobName.class.cast(arguemnts);
		} else {
			currentJob = null;
		}
	}

	/**
	 * Method {@code isSecure}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return
	 */
	public boolean isSecure() {
		return secureModeRunning.get();
	}

	/**
	 * Method {@code setSecure}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param secure
	 */
	public void setSecure(boolean secure) {
		secureModeRunning.set(secure);
	}

	public JobName getCurrentJob() {
		return currentJob;
	}

	/**
	 * Method {@code startCommunication}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void startCommunication() {
		if (applicationStorageManager.isSecureLocked()) {
			startSecure();
		}
	}

	/**
	 * Method {@code stopCommunication}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void stopCommunication() {
		machineController.shutdown();
	}
}
