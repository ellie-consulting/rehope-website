package live.rehope.site.endpoint.creator.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Listener for listening to creator events.
 */
public interface CreatorUpdateListener {

    /**
     * Called when the creator service is ready.
     *
     * @param creators Creators to monitor.
     */
    void onChannelsLoad(@NotNull List<MediaCreator> creators);

    /**
     * When a channel is added.
     *
     * @param userId User id.
     * @param channelId The youtube channel id.
     */
    void onChannelAdd(int userId, @NotNull String channelId);

    /**
     * When a creator channel is removed.
     *
     * @param userId User id.
     */
    void onChannelRemove(int userId);

}
