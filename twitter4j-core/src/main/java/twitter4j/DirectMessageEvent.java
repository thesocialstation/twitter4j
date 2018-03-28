package twitter4j;

import java.util.Date;

/**
 * A data interface representing sent/received direct message event.
 *
 * @author Ron Heft
 */
public interface DirectMessageEvent extends TwitterResponse, EntitySupport, java.io.Serializable {

	String getType();

	long getId();

	Date getCreatedTimestamp();

	long getInitiatedViaTweetId();

	long getInitiatedViaWelcomeMessageId();

	long getRecipientId();

	long getSenderId();

	long getSourceAppId();

	String getText();

	MediaEntity getAttachmentEntity();

}
