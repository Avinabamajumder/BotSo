package com.edfx.rpi.app.utils.audio;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.audio.media.Media;
import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;

/**
 * Class {@code AudioPlayer} is used to play {@link Media}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum AudioPlayer {
	INSTANCE;

	private final Lock lock = new ReentrantLock();
	private final Logger logger = RpiLogger.getLogger(getClass());
	private final String root = ApplicationStorageManager.INSTANCE.getMediaDirectory().getAbsolutePath();

	/**
	 * Constructor {@code AudioPlayer}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private AudioPlayer() {

	}

	/**
	 * Method {@code play} plays the {@link Media}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param media
	 *            the media to play
	 * @see Media
	 */
	public void play(Media media) {
		lock.lock();

		try {
			String path = root + "/" + media.getPath();
			executePlay(path);
		} catch (Throwable cause) {
			logger.error(cause.getMessage(), cause);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Method {@code play} plays a list of {@link Media} with the given interval
	 * {@code pause}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param medias
	 *            the list of media to play
	 * @param pause
	 *            the interval
	 */
	public void play(List<Media> medias, long pause) {
		lock.lock();

		try {
			medias.forEach(media -> {
				try {
					String path = root + "/" + media.getPath();
					executePlay(path);
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}

				try {
					TimeUnit.MILLISECONDS.sleep(pause);
				} catch (Throwable cause) {
					logger.error(cause.getMessage(), cause);
				}
			});
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Method {@code executePlay} exeute {@code aplay} command to play the given
	 * file designated with {@code path}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param path
	 *            the path to the media file to play
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void executePlay(String path) throws IOException, InterruptedException {
		File file = new File(path);

		if (!file.exists()) {
			return;
		}

		String shell = "/bin/bash";
		String script = ApplicationStorageManager.INSTANCE.getScriptDirectory() + "/playSound.sh";

		logger.info("Executing script: " + script);
		logger.info("Path to the media: " + path);
		ProcessBuilder processBuilder = new ProcessBuilder(new String[] { shell, script, path });
		Process process = processBuilder.start();
		process.waitFor();
		process.destroy();
	}
}
