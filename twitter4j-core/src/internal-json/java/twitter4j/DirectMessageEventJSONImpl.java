package twitter4j;

import twitter4j.conf.Configuration;

import java.util.Date;

/**
 * A data class representing sent/received direct message event.
 *
 * @author Ron Heft
 */
/*package*/ final class DirectMessageEventJSONImpl extends TwitterResponseImpl implements DirectMessageEvent, java.io.Serializable {

	private static final long serialVersionUID = -4334099058193766307L;

	private String type;
	private long id;
	private Date createdTimestamp;
	private long initiatedViaTweetId;
	private long initiatedViaWelcomeMessageId;
	private long recipientId;
	private long senderId;
	private long sourceAppId;
	private String text;
	private UserMentionEntity[] userMentionEntities;
	private URLEntity[] urlEntities;
	private HashtagEntity[] hashtagEntities;
	private MediaEntity[] mediaEntities;
	private SymbolEntity[] symbolEntities;
	private MediaEntity attachmentEntity;

	/*package*/DirectMessageEventJSONImpl(HttpResponse res, Configuration conf) throws TwitterException {
		super(res);
		JSONObject json = res.asJSONObject();
		try {
			init(json.getJSONObject("event"));
		} catch ( JSONException jsone ) {
			throw new TwitterException(jsone);
		}
		if (conf.isJSONStoreEnabled()) {
			TwitterObjectFactory.clearThreadLocalMap();
			TwitterObjectFactory.registerJSONObject(this, json);
		}
	}

	/*package*/DirectMessageEventJSONImpl(JSONObject json) throws TwitterException {
		init(json);
	}

	private void init(JSONObject json) throws TwitterException {
		type = ParseUtil.getUnescapedString("type", json);
		id = ParseUtil.getLong("id", json);
		createdTimestamp = new Date(ParseUtil.getLong("created_timestamp", json));

		try {
			JSONObject messageCreate = json.getJSONObject("message_create");
			JSONObject target = messageCreate.getJSONObject("target");
			JSONObject messageData = messageCreate.getJSONObject("message_data");

			recipientId = ParseUtil.getLong("recipient_id", target);
			senderId = ParseUtil.getLong("sender_id", messageCreate);
			sourceAppId = ParseUtil.getLong("source_app_id", messageCreate);

			if ( !json.isNull("initiated_via") ) {
				JSONObject initiatedVia = json.getJSONObject("initiated_via");
				initiatedViaTweetId = ParseUtil.getLong("tweet_id", initiatedVia);
				initiatedViaWelcomeMessageId = ParseUtil.getLong("welcome_message_id", initiatedVia);
			}

			if (!messageData.isNull("entities")) {
				JSONObject entities = messageData.getJSONObject("entities");
				int len;
				if (!entities.isNull("user_mentions")) {
					JSONArray userMentionsArray = entities.getJSONArray("user_mentions");
					len = userMentionsArray.length();
					userMentionEntities = new UserMentionEntity[len];
					for (int i = 0; i < len; i++) {
						userMentionEntities[i] = new UserMentionEntityJSONImpl(userMentionsArray.getJSONObject(i));
					}

				}
				if (!entities.isNull("urls")) {
					JSONArray urlsArray = entities.getJSONArray("urls");
					len = urlsArray.length();
					urlEntities = new URLEntity[len];
					for (int i = 0; i < len; i++) {
						urlEntities[i] = new URLEntityJSONImpl(urlsArray.getJSONObject(i));
					}
				}

				if (!entities.isNull("hashtags")) {
					JSONArray hashtagsArray = entities.getJSONArray("hashtags");
					len = hashtagsArray.length();
					hashtagEntities = new HashtagEntity[len];
					for (int i = 0; i < len; i++) {
						hashtagEntities[i] = new HashtagEntityJSONImpl(hashtagsArray.getJSONObject(i));
					}
				}

				if (!entities.isNull("symbols")) {
					JSONArray symbolsArray = entities.getJSONArray("symbols");
					len = symbolsArray.length();
					symbolEntities = new SymbolEntity[len];
					for (int i = 0; i < len; i++) {
						// HashtagEntityJSONImpl also implements SymbolEntities
						symbolEntities[i] = new HashtagEntityJSONImpl(symbolsArray.getJSONObject(i));
					}
				}

				if (!entities.isNull("media")) {
					JSONArray mediaArray = entities.getJSONArray("media");
					len = mediaArray.length();
					mediaEntities = new MediaEntity[len];
					for (int i = 0; i < len; i++) {
						mediaEntities[i] = new MediaEntityJSONImpl(mediaArray.getJSONObject(i));
					}
				}
			}

			if (!messageData.isNull("attachment")) {
				JSONObject attachment = messageData.getJSONObject("attachment");
				attachmentEntity = new MediaEntityJSONImpl(attachment.getJSONObject("media"));
			}

			userMentionEntities = userMentionEntities == null ? new UserMentionEntity[0] : userMentionEntities;
			urlEntities = urlEntities == null ? new URLEntity[0] : urlEntities;
			hashtagEntities = hashtagEntities == null ? new HashtagEntity[0] : hashtagEntities;
			symbolEntities = symbolEntities == null ? new SymbolEntity[0] : symbolEntities;
			mediaEntities = mediaEntities == null ? new MediaEntity[0] : mediaEntities;
			text = HTMLEntity.unescapeAndSlideEntityIncdices(messageData.getString("text"), userMentionEntities,
					urlEntities, hashtagEntities, mediaEntities);
		} catch (JSONException jsone) {
			throw new TwitterException(jsone);
		}
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	@Override
	public long getInitiatedViaTweetId() {
		return initiatedViaTweetId;
	}

	@Override
	public long getInitiatedViaWelcomeMessageId() {
		return initiatedViaWelcomeMessageId;
	}

	@Override
	public long getRecipientId() {
		return recipientId;
	}

	@Override
	public long getSenderId() {
		return senderId;
	}

	@Override
	public long getSourceAppId() {
		return sourceAppId;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		return userMentionEntities;
	}

	@Override
	public URLEntity[] getURLEntities() {
		return urlEntities;
	}

	@Override
	public HashtagEntity[] getHashtagEntities() {
		return hashtagEntities;
	}

	@Override
	public MediaEntity[] getMediaEntities() {
		return mediaEntities;
	}

	@Override
	public SymbolEntity[] getSymbolEntities() {
		return symbolEntities;
	}

	@Override
	public MediaEntity getAttachmentEntity() {
		return attachmentEntity;
	}

	/*package*/
	static ResponseList<DirectMessageEvent> createDirectMessageEventList(HttpResponse res, Configuration conf) throws TwitterException {
		try {
			if (conf.isJSONStoreEnabled()) {
				TwitterObjectFactory.clearThreadLocalMap();
			}
			JSONObject object = res.asJSONObject();
			JSONArray list = object.getJSONArray("events");
			int size = list.length();
			ResponseList<DirectMessageEvent> directMessageEvents = new ResponseListImpl<DirectMessageEvent>(size, res);
			for (int i = 0; i < size; i++) {
				JSONObject json = list.getJSONObject(i);
				DirectMessageEvent directMessageEvent = new DirectMessageEventJSONImpl(json);
				directMessageEvents.add(directMessageEvent);
				if (conf.isJSONStoreEnabled()) {
					TwitterObjectFactory.registerJSONObject(directMessageEvent, json);
				}
			}
			if (conf.isJSONStoreEnabled()) {
				TwitterObjectFactory.registerJSONObject(directMessageEvents, list);
			}
			return directMessageEvents;
		} catch (JSONException jsone) {
			throw new TwitterException(jsone);
		}
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		return obj instanceof DirectMessageEvent && ((DirectMessageEvent) obj).getId() == this.id;
	}

}
