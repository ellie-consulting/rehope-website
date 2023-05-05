package services.spice.rehope.inventory.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.inventory.element.ElementRepository;
import services.spice.rehope.model.Repository;
import services.spice.rehope.user.principle.PrincipleUserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores user's inventory.
 */
@Singleton
public class UserInventoryRepository extends Repository<UserInventoryElement> {
    private static final String TABLE = "user_inventory";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInventoryRepository.class);

    @Inject
    public UserInventoryRepository(PostgreDatasource datasource) {
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

    /**
     * Get the inventory of a user.
     *
     * @param userId User id.
     * @return Their inventory.
     */
    @NotNull
    public List<UserInventoryElement> getUserInventory(int userId) {
        List<UserInventoryElement> res = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ? WHERE user_id = ?");
            statement.setString(1, TABLE);
            statement.setInt(2, userId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // handle
                res.add(mapResultSetToType(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("failed to get user inventory {}", userId);
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Add an item to a user's inventory.
     *
     * @param userId User id to add it to.
     * @param elementId Element id to add.
     * @param unlockCode Unlock code used, may be null.
     */
    public void addElementToInventory(int userId, int elementId, @Nullable String unlockCode) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE + " (user_id, element_id, unlock_code) VALUES (?, ?, ?)");
            statement.setInt(1, userId);
            statement.setInt(2, elementId);
            statement.setString(3, unlockCode);

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("failed to add {} to user's {} inventory from unlock code {}", elementId, unlockCode, unlockCode);
            e.printStackTrace();
        }
    }

    /**
     * Remove an element from a user's inventory.
     *
     * @param userId User id to remove from.
     * @param elementId Element to remove from.
     * @return If there was anything removed.
     */
    public boolean removeElementFromInventory(int userId, int elementId) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE user_id = ? AND element_id = ?");
            statement.setInt(1, userId);
            statement.setInt(2, elementId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("failed to remove {} from user's {} inventory", elementId, userId);
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected UserInventoryElement mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int userId = resultSet.getInt("user_id");
        int elementId = resultSet.getInt("element_id");
        Time unlockTime = resultSet.getTime("unlock_time");
        String unlockCode = resultSet.getString("unlock_code");
        int relatedUserId = resultSet.getInt("related_user_id");
        float value = resultSet.getFloat("value");

        return new UserInventoryElement(id, userId, elementId, unlockTime, unlockCode, relatedUserId, value);
    }

    @Override
    protected void createTableIfNotExists() {
        try {
            try (Connection connection = datasource.getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "user_id INTEGER NOT NULL REFERENCES " + PrincipleUserRepository.TABLE + "(id) ON DELETE CASCADE," +
                        "element_id INTEGER NOT NULL REFERENCES " + ElementRepository.TABLE + "(id) ON DELETE CASCADE," +
                        "unlock_time TIMESTAMP NOT NULL DEFAULT NOW()," +
                        "unlock_code VARCHAR(100)," +
                        "related_user_id INT REFERENCES " + PrincipleUserRepository.TABLE + "(id)," +
                        "value FLOAT" +
                        ")").execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create element definition table", e);
        }
    }
}
