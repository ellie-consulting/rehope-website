package live.rehope.site.endpoint.inventory.user;

import io.javalin.http.BadRequestResponse;
import live.rehope.site.datasource.PostgreDatasource;
import live.rehope.site.datasource.exception.DatabaseError;
import live.rehope.site.endpoint.user.principle.PrincipleUserRepository;
import io.avaje.inject.RequiresBean;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.rehope.site.endpoint.inventory.element.ElementRepository;
import live.rehope.site.model.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores user's inventory.
 */
@Singleton
@RequiresBean({PrincipleUserRepository.class, ElementRepository.class})
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
     * </br>
     * Optional check for a user unlock context.
     *
     * @param userId User id.
     * @param userContext Unlock context required.
     * @return Their inventory.
     */
    @NotNull
    public List<UserInventoryElement> getUserInventory(int userId, @Nullable Integer userContext) {
        List<UserInventoryElement> res = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement;

            if (userContext != null) {
                statement = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE user_id = ? AND unlock_user_context = ?");
                statement.setInt(2, userContext);
            } else {
                statement = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE user_id = ?");
            }
            statement.setInt(1, userId);

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
    public void addElementToInventory(int userId, int elementId,
                                      @Nullable String unlockCode, @Nullable Integer unlockUserContext, @Nullable Float unlockValue) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE + " (user_id, element_id, unlock_code, unlock_user_context, unlock_value) " +
                            "VALUES (?, ?, ?, ?, ?)");
            statement.setInt(1, userId);
            statement.setInt(2, elementId);
            statement.setString(3, unlockCode);
            statement.setObject(4, unlockUserContext, Types.INTEGER);
            statement.setObject(5, unlockValue, Types.FLOAT);

            if (statement.executeUpdate() == 0) {
                throw new DatabaseError();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("not present")) {
                throw new NotFoundResponse("no inventory element by id " + elementId);
            }

            LOGGER.error("failed to add {} to user {}'s {} inventory from unlock code {}", elementId, userId, unlockCode, unlockCode);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    /**
     * Bulk add elements to inventories.
     *
     * @param elements Elements
     */
    public void addElementsToInventory(@NotNull List<UserInventoryElement> elements) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE + " (user_id, element_id, unlock_code, unlock_user_context, unlock_value)" +
                            " VALUES (?, ?, ?, ?, ?)");

            for (UserInventoryElement element : elements) {
                statement.setInt(1, element.userId());
                statement.setInt(2, element.elementId());
                statement.setString(3, element.unlockCode());
                statement.setObject(4, element.unlockContextUser(), Types.INTEGER);
                statement.setObject(5, element.unlockContextValue(), Types.FLOAT);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            LOGGER.error("failed to execute batch {}", elements);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    /**
     * Remove an element from a user's inventory.
     *
     * @param userId User id to remove from.
     * @param elementId Element to remove from.
     * @param contextUser Element unlock context.
     */
    public void removeElementFromInventory(int userId, int elementId, @Nullable Integer contextUser) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE user_id = ? AND element_id = ? AND unlock_user_context = ?");
            statement.setInt(1, userId);
            statement.setInt(2, elementId);
            statement.setObject(3, contextUser, Types.INTEGER);

            if (statement.executeUpdate() == 0) {
                throw new NotFoundResponse(elementId + " was not in inventory");
            }
        } catch (SQLException e) {
            LOGGER.error("failed to remove {} from user's {} inventory", elementId, userId);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    /**
     * Check if this user has used the code.
     *
     * @param userId User id.
     * @param unlockCode Unlock code to check.
     * @return If it has been used by them.
     */
    public boolean hasUsedCode(int userId, @NotNull String unlockCode) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM " + TABLE + " WHERE user_id = ? AND unlock_code = ? LIMIT 1");
            statement.setInt(1, userId);
            statement.setString(2, unlockCode);

            return statement.executeQuery().next();
        } catch (SQLException e) {
            LOGGER.error("failed to check if {} has used code {}", userId, unlockCode);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    @Override
    protected UserInventoryElement mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int userId = resultSet.getInt("user_id");
        int elementId = resultSet.getInt("element_id");
        Timestamp unlockTime = resultSet.getTimestamp("unlock_time");
        String unlockCode = resultSet.getString("unlock_code");
        int relatedUserId = resultSet.getInt("unlock_user_context");
        float value = resultSet.getFloat("unlock_value");

        return new UserInventoryElement(id, userId, elementId, unlockTime, unlockCode, relatedUserId, value);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INTEGER NOT NULL REFERENCES " + PrincipleUserRepository.TABLE + "(id) ON DELETE CASCADE," +
                    "element_id INTEGER NOT NULL REFERENCES " + ElementRepository.TABLE + "(id) ON DELETE CASCADE," +
                    "unlock_time TIMESTAMP NOT NULL DEFAULT NOW()," +
                    "unlock_code VARCHAR(100)," +
                    "unlock_user_context INT REFERENCES " + PrincipleUserRepository.TABLE + "(id)," +
                    "unlock_value FLOAT" +
                    ")").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user inventory table", e);
        }
    }
}
