package com.edfx.rpi.app.social.twitter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.edfx.rpi.app.social.Communicator;
import com.edfx.rpi.app.utils.logger.RpiLogger;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

/**
 * Class {@code DirectMessageListener}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
final class DirectMessageListener implements UserStreamListener {

	private final Logger logger = RpiLogger.getLogger(getClass());

	private final String userTwitterAccount;
	private String secondaryTwitterAccount;

	/**
	 * Constructor {@code DirectMessageListener}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param userTwitterAccount
	 */
	public DirectMessageListener(String userTwitterAccount) {
		this.userTwitterAccount = userTwitterAccount;
	}

	/**
	 * Method {@code setSecondaryTwitterAccount}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param secondaryTwitterAccount
	 */
	public void setSecondaryTwitterAccount(String secondaryTwitterAccount) {
		this.secondaryTwitterAccount = secondaryTwitterAccount;
	}

	/**
	 * Method {@code onStatus}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param status
	 * @see twitter4j.StatusListener#onStatus(twitter4j.Status)
	 */
	@Override
	public void onStatus(Status status) {

	}

	/**
	 * Method {@code onDeletionNotice}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param statusDeletionNotice
	 * @see twitter4j.StatusListener#onDeletionNotice(twitter4j.StatusDeletionNotice)
	 */
	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

	}

	/**
	 * Method {@code onTrackLimitationNotice}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param numberOfLimitedStatuses
	 * @see twitter4j.StatusListener#onTrackLimitationNotice(int)
	 */
	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

	}

	/**
	 * Method {@code onScrubGeo}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param userId
	 * @param upToStatusId
	 * @see twitter4j.StatusListener#onScrubGeo(long, long)
	 */
	@Override
	public void onScrubGeo(long userId, long upToStatusId) {

	}

	/**
	 * Method {@code onStallWarning}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param warning
	 * @see twitter4j.StatusListener#onStallWarning(twitter4j.StallWarning)
	 */
	@Override
	public void onStallWarning(StallWarning warning) {

	}

	/**
	 * Method {@code onException}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param ex
	 * @see twitter4j.StreamListener#onException(java.lang.Exception)
	 */
	@Override
	public void onException(Exception ex) {

	}

	/**
	 * Method {@code onDeletionNotice}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param directMessageId
	 * @param userId
	 * @see twitter4j.UserStreamListener#onDeletionNotice(long, long)
	 */
	@Override
	public void onDeletionNotice(long directMessageId, long userId) {

	}

	/**
	 * Method {@code onFriendList}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param friendIds
	 * @see twitter4j.UserStreamListener#onFriendList(long[])
	 */
	@Override
	public void onFriendList(long[] friendIds) {

	}

	/**
	 * Method {@code onFavorite}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param source
	 * @param target
	 * @param favoritedStatus
	 * @see twitter4j.UserStreamListener#onFavorite(twitter4j.User,
	 *      twitter4j.User, twitter4j.Status)
	 */
	@Override
	public void onFavorite(User source, User target, Status favoritedStatus) {

	}

	/**
	 * Method {@code onUnfavorite}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param source
	 * @param target
	 * @param unfavoritedStatus
	 * @see twitter4j.UserStreamListener#onUnfavorite(twitter4j.User,
	 *      twitter4j.User, twitter4j.Status)
	 */
	@Override
	public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

	}

	/**
	 * Method {@code onFollow}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param source
	 * @param followedUser
	 * @see twitter4j.UserStreamListener#onFollow(twitter4j.User,
	 *      twitter4j.User)
	 */
	@Override
	public void onFollow(User source, User followedUser) {

	}

	/**
	 * Method {@code onUnfollow}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param source
	 * @param unfollowedUser
	 * @see twitter4j.UserStreamListener#onUnfollow(twitter4j.User,
	 *      twitter4j.User)
	 */
	@Override
	public void onUnfollow(User source, User unfollowedUser) {

	}

	/**
	 * Method {@code onDirectMessage}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param directMessage
	 * @see twitter4j.UserStreamListener#onDirectMessage(twitter4j.DirectMessage)
	 */
	@Override
	public void onDirectMessage(DirectMessage directMessage) {
		String instruction = directMessage.getText();
		String senderScreenName = directMessage.getSender().getScreenName();
		String recipientScreenName = directMessage.getRecipient().getScreenName();

		if (StringUtils.equalsIgnoreCase(senderScreenName, userTwitterAccount) || StringUtils.equalsIgnoreCase(senderScreenName, secondaryTwitterAccount)) {
			logger.info("Message recieved: " + instruction + ". From: " + senderScreenName);
		} else {
			logger.info("Message sent: " + instruction + ". to: " + recipientScreenName);
		}

		if (StringUtils.endsWithIgnoreCase(senderScreenName, userTwitterAccount) || StringUtils.endsWithIgnoreCase(senderScreenName, secondaryTwitterAccount)) {
			logger.info("Instruction recieved: " + instruction + ". From: " + senderScreenName);

			try {
				Communicator.INSTANCE.communicate(instruction, senderScreenName);
			} catch (Throwable cause) {
				logger.error("Unable to process instruction: " + instruction + ". Reason: " + cause.getMessage());
				cause.printStackTrace();
			}
		}
	}

	/**
	 * Method {@code onUserListMemberAddition}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param addedMember
	 * @param listOwner
	 * @param list
	 * @see twitter4j.UserStreamListener#onUserListMemberAddition(twitter4j.User,
	 *      twitter4j.User, twitter4j.UserList)
	 */
	@Override
	public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

	}

	/**
	 * Method {@code onUserListMemberDeletion}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param deletedMember
	 * @param listOwner
	 * @param list
	 * @see twitter4j.UserStreamListener#onUserListMemberDeletion(twitter4j.User,
	 *      twitter4j.User, twitter4j.UserList)
	 */
	@Override
	public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

	}

	/**
	 * Method {@code onUserListSubscription}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param subscriber
	 * @param listOwner
	 * @param list
	 * @see twitter4j.UserStreamListener#onUserListSubscription(twitter4j.User,
	 *      twitter4j.User, twitter4j.UserList)
	 */
	@Override
	public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

	}

	/**
	 * Method {@code onUserListUnsubscription}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param subscriber
	 * @param listOwner
	 * @param list
	 * @see twitter4j.UserStreamListener#onUserListUnsubscription(twitter4j.User,
	 *      twitter4j.User, twitter4j.UserList)
	 */
	@Override
	public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

	}

	/**
	 * Method {@code onUserListCreation}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param listOwner
	 * @param list
	 * @see twitter4j.UserStreamListener#onUserListCreation(twitter4j.User,
	 *      twitter4j.UserList)
	 */
	@Override
	public void onUserListCreation(User listOwner, UserList list) {

	}

	/**
	 * Method {@code onUserListUpdate}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param listOwner
	 * @param list
	 * @see twitter4j.UserStreamListener#onUserListUpdate(twitter4j.User,
	 *      twitter4j.UserList)
	 */
	@Override
	public void onUserListUpdate(User listOwner, UserList list) {

	}

	/**
	 * Method {@code onUserListDeletion}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param listOwner
	 * @param list
	 * @see twitter4j.UserStreamListener#onUserListDeletion(twitter4j.User,
	 *      twitter4j.UserList)
	 */
	@Override
	public void onUserListDeletion(User listOwner, UserList list) {

	}

	/**
	 * Method {@code onUserProfileUpdate}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param updatedUser
	 * @see twitter4j.UserStreamListener#onUserProfileUpdate(twitter4j.User)
	 */
	@Override
	public void onUserProfileUpdate(User updatedUser) {

	}

	/**
	 * Method {@code onBlock}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param source
	 * @param blockedUser
	 * @see twitter4j.UserStreamListener#onBlock(twitter4j.User, twitter4j.User)
	 */
	@Override
	public void onBlock(User source, User blockedUser) {

	}

	/**
	 * Method {@code onUnblock}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param source
	 * @param unblockedUser
	 * @see twitter4j.UserStreamListener#onUnblock(twitter4j.User,
	 *      twitter4j.User)
	 */
	@Override
	public void onUnblock(User source, User unblockedUser) {

	}
}
