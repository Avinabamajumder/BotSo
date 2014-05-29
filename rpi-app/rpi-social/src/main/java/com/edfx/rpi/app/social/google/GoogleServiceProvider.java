package com.edfx.rpi.app.social.google;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerScopes;

/**
 * Class {@code GoogleServiceProvider} represents the provider of the following
 * Google Services:
 * <ul>
 * <li>Drive</li>
 * <li>URL Shortener</li>
 * </ul>
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum GoogleServiceProvider {
	INSTANCE;

	private static final String CLIENT_SECRET_FILE = "client_secret.json";
	private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE, UrlshortenerScopes.URLSHORTENER);

	private String applicationName;

	private File dataStoreDirectory;
	private DataStoreFactory dataStoreFactory;
	private HttpTransport transport;
	private JsonFactory jsonFactory;
	private GoogleClientSecrets clientSecrets;
	private GoogleAuthorizationCodeFlow authorizationCodeFlow;

	/**
	 * Constructor {@code GoogleServiceProvider}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private GoogleServiceProvider() {

	}

	/**
	 * Method {@code initialize} initializes the {@link GoogleServiceProvider}
	 * with the given Application name and an instance of a {@link File} which
	 * is treaded as the data store directory of this service provider.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param applicationName
	 *            is the name of the application to which the services will be
	 *            initialized.
	 * @param dataStoreDirectory
	 *            is the directory where the services store the credential
	 * 
	 * @see DataStoreFactory
	 * @see HttpTransport
	 * @see JsonFactory
	 * @see GoogleClientSecrets
	 * @see GoogleAuthorizationCodeFlow
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void initialize(String applicationName, File dataStoreDirectory) throws IOException, GeneralSecurityException {
		this.applicationName = applicationName;
		this.dataStoreDirectory = dataStoreDirectory;

		dataStoreFactory = getDataStoreFactory();
		transport = getHttpTransport();
		jsonFactory = getJsonFactory();
		clientSecrets = getClientSecrets(jsonFactory);
		authorizationCodeFlow = getAuthorizationCodeFlow(transport, jsonFactory, clientSecrets);
	}

	/**
	 * Method {@code getDataStoreFactory} create and returns the
	 * {@link DataStoreFactory}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of DataStoreFactory
	 * @see DataStoreFactory
	 * @throws IOException
	 */
	private DataStoreFactory getDataStoreFactory() throws IOException {
		return new FileDataStoreFactory(dataStoreDirectory);
	}

	/**
	 * 
	 * Method {@code getHttpTransport} create and returns the
	 * {@link HttpTransport}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of HttpTransport
	 * @see HttpTransport
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private HttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
		return GoogleNetHttpTransport.newTrustedTransport();
	}

	/**
	 * Method {@code getJsonFactory} create and returns the {@link JsonFactory}.
	 * It is an instance of {@link JacksonFactory}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of JsonFactory
	 */
	private JsonFactory getJsonFactory() {
		return JacksonFactory.getDefaultInstance();
	}

	/**
	 * 
	 * Method {@code getClientSecrets} create and returns
	 * {@link GoogleClientSecrets} by processing the client_secret.json file
	 * loaded as {@link InputStream}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param jsonFactory
	 * @return an instance of GoogleClientSecrets
	 * @see GoogleClientSecrets
	 * @throws IOException
	 */
	private GoogleClientSecrets getClientSecrets(JsonFactory jsonFactory) throws IOException {
		InputStream stream = getClass().getResourceAsStream(CLIENT_SECRET_FILE);
		InputStreamReader reader = new InputStreamReader(stream);
		return GoogleClientSecrets.load(jsonFactory, reader);
	}

	/**
	 * Method {@code getAuthorizationCodeFlow} create and returns
	 * {@link GoogleAuthorizationCodeFlow} by creating a
	 * {@link GoogleAuthorizationCodeFlow.Builder} with the help of the
	 * arguments passed into this method and with the scopes within which the
	 * Google OAuth 2.0 will work. <br/>
	 * Our current scopes are:
	 * <ul>
	 * <li>Drive</li>
	 * <li>URL Shortener</li>
	 * </ul>
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param transport
	 *            is the instance of HttpTransport
	 * @param jsonFactory
	 *            is the instance of JsonFactory
	 * @param clientSecrets
	 *            is the instance of GoogleClientSecrets
	 * @return an instance of GoogleAuthorizationCodeFlow
	 * @see HttpTransport
	 * @see JsonFactory
	 * @see GoogleClientSecrets
	 * @see GoogleAuthorizationCodeFlow
	 * @see GoogleAuthorizationCodeFlow.Builder
	 * @throws IOException
	 */
	private GoogleAuthorizationCodeFlow getAuthorizationCodeFlow(HttpTransport transport, JsonFactory jsonFactory, GoogleClientSecrets clientSecrets) throws IOException {
		GoogleAuthorizationCodeFlow.Builder authorizationCodeFlowBuilder = new GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, SCOPES);
		authorizationCodeFlowBuilder.setAccessType("offline");
		authorizationCodeFlowBuilder.setDataStoreFactory(dataStoreFactory);

		return authorizationCodeFlowBuilder.build();
	}

	/**
	 * Method {@code getCredential} returns an instance of OAuth 2.0
	 * {@link Credential} with the given Authorization Code and store it with
	 * the gived user id. This method also refreshes the OAuth 2.0
	 * Authentication Token when needed.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param authorizationCode
	 *            is the code which is used to generate the Credential
	 * @param userId
	 *            is the user id which is used to persist the Credential
	 * @return an instance of Credential
	 * @see Credential
	 * @throws GoogleConfigurationException
	 */
	public Credential getCredential(final String authorizationCode, final String userId) throws GoogleConfigurationException {
		try {
			Credential credential = authorizationCodeFlow.loadCredential(userId);

			if (credential == null) {
				GoogleAuthorizationCodeTokenRequest tokenRequest = authorizationCodeFlow.newTokenRequest(authorizationCode);
				tokenRequest.setRedirectUri(REDIRECT_URI);
				GoogleTokenResponse tokenResponse = tokenRequest.execute();
				credential = authorizationCodeFlow.createAndStoreCredential(tokenResponse, userId);
			} else {
				credential.refreshToken();
			}

			return credential;
		} catch (Throwable cause) {
			GoogleConfigurationException exception = new GoogleConfigurationException(cause);
			exception.initCause(cause);
			throw exception;
		}
	}

	/**
	 * Method {@code getUrlshortener} returns the {@link Urlshortener} service
	 * instance with he help of {@link Urlshortener.Builder} and the
	 * {@link Credential} object passed into it.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param credential
	 *            is the instance of Credential which is needed to create the
	 *            instance of Urlshortener
	 * 
	 * @return an instance of Urlshortener
	 * @see Credential
	 * @see Urlshortener
	 * @see Urlshortener.Builder
	 */
	public Urlshortener getUrlshortener(Credential credential) {
		Urlshortener.Builder urlShortnerBuilder = new Urlshortener.Builder(transport, jsonFactory, credential);
		urlShortnerBuilder.setApplicationName(applicationName);
		return urlShortnerBuilder.build();
	}

	/**
	 * Method {@code getDrive} returns the {@link Drive} service instance with
	 * he help of {@link Drive.Builder} and the {@link Credential} object passed
	 * into it.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param credential
	 *            is the instance of Credential which is needed to create the
	 *            instance of Drive
	 * @return an instance of Drive
	 * @see Credential
	 * @see Drive
	 * @see Drive.Builder
	 */
	public Drive getDrive(Credential credential) {
		Drive.Builder driveBuilder = new Drive.Builder(transport, jsonFactory, credential);
		driveBuilder.setApplicationName(applicationName);
		return driveBuilder.build();
	}
}
