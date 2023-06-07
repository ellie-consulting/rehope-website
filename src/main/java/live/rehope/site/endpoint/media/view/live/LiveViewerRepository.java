package live.rehope.site.endpoint.media.view.live;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import live.rehope.site.datasource.PostgreDatasource;
import live.rehope.site.model.Repository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

@Singleton
public class LiveViewerRepository extends Repository<LiveViewer> {
    private static final String TABLE = "live_viewers";
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveViewerRepository.class);

    @Inject
    public LiveViewerRepository(PostgreDatasource datasource) {
        super(datasource);
    }

    @Override
    public String getTable() {
        return TABLE;
    }

    public void registerUserWatching(int userId, String videoId) {

    }

    public Optional<LiveViewer> getCurrentVideo(int userId) {
        return getByField("user_id", userId);
    }

    public Optional<LiveViewer> popCurrentVideo(int userId) {
        return Optional.empty();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected LiveViewer mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("user_id");
        String videoId = resultSet.getString("video_id");
        Timestamp startedAt = resultSet.getTimestamp("started_at");
        return new LiveViewer(userId, videoId, startedAt);
    }

    @Override
    protected void createTableIfNotExists() {

    }
}
