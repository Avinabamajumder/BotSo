package com.edfx.rpi.app.machine.job;

import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.tasks.ShutdownTask;

/**
 * Class {@code GoToSleepJob} is executed for the command <b>go to sleep</b>. It
 * turns of the RPI.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class GoToSleepJob extends QueuingJob {

	public GoToSleepJob() {
		super(JobName.GO_TO_SLEEP);
	}

	/**
	 * Method {@code run}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		play(CommandResponse.GO_TO_SLEEP);
		ShutdownTask.INSTANCE.shutDown(false);
	}
}
