package com.edfx.rpi.app.social.google;

import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.edfx.rpi.app.utils.logger.RpiLogger;
import com.edfx.rpi.app.utils.properties.PropertiesLoader;
import com.edfx.rpi.app.utils.properties.PropertiesLoader.Properties;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

/**
 * Class {@code GmailClient} is client to manipulate the mail sending and
 * receiving functionalities. It uses the {@code imap} and {@code smtp}
 * protocols provides the Google for sending and receiving mails.
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class GmailClient extends Observable {
	private final Logger logger = RpiLogger.getLogger(getClass());

	private final java.util.Properties gmailProperties;

	private final GoogleConfiguration configuration;

	private String username;
	private String password;
	private Session session;
	private MailReciever mailReciever;

	/**
	 * Constructor {@code GmailClient}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param configuration
	 *            is an instance of GoogleConfiguration
	 * @see GoogleConfiguration
	 */
	GmailClient(GoogleConfiguration configuration) {
		this.configuration = configuration;
		this.gmailProperties = PropertiesLoader.INSTANCE.getProperties(Properties.GMAIL);

		init();
	}

	/**
	 * Method {@code init} initializes the client.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private void init() {
		username = configuration.getRpiGmailAccount();
		password = configuration.getRpiGmailPassword();

		session = Session.getInstance(gmailProperties, new Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	/**
	 * Method {@code sendMail} is used to send mail. It sends mail to the RPI's
	 * gmail account.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param subject
	 *            is the subject of the mail
	 * @param body
	 *            is the body of the mail
	 */
	public void sendMail(String subject, String body) {
		sendMail(subject, body, configuration.getRpiGmailAccount());
	}

	/**
	 * Method {@code sendMail} is used to send mail.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param subject
	 *            is the subject of the mail
	 * @param body
	 *            is the body of the mail
	 * @param recipient
	 *            is the email address of the recipient
	 */
	public void sendMail(String subject, String body, String recipient) {
		logger.info("Sending mail...");

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			message.setSubject(subject);
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		logger.info("Mail sent...");
	}

	/**
	 * Method {@code startMailReceiver} starts the {@link MailReciever} to
	 * receive the mail.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void startMailReceiver() {
		mailReciever = new MailReciever();
	}

	/**
	 * Method {@code stopMailReceiver} stops the {@link MailReciever}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	public void stopMailReceiver() {
		mailReciever.idleThread.kill();
	}

	/**
	 * Method {@code setTokens} notifies and passes the Twitter Authorization
	 * Token and Google Authorization Token the observer of this class which is
	 * {@link GoogleManager}.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param tokens
	 * @see GoogleManager
	 */
	private void setTokens(String[] tokens) {
		logger.info("Sending token to process.");
		setChanged();
		notifyObservers(tokens);
	}

	/**
	 * Class {@code MailReciever} is used as a receiver of incoming mail. It
	 * polls in the Inbox of RPI's gmail account and if new mail arrives it
	 * process that mail for Twitter Authorization Token and Google
	 * Authorization Token.
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 */
	private class MailReciever {

		private IdleThread idleThread;

		/**
		 * Constructor {@code MailReciever}.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 */
		public MailReciever() {
			init();
		}

		/**
		 * Method {@code init} initializes the {@link MailReciever}.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 */
		private void init() {
			IMAPStore store = null;

			Folder inbox = null;

			try {
				store = (IMAPStore) session.getStore("imaps");
				store.connect(username, password);

				if (!store.hasCapability("IDLE")) {
					throw new RuntimeException("IDLE not supported");
				}

				inbox = (IMAPFolder) store.getFolder("INBOX");
				inbox.addMessageCountListener(new MessageCountAdapter() {

					@Override
					public void messagesAdded(MessageCountEvent event) {
						Message[] messages = event.getMessages();

						for (Message message : messages) {
							String from = StringUtils.EMPTY;

							try {
								from = InternetAddress.toString(message.getFrom());
								logger.info("Mail recieved with subject: " + message.getSubject() + " - From: " + from);
							} catch (Throwable cause) {
								logger.error(cause);
							}

							try {
								if (StringUtils.containsIgnoreCase(message.getSubject(), "RPI Authorization") || StringUtils.containsIgnoreCase(from, username)) {
									String content = getText(message);
									String sanitezedContent = Jsoup.parse(content).text();
									String[] tokens = processMessage(sanitezedContent);

									if (Objects.nonNull(tokens)) {
										setTokens(tokens);
									}
								}
							} catch (Throwable cause) {
								logger.error(cause);
							}
						}
					}
				});

				idleThread = new IdleThread(inbox);
				idleThread.setDaemon(false);
				idleThread.start();
			} catch (Exception cause) {
				logger.error(cause);
			} finally {
				close(inbox);
				close(store);
			}
		}

		/**
		 * Method {@code getText} process the mail content and takes out the
		 * text. Basically it convert {@link Part} to {@link String}.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @param part
		 *            is the mail content to be processed
		 * @return the string representation of the mail content
		 * @throws MessagingException
		 * @throws IOException
		 */
		private String getText(Part part) throws MessagingException, IOException {
			if (part.isMimeType("text/*")) {
				String s = (String) part.getContent();
				return s;
			}

			if (part.isMimeType("multipart/alternative")) {
				Multipart mp = (Multipart) part.getContent();
				String text = null;

				for (int i = 0; i < mp.getCount(); i++) {
					Part bp = mp.getBodyPart(i);

					if (bp.isMimeType("text/plain")) {
						if (text == null) {
							text = getText(bp);
						}

						continue;
					} else if (bp.isMimeType("text/html")) {
						String s = getText(bp);
						if (Objects.nonNull(s)) {
							return s;
						}
					} else {
						return getText(bp);
					}
				}

				return text;
			} else if (part.isMimeType("multipart/*")) {
				Multipart mp = (Multipart) part.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					String s = getText(mp.getBodyPart(i));

					if (Objects.nonNull(s)) {
						return s;
					}
				}
			}

			return null;
		}

		/**
		 * Method {@code processMessage} process the message and searches for
		 * Twitter Authorization Token and Google Authorization Token by using
		 * regular expression.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @param message
		 *            is the message to process
		 * @return an array of String which contains the Twitter Authorization
		 *         Token at the zeroth index and the Google Authorization Token
		 *         at the first index.
		 */
		private String[] processMessage(String message) {
			logger.info("Processing message");

			try {
				message = StringUtils.normalizeSpace(message);

				String googleToken = "";
				String twitterToken = "";

				String googleRegex = "(R|r)(P|p)(I|i) *(G|g)(O|o){2}(G|g)(L|l)(E|e) *(P|p)(I|i)(N|n) *(:) *[\\x00-\\x7F][^\\s]*";
				String twitterRegex = "(R|r)(P|p)(I|i) *(T|t)(W|w)(I|i)(T|t){2}(E|e)(R|r) *(P|p)(I|i)(N|n) *(:) *[0-9]*";

				Pattern pattern = Pattern.compile(googleRegex);

				Matcher matcher = pattern.matcher(message);

				String googlePart = "";

				if (matcher.find()) {
					googlePart = matcher.group();
				}

				if (StringUtils.isNotBlank(googlePart)) {
					googleRegex = "(R|r)(P|p)(I|i) *(G|g)(O|o){2}(G|g)(L|l)(E|e) *(P|p)(I|i)(N|n) *(:)";
					googleToken = StringUtils.trim(googlePart.split(googleRegex)[1]);
				}

				if (StringUtils.containsIgnoreCase(googleToken, "your_pin")) {
					googleToken = StringUtils.EMPTY;
				}

				pattern = Pattern.compile(twitterRegex);
				matcher = pattern.matcher(message);

				String twitterPart = "";

				if (matcher.find()) {
					twitterPart = matcher.group();
				}

				if (StringUtils.isNotBlank(twitterPart)) {
					twitterRegex = "(R|r)(P|p)(I|i) *(T|t)(W|w)(I|i)(T|t){2}(E|e)(R|r) *(P|p)(I|i)(N|n) *(:)";
					twitterToken = StringUtils.trim(twitterPart.split(twitterRegex)[1]);
				}

				if (StringUtils.isNotBlank(googleToken) || StringUtils.isNotBlank(twitterToken)) {
					return new String[] { googleToken, twitterToken };
				}
			} catch (Throwable cause) {
				logger.error(cause.getMessage(), cause);
			}

			return null;
		}

		/**
		 * Class {@code IdleThread} ensures that the Inbox remain open by
		 * passing IDLE Command to the server.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 */
		private class IdleThread extends Thread {

			private final Folder folder;
			private volatile boolean running = true;

			/**
			 * Constructor {@code IdleThread}.
			 * 
			 * @author Tapas Bose
			 * @since RPI V1.0
			 * @param folder
			 */
			public IdleThread(Folder folder) {
				this.folder = folder;
			}

			/**
			 * Method {@code kill} kills the running thread.
			 * 
			 * @author Tapas Bose
			 * @since RPI V1.0
			 */
			public synchronized void kill() {
				if (!running) {
					return;
				}

				this.running = false;
			}

			/**
			 * Method {@code run} runs the operation.
			 * 
			 * @author Tapas Bose
			 * @since RPI V1.0
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				while (running) {
					try {
						ensureOpen(folder);
						logger.info("Enter idle");
						((IMAPFolder) folder).idle();
					} catch (Throwable cause) {
						logger.error(cause);

						try {
							TimeUnit.MILLISECONDS.sleep(100);
						} catch (InterruptedException ignore) {
						}
					}
				}

				logger.info("Mail receiver stopped.");
			}
		}

		/**
		 * Method {@code close} closes the {@link Folder}.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @param folder
		 *            the instance of Folder
		 * @see Folder
		 */
		public void close(final Folder folder) {
			try {
				if (folder != null && folder.isOpen()) {
					folder.close(false);
				}
			} catch (Throwable ignore) {
			}

		}

		/**
		 * Method {@code close} closes the {@link Store}.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @param store
		 *            the instance of Store
		 * @see Store
		 */
		public void close(final Store store) {
			try {
				if (store != null && store.isConnected()) {
					store.close();
				}
			} catch (Throwable ignore) {
			}
		}

		/**
		 * Method {@code ensureOpen} ensures the {@link Folder} instance which
		 * has been passed into it, remains open.
		 * 
		 * @author Tapas Bose
		 * @since RPI V1.0
		 * @param folder
		 *            is the instance of Folder which needs to be opened.
		 * @throws MessagingException
		 */
		public void ensureOpen(final Folder folder) throws MessagingException {
			if (folder != null) {
				Store store = folder.getStore();
				if (store != null && !store.isConnected()) {
					store.connect(username, password);
				}
			} else {
				throw new MessagingException("Unable to open a null folder");
			}

			if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
				logger.info("Open folder " + folder.getFullName());

				folder.open(Folder.READ_ONLY);

				if (!folder.isOpen()) {
					throw new MessagingException("Unable to open folder " + folder.getFullName());
				}
			}

		}
	}
}
