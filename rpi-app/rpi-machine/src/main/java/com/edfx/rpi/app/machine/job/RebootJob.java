package com.edfx.rpi.app.machine.job;

import com.edfx.rpi.app.utils.audio.media.CommandResponse;
import com.edfx.rpi.app.utils.tasks.ShutdownTask;

public abstract class RebootJob extends QueuingJob {

	public RebootJob() {
		super(JobName.REBOOT);
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
		ShutdownTask.INSTANCE.shutDown(true);
	}
}
