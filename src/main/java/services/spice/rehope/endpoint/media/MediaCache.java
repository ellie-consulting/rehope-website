package services.spice.rehope.endpoint.media;

import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import services.spice.rehope.endpoint.media.model.Media;
import services.spice.rehope.endpoint.media.model.MediaProfile;
import services.spice.rehope.endpoint.media.youtube.YouTubeClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * In-memory repository of media content.
 */
@Singleton
public class MediaCache {
    private static final long MIN_REFRESH_TIME = TimeUnit.MINUTES.toMillis(5);

    private final YouTubeClient youTubeClient;
    // User Id, Profile
    private final Map<Integer, MediaProfile> profiles = new HashMap<>();

    public MediaCache(YouTubeClient youTubeClient) {
        this.youTubeClient = youTubeClient;
    }

    /**
     * Add a user for tracking.
     *
     * @param userId User id.
     * @param channelId YouTube channel id.
     */
    public void setUserTracking(int userId, @NotNull String channelId) {
        profiles.computeIfAbsent(userId, integer -> new MediaProfile(userId, channelId));
    }

    /**
     * Remove a user's cached profile.
     *
     * @param userId User id to remove.
     */
    public void removeUserTracking(int userId) {
        profiles.remove(userId);
    }

    /**
     * Get all live streams.
     *
     * @return Live streams.
     */
    @NotNull
    public List<Media> getLiveStreams() {
        return profiles.values().stream()
                .map(MediaProfile::getLiveStream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Get videos of a user id.
     *
     * @param userId User id to get.
     * @return A list of media.
     */
    @NotNull
    public List<Media> getVideosOf(int userId) {
        MediaProfile profile = profiles.get(userId);
        if (profile == null) {
            return List.of();
        }

        return profile.getVideos();
    }

    /**
     * Get the live stream of a user id.
     *
     * @param userId User id to get of.
     * @return Any livestream.
     */
    public Optional<Media> getLiveStreamOf(int userId) {
        MediaProfile profile = profiles.get(userId);
        if (profile == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(profile.getLiveStream());
    }

    /**
     * Refresh a user's profile.
     * </br>
     * Will not refresh if it was refreshed within MIN_REFRESH_TIME.
     *
     * @param userId User id to refresh.
     */
    public boolean refresh(int userId) {
        MediaProfile profile = profiles.get(userId);
        if (profile == null || !canRefresh(profile)) {
            return false;
        }

        try {
            // don't filter for date if live, because we need to know if it finished.
            boolean newContentOnly = profile.getLiveStream() == null;

            List<Media> content = youTubeClient.getChannelContent(
                    profile.getUserId(), profile.getChannelId(), 10,
                    newContentOnly ? profile.getLastRefresh() : null
            );

            profile.setLastRefresh(System.currentTimeMillis());

            if (newContentOnly) {
                if (content.isEmpty()) {
                    return true;
                }

                content.forEach(profile::addMedia);
            } else {
                profile.setMedia(content);
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to refresh content of " + userId, e);
        }

        return true;
    }

    /**
     * Refresh all profiles.
     *
     * @see MediaCache#refresh(int)
     */
    public void refreshAll() {
        profiles.keySet().forEach(userId -> {
            refresh(userId);
            // todo delay
        });
    }

    private boolean canRefresh(MediaProfile profile) {
        return System.currentTimeMillis() - profile.getLastRefreshMillis() > MIN_REFRESH_TIME;
    }

}
