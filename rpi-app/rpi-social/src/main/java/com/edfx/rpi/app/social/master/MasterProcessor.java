package com.edfx.rpi.app.social.master;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.edfx.rpi.app.social.google.GoogleConfiguration;
import com.edfx.rpi.app.social.twitter.TwitterConfiguration;
import com.edfx.rpi.app.utils.config.SecondaryUserConfiguration;
import com.edfx.rpi.app.utils.storage.ApplicationStorageManager;

/**
 * Class {@code MasterProcessor}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public enum MasterProcessor {

	INSTANCE;

	private final ApplicationStorageManager applicationStorageManager = ApplicationStorageManager.INSTANCE;

	/**
	 * Constructor {@code MasterProcessor}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private MasterProcessor() {

	}

	/**
	 * Method {@code getMaster} returns the current master
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return the current master
	 */
	public Master getMaster() {
		Master master = new Master();

		SecondaryUserConfiguration secondaryUserConfiguration = SecondaryUserConfiguration.class.cast(applicationStorageManager.readSecondaryConfig());

		if (Objects.nonNull(secondaryUserConfiguration) && secondaryUserConfiguration.isActive()) {
			master.setGoogleAccount(secondaryUserConfiguration.getGoogleAccount());
			master.setTwitterAccount(secondaryUserConfiguration.getTwitterAccount());
		} else {
			GoogleConfiguration googleConfiguration = GoogleConfiguration.class.cast(applicationStorageManager.readGoogleConfig());
			TwitterConfiguration twitterConfiguration = TwitterConfiguration.class.cast(applicationStorageManager.readTwitterConfig());

			master.setGoogleAccount(googleConfiguration.getUserGmailAccount());
			master.setTwitterAccount(twitterConfiguration.getUserAccount());
		}

		return master;
	}

	/**
	 * Method {@code getMasterType} returns the {@link MasterType} from the
	 * given twitter handler
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterHandler
	 *            to check
	 * @return an instance of MasterType or null if not matched
	 */
	public MasterType getMasterType(String twitterHandler) {
		SecondaryUserConfiguration secondaryUserConfiguration = SecondaryUserConfiguration.class.cast(applicationStorageManager.readSecondaryConfig());
		TwitterConfiguration twitterConfiguration = TwitterConfiguration.class.cast(applicationStorageManager.readTwitterConfig());

		if (Objects.nonNull(secondaryUserConfiguration)) {
			if (StringUtils.equalsIgnoreCase(twitterHandler, secondaryUserConfiguration.getTwitterAccount())) {
				return MasterType.SCONDARY;
			}
		}

		if (Objects.nonNull(twitterConfiguration)) {
			if (StringUtils.equalsIgnoreCase(twitterHandler, twitterConfiguration.getUserAccount())) {
				return MasterType.PRIMARY;
			}
		}

		return null;
	}

	/**
	 * Method {@code getCurrentMasterType} returns if the current master is a
	 * secondary or primary master
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of MasterType
	 */
	public MasterType getCurrentMasterType() {
		SecondaryUserConfiguration secondaryUserConfiguration = SecondaryUserConfiguration.class.cast(applicationStorageManager.readSecondaryConfig());

		if (Objects.nonNull(secondaryUserConfiguration) && secondaryUserConfiguration.isActive()) {
			return MasterType.SCONDARY;
		} else {
			return MasterType.PRIMARY;
		}
	}

	/**
	 * Method {@code getPrimaryMaster} returns the primary {@link Master}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of Master
	 */
	public Master getPrimaryMaster() {
		Master master = new Master();

		GoogleConfiguration googleConfiguration = GoogleConfiguration.class.cast(applicationStorageManager.readGoogleConfig());
		TwitterConfiguration twitterConfiguration = TwitterConfiguration.class.cast(applicationStorageManager.readTwitterConfig());

		master.setGoogleAccount(googleConfiguration.getUserGmailAccount());
		master.setTwitterAccount(twitterConfiguration.getUserAccount());

		return master;
	}

	/**
	 * Method {@code getSecondaryMaster} returns the secondary {@link Master}
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @return an instance of Master or null id secondary master doesn't exist
	 */
	public Master getSecondaryMaster() {
		SecondaryUserConfiguration secondaryUserConfiguration = SecondaryUserConfiguration.class.cast(applicationStorageManager.readSecondaryConfig());

		if (Objects.nonNull(secondaryUserConfiguration) && secondaryUserConfiguration.isActive()) {
			Master master = new Master();
			master.setGoogleAccount(secondaryUserConfiguration.getGoogleAccount());
			master.setTwitterAccount(secondaryUserConfiguration.getTwitterAccount());

			return master;
		}

		return null;
	}

	/**
	 * Method {@code getMaster} returns the {@link Master} from the
	 * given twitter handler
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param twitterHandler
	 *            to check
	 * @return an instance of Master or null if not matched
	 */
	public Master getMaster(String twitterHandler) {
		SecondaryUserConfiguration secondaryUserConfiguration = SecondaryUserConfiguration.class.cast(applicationStorageManager.readSecondaryConfig());

		if (Objects.nonNull(secondaryUserConfiguration) && StringUtils.equalsIgnoreCase(twitterHandler, secondaryUserConfiguration.getTwitterAccount())) {
			Master master = new Master();
			master.setGoogleAccount(secondaryUserConfiguration.getGoogleAccount());
			master.setTwitterAccount(secondaryUserConfiguration.getTwitterAccount());

			return master;
		} else {
			Master master = new Master();

			GoogleConfiguration googleConfiguration = GoogleConfiguration.class.cast(applicationStorageManager.readGoogleConfig());
			TwitterConfiguration twitterConfiguration = TwitterConfiguration.class.cast(applicationStorageManager.readTwitterConfig());

			master.setGoogleAccount(googleConfiguration.getUserGmailAccount());
			master.setTwitterAccount(twitterConfiguration.getUserAccount());

			return master;
		}
	}
}
