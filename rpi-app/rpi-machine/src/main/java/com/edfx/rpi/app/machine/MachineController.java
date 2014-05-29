package com.edfx.rpi.app.machine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.machine.job.ImmediateJob;
import com.edfx.rpi.app.machine.job.Job;
import com.edfx.rpi.app.machine.job.QueuingJob;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.thread.RpiThreadFactory;

/**
 * Class {@code MachineController} is core class for the machine layer. It
 * initializes the {@link ExecutorService} instances which are used to execute
 * the various {@link Job} performed by RPI.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum MachineController {

	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());

	private final BlockingQueue<Runnable> workQueue;
	private final ExecutorService immediateJobExecutor;
	private final ExecutorService queueingJobExecutor;

	/**
	 * Constructor {@code MachineController}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private MachineController() {
		int corePoolSize = 4;
		int maximumPoolSize = corePoolSize;

		ThreadFactory rpiThreadFactory = RpiThreadFactory.INSTANCE;
		workQueue = new LinkedBlockingQueue<>();
		immediateJobExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, Long.MAX_VALUE, TimeUnit.NANOSECONDS, workQueue, rpiThreadFactory);
		queueingJobExecutor = Executors.newSingleThreadExecutor(rpiThreadFactory);
	}

	/**
	 * Method {@code getImmediateJobExecutor} returns the instance of the
	 * {@link ExecutorService} which is used to execute {@link ImmediateJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of ExecutorService
	 * @see ImmediateJob
	 */
	public ExecutorService getImmediateJobExecutor() {
		return immediateJobExecutor;
	}

	/**
	 * Method {@code getQueueingJobExecutor} returns the instance of the
	 * {@link ExecutorService} which is used to execute {@link QueuingJob}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of ExecutorService
	 * @see QueuingJob
	 */
	public ExecutorService getQueueingJobExecutor() {
		return queueingJobExecutor;
	}

	/**
	 * Method {@code shutdown} shutdown the {@link ExecutorService} instances
	 * which executes the {@link ImmediateJob} and {@link QueuingJob}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @see ImmediateJob
	 * @see QueuingJob
	 */
	public void shutdown() {
		logger.info("Stopping Machine Controller.");
		immediateJobExecutor.shutdownNow();
		queueingJobExecutor.shutdownNow();
	}
}
