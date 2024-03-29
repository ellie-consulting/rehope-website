package live.rehope.site.endpoint.user.principle;

import live.rehope.site.datasource.PostgreDatasource;
import live.rehope.site.endpoint.user.auth.AuthProviderSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import live.rehope.site.endpoint.user.principle.model.PrincipleUser;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.rehope.site.datasource.exception.DatabaseError;
import live.rehope.site.model.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Primary data handling for users.
 */
@Singleton
public class PrincipleUserRepository extends Repository<PrincipleUser> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrincipleUserRepository.class);
    public static final String TABLE = "users";

    @Inject
    public PrincipleUserRepository(PostgreDatasource datasource) {
        super(datasource);
    }

    @Override
    public String getTable() {
        return TABLE;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @NotNull
    public List<PrincipleUser> getUsersByRole(@NotNull UserRole role) {
        return getAllWithFilter("role", role.name());
    }

    @NotNull
    public Optional<PrincipleUser> getUserByEmail(@NotNull String email) {
        return getByField("email", email);
    }

    @NotNull
    public Optional<PrincipleUser> getUserByUsername(@NotNull String username) {
        return getByField("username", username);
    }

    @NotNull
    public Optional<PrincipleUser> getUserByProviderId(@NotNull String providerId) {
        return getByField("provider_id", providerId);
    }

    public PrincipleUser createUser(@NotNull PrincipleUser user) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("INSERT INTO " + TABLE + " (username, email, role, " +
                            "auth_provider, provider_id, account_created, last_login) VALUES (?, ?, ?, ?, ?, ?, ?)" +
                            "RETURNING id");

            statement.setObject(1, user.username());
            statement.setString(2, user.email());
            statement.setString(3, user.role().name());
            statement.setString(4, user.authSource().name());
            statement.setString(5, user.providerId());
            statement.setTimestamp(6, user.accountedCreated());
            statement.setTimestamp(7, user.lastLogin());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                return user.copyWithId(resultSet.getInt("id"));
            }

            throw new DatabaseError();
        } catch (SQLException e) {
            LOGGER.error("failed to create a user");
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    public void setUsername(int id, String newUsername) {
        updateFieldById(id, "username", newUsername);
    }

    public void updateLastLogin(int id) {
        updateFieldById(id, "last_login", Timestamp.valueOf(LocalDateTime.now()));
    }

    @Override
    protected PrincipleUser mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("username");
        String email = resultSet.getString("email");
        UserRole role = UserRole.valueOf(resultSet.getString("role"));
        AuthProviderSource authProviderSource = AuthProviderSource.valueOf(resultSet.getString("auth_provider")); // try
        String providerId = resultSet.getString("provider_id");
        Timestamp accountCreated = resultSet.getTimestamp("account_created");
        Timestamp lastLogin = resultSet.getTimestamp("last_login");

        return new PrincipleUser(id, name, email, role, authProviderSource, providerId, accountCreated, lastLogin);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "username VARCHAR(16) UNIQUE," +
                    "email VARCHAR(255) NOT NULL UNIQUE," +
                    "role VARCHAR(25) NOT NULL DEFAULT 'USER'," +
                    "auth_provider VARCHAR(25) NOT NULL," +
                    "provider_id VARCHAR(255) NOT NULL," +
                    "account_created TIMESTAMP NOT NULL DEFAULT NOW()," +
                    "last_login TIMESTAMP NOT NULL DEFAULT NOW()" +
                    ")").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create users table", e);
        }
    }

}
