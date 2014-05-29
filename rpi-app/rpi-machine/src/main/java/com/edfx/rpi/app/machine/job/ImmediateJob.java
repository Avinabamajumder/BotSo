package com.edfx.rpi.app.machine.job;

/**
 * Class {@code ImmediateJob} represents the Job types which needs be executed
 * immediately.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class ImmediateJob extends AbstractJob {

	/**
	 * Constructor {@code ImmediateJob}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param jobName
	 */
	public ImmediateJob(JobName jobName) {
		super(jobName);
	}
}
