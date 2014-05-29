package com.edfx.rpi.app.machine.job;

import java.util.Observable;

import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;

/**
 * Class {@code AbstractJob} is the abstract representation of Job.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public abstract class AbstractJob extends Observable implements Job {

	public static final String SCRIPT_DIR = ApplicationStorageManager.INSTANCE.getScriptDirectory().getAbsolutePath();
	public static final String STORE_DIR = ApplicationStorageManager.INSTANCE.getStoreDirectory().getAbsolutePath();

	private JobName jobName;
	private String message;

	/**
	 * Constructor {@code AbstractJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param jobName
	 */
	public AbstractJob(JobName jobName) {
		this.jobName = jobName;
	}

	/**
	 * Method {@code getJobName} returns the {@link JobName}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of JobName
	 * @see com.edfx.rpi.app.machine.job.Job#getJobName()
	 */
	public JobName getJobName() {
		return jobName;
	}

	/**
	 * Method {@code getMessage} returns the message which is the response of
	 * the Machine. This message is sent to Twitter.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the message
	 * @see com.edfx.rpi.app.machine.job.Job#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Method {@code setMessage} sets the message which will be sent to Twitter
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 *            the message to be set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
