package live.rehope.site.endpoint.media.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Cached media profile of a user.
 */
public class MediaProfile {

    private final int userId;
    private final String channelId;

    private Media liveStream;
    private List<Media> videos;

    private Long lastRefresh;

    public MediaProfile(int userId, @NotNull String channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.videos = new LinkedList<>();
    }

    public int getUserId() {
        return userId;
    }

    @NotNull
    public String getChannelId() {
        return channelId;
    }

    public void setMedia(@NotNull List<Media> media) {
        this.videos.clear();
        this.liveStream = null;
        media.forEach(this::addMedia);
    }

    public void addMedia(@NotNull Media media) {
        if (media.mediaType() == MediaType.STREAM) {
            this.liveStream = media;
        } else {
            videos.add(media);
        }
    }

    @Nullable
    public Media getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(Media liveStream) {
        this.liveStream = liveStream;
    }

    @NotNull
    public List<Media> getVideos() {
        return videos;
    }

    public void setVideos(@NotNull List<Media> videos) {
        this.videos = videos;
    }

    public boolean hasBeenLoaded() {
        return lastRefresh != null;
    }

    @Nullable
    public Long getLastRefreshMillis() {
        return lastRefresh;
    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

}
