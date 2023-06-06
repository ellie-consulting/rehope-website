package live.rehope.site.endpoint.media.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents cached media of a user.
 *
 * @param userId Owning user id.
 * @param channelId Their channel id.
 * @param url The video url.
 * @param mediaType The media type, video or live stream.
 * @param publishedAt When the video was published.
 */
public record Media(int userId, @NotNull String channelId, @NotNull String url, MediaType mediaType, long publishedAt) {
}
