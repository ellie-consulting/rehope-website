package services.spice.rehope.endpoint.user.principle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.endpoint.user.auth.AuthProviderSource;
import services.spice.rehope.model.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Data handling for users.
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
    public Optional<PrincipleUser> getUserByProviderId(@NotNull String providerId) {
        return getByField("provider_id", providerId);
    }

    public boolean createUser(@NotNull PrincipleUser user) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("INSERT INTO " + TABLE + " (username, email, role, " +
                            "auth_provider, provider_id, account_created, last_login) VALUES (?, ?, ?, ?, ?, ?, ?)" +
                            "RETURNING id");

            statement.setObject(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getRole().name());
            statement.setString(4, user.getAuthProvider().name());
            statement.setString(5, user.getProviderId());
            statement.setTime(6, user.getAccountCreated());
            statement.setTime(7, user.getLastLogin());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                user.setId(resultSet.getInt("id"));

                return true;
            }

        } catch (SQLException e) {
            LOGGER.error("failed to create a user");
            e.printStackTrace();
        }

        return false;
    }

    public boolean setUsername(int id, String newUsername) {
        return updateFieldById(id, "username", newUsername);
    }

    public boolean updateLastLogin(int id) {
        return updateFieldById(id, "last_login", Time.from(Instant.now()));
    }

    @Override
    protected PrincipleUser mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("username");
        String email = resultSet.getString("email");
        UserRole role = UserRole.valueOf(resultSet.getString("role"));
        AuthProviderSource authProviderSource = AuthProviderSource.valueOf(resultSet.getString("auth_provider")); // try
        String providerId = resultSet.getString("provider_id");
        Time accountCreated = resultSet.getTime("account_created");
        Time lastLogin = resultSet.getTime("last_login");

        return new PrincipleUser(id, name, email, role, authProviderSource, providerId, accountCreated, lastLogin);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "username VARCHAR(255) UNIQUE," +
                    "email VARCHAR(255) NOT NULL UNIQUE," +
                    "role VARCHAR(25) NOT NULL DEFAULT USER," +
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
