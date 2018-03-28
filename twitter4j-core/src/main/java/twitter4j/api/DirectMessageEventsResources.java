package twitter4j.api;

import twitter4j.DirectMessageEvent;
import twitter4j.ResponseList;
import twitter4j.TwitterException;

/**
 * @author Ron Heft
 */
public interface DirectMessageEventsResources {

	/**
	 * Returns a list of the direct messages sent and received to the authenticating user.
	 * <br>This method calls https://api.twitter.com/1.1/direct_messages/events/list
	 *
	 * @return List
	 * @throws TwitterException when Twitter service or network is unavailable
	 * @see <a href="https://developer.twitter.com/en/docs/direct-messages/sending-and-receiving/api-reference/list-events">GET direct_messages/events/list | Twitter Developers</a>
	 */
	ResponseList<DirectMessageEvent> getDirectMessageEvents()
			throws TwitterException;


	/**
	 * Sends a new direct message to the specified user from the authenticating user.  Requires both the user and text parameters below.
	 * <br>This method calls https://api.twitter.com/1.1/direct_messages/events/new
	 *
	 * @param userId the screen name of the user to whom send the direct message
	 * @param text   The text of your direct message.
	 * @return DirectMessage
	 * @throws TwitterException when Twitter service or network is unavailable
	 * @see <a href="https://developer.twitter.com/en/docs/direct-messages/sending-and-receiving/api-reference/new-event">POST direct_messages/events/new | Twitter Developers</a>
	 */
	DirectMessageEvent sendDirectMessageEvent(long userId, String text)
			throws TwitterException;

}
