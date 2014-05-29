package com.edfx.rpi.app.social.google;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;

/**
 * Class {@code DriveClient} is the client to manipulate various operations with
 * Google Drive
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum DriveClient {

	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());

	/**
	 * Constructor {@code DriveClient}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private DriveClient() {

	}

	/**
	 * Method {@code uploadVideo} uploads a given video file in Google Drive and
	 * also share the video with the given person. The video is private by
	 * default.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param drive
	 *            is the instance of Drive
	 * @param content
	 *            is video which will be uploaded
	 * @param shareWith
	 *            is the email address of the person with whom the video will be
	 *            shared
	 * @return the URL to uploaded video
	 * @throws IOException
	 * @see Drive
	 */
	public String uploadVideo(Drive drive, java.io.File content, String shareWith) throws IOException {
		Date now = new Date();
		DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");

		String fileName = content.getName();
		int indexOfDot = StringUtils.lastIndexOf(fileName, ".");

		String name = StringUtils.substring(fileName, 0, indexOfDot);
		String extension = StringUtils.substring(fileName, indexOfDot + 1, fileName.length());

		StringBuilder fileNameBuilder = new StringBuilder(name);
		fileNameBuilder.append("_").append(now.getTime());
		fileNameBuilder.append(".").append(extension);
		fileName = fileNameBuilder.toString();

		String description = "Video taken by RPI at @" + formatter.format(now);

		File file = new File().setTitle(fileName).setDescription(description).setMimeType("video/*");
		FileContent mediaContent = new FileContent("video/*", content);

		File uploadedFile = drive.files().insert(file, mediaContent).execute();

		Permission permission = new Permission().setValue(shareWith).setRole("reader").setType("user");

		drive.permissions().insert(uploadedFile.getId(), permission).setSendNotificationEmails(false).execute();

		String url = uploadedFile.getAlternateLink();

		return url;
	}

	/**
	 * Method {@code uploadImages} uploads given images in Google Drive and
	 * place these image files into an album with the specified name. It also
	 * share the album with the given person. The album is private by default.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param drive
	 *            is the instance of Drive
	 * @param contents
	 *            are the images which will be uploaded
	 * @param albumName
	 *            is the name of the album
	 * @param shareWith
	 *            is the email address of the person with whom the video will be
	 *            shared
	 * @return the URL to uploaded album
	 * @throws IOException
	 * @see Drive
	 */
	public String uploadImages(final Drive drive, final java.io.File[] contents, String albumName, String shareWith) throws IOException {
		Date now = new Date();
		DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");
		String folderName = albumName + " - " + formatter.format(now);
		String description = "Images taken by RPI at @" + formatter.format(now);

		File folder = new File().setTitle(folderName).setDescription(description).setMimeType("application/vnd.google-apps.folder");

		File uploadedFolder = drive.files().insert(folder).execute();

		final String folderId = uploadedFolder.getId();

		Arrays.asList(contents).forEach(content -> {
			ParentReference parent = new ParentReference().setId(folderId);

			File file = new File().setTitle(content.getName()).setMimeType("image/*").setParents(Arrays.asList(parent));

			FileContent mediaContent = new FileContent("image/*", content);

			try {
				drive.files().insert(file, mediaContent).execute();
			} catch (Throwable cause) {
				logger.error(cause);
			}
		});

		Permission permission = new Permission().setValue(shareWith).setRole("reader").setType("user");

		drive.permissions().insert(folderId, permission).setSendNotificationEmails(false).execute();

		String url = uploadedFolder.getAlternateLink();

		return url;
	}
}
