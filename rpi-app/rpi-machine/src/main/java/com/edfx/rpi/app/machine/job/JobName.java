package com.edfx.rpi.app.machine.job;

/**
 * Class {@code JobName} represents the name of the each job that RPI performs.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum JobName {

	ARE_YOU_THERE("Are you there.", JobType.IMMEDIATE),
	TAKE_THREE("Take three.", JobType.QUEUING),
	SWEEP_ROOM("Sweep room.", JobType.QUEUING),
	GO_SECURE("Go secure.", JobType.IMMEDIATE),
	TEMPERATURE("Temperature.", JobType.IMMEDIATE),
	STOP_SECURE("Stop secure.", JobType.IMMEDIATE),
	GO_TO_SLEEP("Go to sleep.", JobType.QUEUING),
	IP("IP", JobType.IMMEDIATE),
	REBOOT("Reboot", JobType.QUEUING);
	
	private String descriptipn;
	private JobType jobType;
	
	/**
	 * Constructor {@code JobName}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param descriptipn
	 * @param jobType
	 */
	private JobName(String descriptipn, JobType jobType) {
		this.descriptipn = descriptipn;
		this.jobType = jobType;
	}

	/**
	 * Method {@code getDescriptipn} return the descriptipn
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the descriptipn
	 */
	public String getDescriptipn() {
		return descriptipn;
	}

	/**
	 * Method {@code getJobType} return the jobType
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the jobType
	 */
	public JobType getJobType() {
		return jobType;
	}
}