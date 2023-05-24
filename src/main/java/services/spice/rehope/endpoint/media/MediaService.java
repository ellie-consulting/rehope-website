package services.spice.rehope.endpoint.media;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import services.spice.rehope.endpoint.media.model.Media;
import services.spice.rehope.endpoint.media.model.MediaCreator;
import services.spice.rehope.endpoint.user.principle.PrincipleUserService;

import java.util.*;

@Singleton
public class MediaService {

    private final MediaCache mediaCache;
    private final FeaturedMediaRepository featuredRepository;
    private final MediaChannelRepository channelRepository;
    private PrincipleUserService userService;

    public MediaService(MediaCache mediaCache,
                        FeaturedMediaRepository featuredRepository, MediaChannelRepository channelRepository,
                        PrincipleUserService userService) {
        this.mediaCache = mediaCache;
        this.featuredRepository = featuredRepository;
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @PostConstruct
    public void fetchWatchingUsers() {
        List<MediaCreator> creators = channelRepository.getAll();
        for (MediaCreator creator : creators) {
            mediaCache.setUserTracking(creator.userId(), creator.channelId());
        }
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
     * Remove a featured media by its url.
     *
     * @param url Url to remove.
     */
    public void removeFeaturedMedia(@NotNull String url) {
        featuredRepository.removeMedia(url);
    }

    /**
     * @return Get all current cached live streams.
     */
    @NotNull
    public List<Media> getLiveStreams() {
        return mediaCache.getLiveStreams();
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
     * Get the live stream of a user.
     *
     * @param userId User to get live stream of.
     * @return Their live stream.
     */
    public Optional<Media> getLiveStreamOf(int userId) {
        return mediaCache.getLiveStreamOf(userId);
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

    /**
     * @return Get all registered creators.
     */
    @NotNull
    public List<MediaCreator> getCreators() {
        return channelRepository.getAll();
    }

    /**
     * Get connection data from a user id.
     *
     * @param userId User id to get.
     * @return Their data.
     */
    public Optional<MediaCreator> getCreatorDataFromUserId(int userId) {
        return channelRepository.getDataFromUserId(userId);
    }

    /**
     * Get connection data from a channel id.
     *
     * @param channelId Channel id to get by.
     * @return Their data.
     */
    public Optional<MediaCreator> getDataFromChannelId(@NotNull String channelId) {
        return channelRepository.getDataFromChannelId(channelId);
    }

    /**
     * Add a new youtube connection to a user id.
     *
     * @param userId User id.
     * @param channelId Channel id.
     */
    public void addChannelId(int userId, @NotNull String channelId) {
        channelRepository.addChannelId(userId, channelId);
        mediaCache.setUserTracking(userId, channelId);
    }

    /**
     * Remove a YouTube connection to a user id.
     *
     * @param userId User id to remove connection of.
     */
    public void removeChannelId(int userId) {
        channelRepository.removeChannelId(userId);
        mediaCache.removeUserTracking(userId);
    }
}
