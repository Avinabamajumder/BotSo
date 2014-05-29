package com.edfx.rpi.app.utils.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Class {@code ApplicationStorageManager} is the manager of the storage of the
 * RPI.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum ApplicationStorageManager {

	INSTANCE;

	private final String requestTokenFile = "request-token";
	private final String twitterConfigFile = "twitter-config";
	private final String googleConfigFile = "google-config";
	private final String secondaryConfigFile = "secondary-config";
	private final String secureLock = "secure-lock";
	private final String googleCrdentialDir = "credential";

	private Path appDirectory;
	private Path configDirectory;
	private Path mediaDirectory;
	private Path scriptDirectory;
	private Path storeDirectory;

	/**
	 * Constructor {@code ApplicationStorageManager}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private ApplicationStorageManager() {
		initialize();
	}

	/**
	 * Method {@code initialize} initializes the storage.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @throws ApplicationStorageException
	 */
	private void initialize() throws ApplicationStorageException {
		appDirectory = SystemUtils.getUserHome().toPath();
		configDirectory = appDirectory.resolve("config");
		mediaDirectory = appDirectory.resolve("media");
		scriptDirectory = appDirectory.resolve("scripts");
		storeDirectory = appDirectory.resolve("store");
	}

	/**
	 * Method {@code resolvePath} resolves the {@code path} with the given
	 * {@code parentPath}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param parentPath
	 *            the path to the parent
	 * @param path
	 *            the path to resolve
	 * @return the resolved path
	 */
	private Path resolvePath(Path parentPath, String path) {
		return parentPath.resolve(path);
	}

	/**
	 * Method {@code createNew} creates a new file to the given path.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param path
	 *            is the path where the file will be created
	 * @return the path instance of the newly created file
	 * @throws IOException
	 */
	private Path createNew(Path path) throws IOException {
		return Files.createFile(path);
	}

	/**
	 * Method {@code getApplicationStorageException} returns a new instance of
	 * {@link ApplicationStorageException} with the given {@code message}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 *            the error message
	 * @return an instance of ApplicationStorageException
	 */
	private ApplicationStorageException getApplicationStorageException(String message) {
		return getApplicationStorageException(message, null);
	}

	/**
	 * Method {@code getApplicationStorageException} returns a new instance of
	 * {@link ApplicationStorageException} with the given {@code cause}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param cause
	 *            the Throwable instance
	 * @return an instance of ApplicationStorageException
	 */
	private ApplicationStorageException getApplicationStorageException(Throwable cause) {
		return getApplicationStorageException(null, cause);
	}

	/**
	 * Method {@code getApplicationStorageException} returns a new instance of
	 * {@link ApplicationStorageException} with the given {@code message} and
	 * {@code cause}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param message
	 *            the error message
	 * @param cause
	 *            the Throwable instance
	 * @return an instance of ApplicationStorageException
	 */
	private ApplicationStorageException getApplicationStorageException(String message, Throwable cause) {
		if (cause instanceof ApplicationStorageException) {
			return (ApplicationStorageException) cause;
		}

		StringBuilder exceptionMessageBuilder = new StringBuilder(StringUtils.EMPTY);

		if (StringUtils.isNotEmpty(message)) {
			exceptionMessageBuilder.append(message);
		}

		if (Objects.nonNull(cause)) {
			exceptionMessageBuilder.append(StringUtils.SPACE);
			exceptionMessageBuilder.append(cause.getMessage());
		}

		ApplicationStorageException exception = new ApplicationStorageException(exceptionMessageBuilder.toString());

		if (Objects.nonNull(cause)) {
			exception.initCause(cause);
		}

		return exception;
	}

	/**
	 * Method {@code checkInitialized} checks if the storage is initialized or
	 * not. If not throws {@link ApplicationStorageException}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @throws ApplicationStorageException
	 */
	private void checkInitialized() throws ApplicationStorageException {
		if (!Files.exists(appDirectory)) {
			throw getApplicationStorageException("Application File Manager is not initialized.");
		}
	}

	/**
	 * Method {@code writeSerializable} writes a {@link Serializable} to the
	 * given {@code fileName}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param serializable
	 *            the Serializable instance to write
	 * @param fileName
	 *            the name of the file
	 * @throws ApplicationStorageException
	 */
	private void writeSerializable(Serializable serializable, String fileName) throws ApplicationStorageException {
		checkInitialized();
		ObjectOutputStream stream = null;

		try {
			Path pathToWrite = resolvePath(configDirectory, fileName);

			if (Files.exists(pathToWrite)) {
				Files.delete(pathToWrite);
			}

			pathToWrite = createNew(pathToWrite);

			File file = pathToWrite.toFile();
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			stream = new ObjectOutputStream(bos);
			stream.writeObject(serializable);
		} catch (Throwable cause) {
			throw getApplicationStorageException(cause);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Throwable ignore) {
				}
			}
		}
	}

	/**
	 * Method {@code readSerializable} reads the {@link Serializable} from the
	 * given {@code fileName}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param fileName
	 *            the file name from which the Serializable will be read
	 * @return a Serializable instance
	 * @throws ApplicationStorageException
	 */
	private Serializable readSerializable(String fileName) throws ApplicationStorageException {
		checkInitialized();
		ObjectInputStream stream = null;

		try {
			Path pathToRead = resolvePath(configDirectory, fileName);

			if (!Files.exists(pathToRead)) {
				return null;
			}

			File file = pathToRead.toFile();
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			stream = new ObjectInputStream(bis);
			return (Serializable) stream.readObject();
		} catch (Throwable cause) {
			throw getApplicationStorageException(cause);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Throwable ignore) {
				}
			}
		}
	}

	/**
	 * Method {@code delete} deletes the file provided by the path if it exists.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param path
	 *            is the path to the file which will be deleted
	 */
	private void delete(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (Throwable ignore) {
		}
	}

	/**
	 * Method {@code writeRequestToken} writes the Twitter Request Token
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param serializable
	 *            is the Serializable instance of the Twitter Request Token
	 */
	public void writeRequestToken(Serializable serializable) {
		writeSerializable(serializable, requestTokenFile);
	}

	/**
	 * Method {@code readRequestToken} reads the Twitter Request Token
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return a Serializable instance of the Twitter Request Token
	 */
	public Serializable readRequestToken() {
		return readSerializable(requestTokenFile);
	}

	/**
	 * Method {@code deleteRequestToken} deletes the Twitter Request Token
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void deleteRequestToken() {
		Path requestTokenPath = resolvePath(configDirectory, requestTokenFile);

		try {
			Files.delete(requestTokenPath);
		} catch (Throwable cause) {
			throw getApplicationStorageException(cause);
		}
	}

	/**
	 * Method {@code writeTwitterConfig} writes the Twitter Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param serializable
	 *            is the Serializable instance of the Twitter Config
	 */
	public void writeTwitterConfig(Serializable serializable) {
		writeSerializable(serializable, twitterConfigFile);
	}

	/**
	 * Method {@code readTwitterConfig} reads the Twitter Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return a Serializable instance of the Twitter Config
	 */
	public Serializable readTwitterConfig() {
		return readSerializable(twitterConfigFile);
	}

	/**
	 * Method {@code deleteTwitterConfig} deletes the Twitter Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void deleteTwitterConfig() {
		Path path = resolvePath(configDirectory, twitterConfigFile);
		delete(path);
	}

	/**
	 * Method {@code writeGoogleConfig} writes the Google Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param serializable
	 *            is the Serializable instance of the Google Config
	 */
	public void writeGoogleConfig(Serializable serializable) {
		writeSerializable(serializable, googleConfigFile);
	}

	/**
	 * Method {@code readGoogleConfig} reads the Google Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return a Serializable instance of the Google Config
	 */
	public Serializable readGoogleConfig() {
		return readSerializable(googleConfigFile);
	}

	/**
	 * Method {@code deleteGoogleConfig} deletes the Google Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void deleteGoogleConfig() {
		Path path = resolvePath(configDirectory, googleConfigFile);
		delete(path);
	}

	/**
	 * Method {@code writeSecondaryConfig} reads the Secondary Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param serializable
	 *            is the Serializable instance of the Secondary Config
	 */
	public void writeSecondaryConfig(Serializable serializable) {
		writeSerializable(serializable, secondaryConfigFile);
	}

	/**
	 * Method {@code readSecondaryConfig} reads the Secondary Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return a Serializable instance of the Secondary Config
	 */
	public Serializable readSecondaryConfig() {
		return readSerializable(secondaryConfigFile);
	}

	/**
	 * Method {@code deleteSecondaryConfig} deletes the Secondary Config
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void deleteSecondaryConfig() {
		Path path = resolvePath(configDirectory, secondaryConfigFile);
		delete(path);
	}

	/**
	 * Method {@code getApplicationDirectory} returns the path to the
	 * application directory
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return path to the application directory
	 */
	public Path getApplicationDirectory() {
		return appDirectory;
	}

	/**
	 * Method {@code getGoogleCredentialDir} returns the {@link File} which will
	 * be used a the Google Credential Store Directory.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the Google Credential Store Directory
	 */
	public File getGoogleCredentialDir() {
		return new File(configDirectory.toFile(), googleCrdentialDir);
	}

	/**
	 * Method {@code deleteGoogleCredentialDir} deletes the Google Credential
	 * Store Directory.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void deleteGoogleCredentialDir() {
		Path path = resolvePath(configDirectory, googleCrdentialDir);
		delete(path);
	}

	/**
	 * Method {@code addSecureLock} writes an empty file by which RPI determines
	 * if the Go secure mode was on before the application was shutdown.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @throws ApplicationStorageException
	 */
	public void addSecureLock() throws ApplicationStorageException {
		Path secureLockPath = resolvePath(appDirectory, secureLock);

		try {
			delete(secureLockPath);
			Files.createFile(secureLockPath);
		} catch (Throwable cause) {
			throw getApplicationStorageException(cause);
		}
	}

	/**
	 * Method {@code deleteSecureLock} deletes the Go Secure Lock.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @throws ApplicationStorageException
	 */
	public void deleteSecureLock() throws ApplicationStorageException {
		Path secureLockPath = resolvePath(appDirectory, secureLock);
		delete(secureLockPath);
	}

	/**
	 * Method {@code isSecureLocked} checks if the Go Secure Lock file exists or
	 * not
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return true if exists
	 * @throws ApplicationStorageException
	 */
	public boolean isSecureLocked() throws ApplicationStorageException {
		Path secureLockPath = resolvePath(appDirectory, secureLock);

		try {
			return Files.exists(secureLockPath);
		} catch (Throwable cause) {
			throw getApplicationStorageException(cause);
		}
	}

	/**
	 * Method {@code getMediaDirectory} returns the {@link File} which is used
	 * as the storage of the media files.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the media storage
	 */
	public File getMediaDirectory() {
		return mediaDirectory.toFile();
	}

	/**
	 * Method {@code getScriptDirectory} returns the {@link File} which is used
	 * as the storage of the script files.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the script storage
	 */
	public File getScriptDirectory() {
		return scriptDirectory.toFile();
	}

	/**
	 * Method {@code getStoreDirectory} returns the {@link File} which is used
	 * as the storage of the data files which are generated by RPI.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the data storage
	 */
	public File getStoreDirectory() {
		return storeDirectory.toFile();
	}
}