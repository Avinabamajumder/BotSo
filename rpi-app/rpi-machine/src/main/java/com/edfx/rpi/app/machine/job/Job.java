package com.edfx.rpi.app.machine.job;

import com.edfx.rpi.app.utils.audio.AudioPlayer;
import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.audio.media.Media;
import com.edfx.rpi.app.utils.thread.RpiThreadFactory;

/**
 * Class {@code Job} represents a {@link Runnable} Job.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public interface Job extends Runnable {
	final RpiThreadFactory rpiThreadFactory = RpiThreadFactory.INSTANCE;
	final AudioPlayer audioPlayer = AudioPlayer.INSTANCE;

	/**
	 * Method {@code notifyUser} is used to send message to the master in
	 * Twitter.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	void notifyUser();

	/**
	 * Method {@code getMessage} returns the message to be sent.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the message
	 */
	String getMessage();

	/**
	 * Method {@code getJobName} returns an instance of {@link JobName}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of JobName
	 * @see JobName
	 */
	JobName getJobName();

	/**
	 * Method {@code play} plays the {@link Media} which are the
	 * {@link CommandResponse}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param media to be played
	 * @see Media
	 * @see CommandResponse
	 */
	default void play(Media media) {
		try {
			rpiThreadFactory.newThread(() -> {
				audioPlayer.play(media);
			}).start();
		} catch (Throwable ignore) {

		}
	}

	/**
	 * Method {@code getJobType} returns the type of the Job
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of JobType
	 */
	default JobType getJobType() {
		return getJobName().getJobType();
	}
}
