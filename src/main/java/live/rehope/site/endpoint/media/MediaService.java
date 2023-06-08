package live.rehope.site.endpoint.media;

import jakarta.inject.Inject;
import live.rehope.site.endpoint.creator.model.CreatorUpdateListener;
import live.rehope.site.endpoint.creator.model.MediaCreator;
import live.rehope.site.endpoint.media.model.VideoPublishEvent;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.rehope.site.endpoint.media.model.Media;

import java.util.*;

@Singleton
public class MediaService implements CreatorUpdateListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaService.class);

    private final MediaCache mediaCache;
    private final FeaturedMediaRepository featuredRepository;

    @Inject
    public MediaService(MediaCache mediaCache, FeaturedMediaRepository featuredRepository) {
        this.mediaCache = mediaCache;
        this.featuredRepository = featuredRepository;
    }

    /**
     * @return Featured media, this may be a cached result.
     */
    @NotNull
    public List<Media> getFeaturedMedia() {
        return featuredRepository.getAll();
    }

    /**
     * Add a featured media.
     * </br>
     * The url must be unique to the table.
     *
     * @param media Media to add.
     */
    public void addFeaturedMedia(@NotNull Media media) {
        featuredRepository.addMedia(media);
    }

    /**
     * Remove a featured media by feature id.
     *
     * @param featuredId Its feature id from the db.
     */
    public void removeFeaturedMediaById(int featuredId) {
        featuredRepository.removeMediaById(featuredId);
    }

    public void removeFeaturedMediaByUserId(int userId) {
        featuredRepository.removeMediaByUserId(userId);
    }

    /**
     * @return Get all current" cached live streams.
     */
    @NotNull
    public List<Media> getLiveStreams() {
        return mediaCache.getLiveStreams();
    }

    /**
     * Get the live stream of a user.
     *
     * @param userId User to get live stream of.
     * @return Their live stream.
     */
    public Optional<Media> getLiveStreamOf(int userId) {
        return mediaCache.getLiveStreamOf(userId);
    }

    /**
     * @return Get all current cached videos.
     */
    @NotNull
    public List<Media> getVideos() {
        return mediaCache.getVideos();
    }


    /**
     * Get cached videos of a user.
     *
     * @param userId User to get videos of.
     * @return Their cached videos.
     */
    @NotNull
    public List<Media> getVideosOf(int userId) {
        return mediaCache.getVideosOf(userId);
    }

    /**
     * Refresh the media of a user.
     *
     * @param userId User id to get content of.
     * @return If refreshed successfully.
     */
    public boolean refreshMediaOf(int userId) {
        return mediaCache.refresh(userId);
    }

    @Override
    public void onChannelsLoad(@NotNull List<MediaCreator> creators) {
        for (MediaCreator creator : creators) {
            mediaCache.setUserTracking(creator.userId(), creator.channelId());
        }
    }

    /**
     * Add a new YouTube connection to a user id.
     * </br>
     * Also subscribes the channel to the XML feed.
     *
     * @param userId User id.
     * @param channelId Channel id.
     */
    @Override
    public void onChannelAdd(int userId, @NotNull String channelId) {
        mediaCache.setUserTracking(userId, channelId);

        // Subscribe to channel
//        try {
//            mediaCache.getYouTubeClient().subscribeToChannel(channelId);
//        } catch (IOException e) {
//            LOGGER.error("failed to subscribe to channel for user {}", userId);
//            e.printStackTrace();
//        }
    }
    /**
     * Remove a YouTube connection to a user id.
     * And remove their content from featured.
     *
     * @param userId User id to remove connection of.
     */
    @Override
    public void onChannelRemove(int userId) {
        mediaCache.removeUserTracking(userId);
        featuredRepository.removeMediaByUserId(userId);
    }

    public void handleVideoPublishEvent(@NotNull VideoPublishEvent event) {
        mediaCache.refreshAll();
        // todo no???
    }

    public MediaCache getMediaCache() {
        return mediaCache;
    }
}
