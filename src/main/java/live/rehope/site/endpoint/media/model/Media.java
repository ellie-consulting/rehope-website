package live.rehope.site.endpoint.media.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents cached media of a user.
 *
 * @param featuredId Featured id.
 * @param userId Owning user id.
 * @param channelId Their channel id.
 * @param videoId Video id.
 * @param mediaType The media type, video or live stream.
 * @param publishedAt When the video was published.
 */
public record Media(int featuredId, int userId, @NotNull String channelId,
                    @NotNull String videoId, MediaType mediaType, long publishedAt) {

    private static final String VIDEO_URL = "https://youtu.be/%s";

    public Media(int userId, @NotNull String channelId, @NotNull String videoId,
                 MediaType mediaType, long publishedAt) {
        this(-1, userId, channelId, videoId, mediaType, publishedAt);
    }

    public Media(int userId, @NotNull String channelId, @NotNull String videoId, MediaType mediaType) {
        this(userId, channelId, videoId, mediaType, System.currentTimeMillis());
    }

    public boolean isFeatured() {
        return featuredId > 0;
    }

    public String url() {
        return VIDEO_URL.formatted(videoId);
    }

}
