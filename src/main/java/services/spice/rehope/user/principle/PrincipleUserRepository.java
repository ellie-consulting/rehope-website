package services.spice.rehope.user.principle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.model.Repository;
import services.spice.rehope.user.auth.AuthProvider;

import java.sql.*;
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

    public Optional<PrincipleUser> getUserByEmail(@NotNull String email) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ? WHERE email = ?");
            statement.setString(1, TABLE);
            statement.setString(2, email);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToType(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.error("failed to get user by email {}", email);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<PrincipleUser> getUserByProviderId(@NotNull String providerId) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ? WHERE provider_id = ?");
            statement.setString(1, TABLE);
            statement.setString(2, providerId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToType(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.error("failed to get user by provider id {}", providerId);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    protected PrincipleUser mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        UserRole role = UserRole.valueOf(resultSet.getString("role"));
        AuthProvider authProvider = AuthProvider.valueOf(resultSet.getString("auth_provider")); // try
        String providerId = resultSet.getString("provider_id");
        Time accountCreated = resultSet.getTime("account_created");
        Time lastLogin = resultSet.getTime("last_login");

        return new PrincipleUser(id, name, email, role, authProvider, providerId, accountCreated, lastLogin);
    }

    @Override
    protected void createTableIfNotExists() {
        try {
            try (Connection connection = datasource.getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "username VARCHAR(255) NOT NULL UNIQUE," +
                        "email VARCHAR(255) NOT NULL UNIQUE," +
                        "role VARCHAR(25) NOT NULL DEFAULT USER," +
                        "auth_provider VARCHAR(25) NOT NULL," +
                        "provider_id VARCHAR(255) NOT NULL," +
                        "account_created TIMESTAMP NOT NULL DEFAULT NOW()," +
                        "last_login TIMESTAMP NOT NULL DEFAULT NOW()" +
                        ")").execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create users table", e);
        }
    }

}
