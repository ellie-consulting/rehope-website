package live.rehope.site.endpoint.creator.model;

/**
 * A link between a user and their youtube channel.
 *
 * @param userId User id.
 * @param channelId Youtube Channel id.
 */
public record MediaCreator(int userId, String channelId) {
}
