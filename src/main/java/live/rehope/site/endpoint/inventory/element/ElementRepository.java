package live.rehope.site.endpoint.inventory.element;

import live.rehope.site.datasource.PostgreDatasource;
import live.rehope.site.datasource.exception.DatabaseError;
import live.rehope.site.endpoint.inventory.element.model.InventoryElement;
import live.rehope.site.endpoint.inventory.element.model.UnlockObjective;
import live.rehope.site.model.Repository;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.rehope.site.endpoint.inventory.element.model.ElementType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores inventory element definitions.
 * </br>
 * Each definition has a type and its own unique id.
 */
@Singleton
public class ElementRepository extends Repository<InventoryElement> {
    public static final String TABLE = "element_definitions";
    private static final Logger LOGGER = LoggerFactory.getLogger(ElementRepository.class);

    @Inject
    public ElementRepository(PostgreDatasource datasource) {
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
     * Register an item definition.
     *
     * @param element Element to register.
     * @return Created element id
     */
    public int addItem(@NotNull InventoryElement element) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("INSERT INTO " + TABLE + " (type, unlock_objective, " +
                            "unlock_value, name, description, icon_uri) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id");
            statement.setString(1, element.type().toString());
            statement.setString(2, element.unlockObjective() != null ? element.unlockObjective().toString() : null);
            statement.setFloat(3, element.unlockValue());
            statement.setString(4, element.name());
            statement.setString(5, element.description());
            statement.setString(6, element.iconUri());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }

            throw new DatabaseError();
        } catch (SQLException e) {
            getLogger().error("failed to insert inventory element: {}", element);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    /**
     * Update an element.
     *
     * @param element Element to update.
     */
    public void updateElement(int id, @NotNull InventoryElement element) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("UPDATE " + TABLE +" SET type = ?, unlock_objective = ?, unlock_value = ?, " +
                            "name = ?, description = ?, icon_uri = ? WHERE id = ?");

            statement.setString(1, element.type().toString());
            statement.setString(2, element.unlockObjective() != null ? element.unlockObjective().toString() : null);
            statement.setFloat(3, element.unlockValue());
            statement.setString(4, element.name());
            statement.setString(5, element.description());
            statement.setString(6, element.iconUri());
            statement.setInt(7, id);

            if (statement.executeUpdate() == 0) {
                throw new NotFoundResponse(element.name() + " does not exist");
            }
        } catch (SQLException e) {
            getLogger().error("failed to update inventory element {}", element);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    /**
     * Get all unlockable elements from this objective and value.
     *
     * @param unlockObjective In which object to achieve.
     * @param unlockValue The value obtained.
     * @return Elements that can be unlocked with this criteria.
     */
    @NotNull
    public List<InventoryElement> getUnlockableElements(@NotNull UnlockObjective unlockObjective, float unlockValue) {
        List<InventoryElement> inventoryElements = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE unlock_objective = ? " +
                            "AND unlock_value >= ?");

            statement.setString(1, unlockObjective.toString());
            statement.setFloat(2, unlockValue);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                inventoryElements.add(mapResultSetToType(resultSet));
            }
        } catch (SQLException e) {
            getLogger().error("failed to get inventory elements for {} {}", unlockObjective, unlockValue);
            e.printStackTrace();
            throw new DatabaseError();
        }

        return inventoryElements;
    }

    /**
     * Get all elements that can be unlocked by this objective.
     *
     * @param objective Objective.
     * @return Unlockable elements.
     */
    @NotNull
    public List<InventoryElement> getUnlockableElementsByObjective(UnlockObjective objective) {
        return getAllWithFilter("unlock_objective", objective);
    }

    /**
     * Delete an element by its id.
     *
     * @param id Id to delete by.
     */
    public void deleteElementById(int id) {
        deleteDataById(id);
    }

    @Override
    protected InventoryElement mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        ElementType type = ElementType.valueOf(resultSet.getString("type"));
        UnlockObjective unlockObjective = resultSet.getString("unlock_objective") != null
                ? UnlockObjective.valueOf(resultSet.getString("unlock_objective")) : null;
        float unlockValue = resultSet.getFloat("unlock_value");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String iconUri = resultSet.getString("icon_uri");

        return new InventoryElement(id, type, unlockObjective, unlockValue, name, description, iconUri);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "type VARCHAR(25) NOT NULL," +
                    "unlock_objective VARCHAR(25) NULL," +
                    "unlock_value FLOAT," +
                    "name VARCHAR(255) NOT NULL," +
                    "description VARCHAR(255) NOT NULL," +
                    "icon_uri VARCHAR(255) NOT NULL" +
                    ")").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create element definition table", e);
        }
    }
}
