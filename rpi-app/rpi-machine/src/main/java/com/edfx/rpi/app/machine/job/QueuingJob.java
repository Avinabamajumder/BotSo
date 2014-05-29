package com.edfx.rpi.app.machine.job;

import java.util.Observer;

/**
 * 
 * Class {@code QueuingJob} represents the Job types which needs be executed in
 * a queue.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class QueuingJob extends AbstractJob {

	/**
	 * Constructor {@code QueuingJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param jobName
	 */
	public QueuingJob(JobName jobName) {
		super(jobName);
	}

	/**
	 * Method {@code beginExecution} is executed at the beginning of each run.
	 * It notifies it's {@link Observer}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void beginExecution() {
		setChanged();
		notifyObservers(getJobName());
	}

	/**
	 * Method {@code endExecution} is executed at the end of each run.
	 * It notifies it's {@link Observer}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void endExecution() {
		setChanged();
		notifyObservers(null);
	}
}
