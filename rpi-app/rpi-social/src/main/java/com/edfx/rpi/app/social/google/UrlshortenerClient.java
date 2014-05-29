package com.edfx.rpi.app.social.google;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;

/**
 * Class {@code UrlshortenerClient} is the client which is used to shorten the
 * URL.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum UrlshortenerClient {
	INSTANCE;

	private final Logger logger = RpiLogger.getLogger(getClass());

	/**
	 * Constructor {@code UrlshortenerClient}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private UrlshortenerClient() {

	}

	/**
	 * Method {@code shrotenUrl} shorten the given URL with the help of
	 * {@link Urlshortener} service.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param urlshortener
	 *            is the instance of Urlshortener
	 * @param longUrl
	 *            is the URL which is need to be shorten
	 * @return the short form of the given URL.
	 * @see Urlshortener
	 * @throws IOException
	 */
	public String shrotenUrl(Urlshortener urlshortener, String longUrl) throws IOException {
		logger.info("Processing Url: " + longUrl);

		Url toInsert = new Url().setLongUrl(longUrl);
		Url url = urlshortener.url().insert(toInsert).execute();
		return url.getId();
	}
}
