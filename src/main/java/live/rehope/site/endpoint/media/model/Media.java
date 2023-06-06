package live.rehope.site.endpoint.media.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents cached media of a user.
 *
 * @param featuredId Featured id.
 * @param userId Owning user id.
 * @param channelId Their channel id.
 * @param url The video url.
 * @param mediaType The media type, video or live stream.
 * @param publishedAt When the video was published.
 */
public record Media(int featuredId, int userId, @NotNull String channelId, @NotNull String url,
                    MediaType mediaType, long publishedAt) {

    public Media(int userId, @NotNull String channelId, @NotNull String url,
                 MediaType mediaType, long publishedAt) {
        this(-1, userId, channelId, url, mediaType, publishedAt);
    }

    public Media(int userId, @NotNull String channelId, @NotNull String url, MediaType mediaType) {
        this(userId, channelId, url, mediaType, System.currentTimeMillis());
    }

    public boolean isFeatured() {
        return featuredId > 0;
    }

}
