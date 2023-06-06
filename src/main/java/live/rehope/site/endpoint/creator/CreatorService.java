package live.rehope.site.endpoint.creator;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import live.rehope.site.endpoint.creator.model.CreatorUpdateListener;
import live.rehope.site.endpoint.creator.model.MediaCreator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Handles links between users and media channels that are monitored.
 */
@Singleton
public class CreatorService {

    private final MediaChannelRepository repository;
    private final CreatorUpdateListener listener;

    @Inject
    public CreatorService(MediaChannelRepository repository, CreatorUpdateListener listener) {
        this.repository = repository;
        this.listener = listener;
    }

    @PostConstruct
    public void onLoad() {
        listener.onChannelsLoad(repository.getAll());
    }

    /**
     * @return All linked creators.
     */
    @NotNull
    public List<MediaCreator> getCreators() {
        return repository.getAll();
    }

    /**
     * Get a creator by the user id.
     *
     * @param userId User id.
     * @return Creator.
     */
    public Optional<MediaCreator> getCreator(int userId) {
        return repository.getDataFromUserId(userId);
    }

    /**
     * Link a channel and notify a listener.
     *
     * @param userId User id.
     * @param channelId Channel id.
     */
    public void linkChannel(int userId, @NotNull String channelId) {
        repository.addChannelId(userId, channelId);

        // this will not run unless the above code is success
        listener.onChannelAdd(userId, channelId);
    }

    /**
     * Unlink a channel by the user id.
     * Also notifies the listener of removal.
     *
     * @param userId User id.
     */
    public void unlinkChannel(int userId) {
        repository.removeChannelId(userId);

        listener.onChannelRemove(userId);
    }

}
