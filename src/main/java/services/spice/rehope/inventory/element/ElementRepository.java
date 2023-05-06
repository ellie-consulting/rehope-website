package services.spice.rehope.inventory.element;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.inventory.element.model.ElementType;
import services.spice.rehope.inventory.element.model.InventoryElement;
import services.spice.rehope.inventory.element.model.UnlockObjective;
import services.spice.rehope.model.Repository;

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

    @NotNull
    public List<InventoryElement> getAll() {
        List<InventoryElement> elements = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM " + TABLE);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                elements.add(mapResultSetToType(resultSet));
            }

        } catch (SQLException e) {
            getLogger().error("failed to get inventory elements");
            e.printStackTrace();
        }

        return elements;
    }

    /**
     * Register an item definition.
     *
     * @param element Element to register.
     * @return If it was added (no dupes)
     */
    public boolean addItem(@NotNull InventoryElement element) {
        if (existsByField("element_id", element.name())) {
            return false;
        }

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("INSERT INTO " + TABLE + " (element_id, type, unlock_objective, " +
                            "unlock_value, name, description, icon_uri) VALUES (?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, element.elementId());
            statement.setString(2, element.type().toString());
            statement.setString(3, element.unlockObjective() != null ? element.unlockObjective().toString() : null);
            statement.setFloat(4, element.unlockValue());
            statement.setString(5, element.name());
            statement.setString(6, element.description());
            statement.setString(7, element.iconUri());
            statement.execute();

            return true;
        } catch (SQLException e) {
            getLogger().error("failed to insert inventory element {}", element);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update an element.
     *
     * @param element Element to update.
     */
    public void updateElement(@NotNull InventoryElement element) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("UPDATE " + TABLE +" SET type = ?, unlock_objective = ?, unlock_value = ?, " +
                            "name = ?, description = ?, icon_uri = ? WHERE element_id = ?");

            statement.setString(1, element.type().toString());
            statement.setString(2, element.unlockObjective() != null ? element.unlockObjective().toString() : null);
            statement.setFloat(3, element.unlockValue());
            statement.setString(4, element.name());
            statement.setString(5, element.description());
            statement.setString(6, element.iconUri());
            statement.setString(7, element.elementId());
            statement.execute();
        } catch (SQLException e) {
            getLogger().error("failed to insert inventory element {}", element);
            e.printStackTrace();
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
        }

        return inventoryElements;
    }

    @Override
    protected InventoryElement mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String elementId = resultSet.getString("element_id");
        ElementType type = ElementType.valueOf(resultSet.getString("type"));
        UnlockObjective unlockObjective = resultSet.getString("unlock_objective") != null
                ? UnlockObjective.valueOf(resultSet.getString("unlock_objective")) : null;
        float unlockValue = resultSet.getFloat("unlock_value");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String iconUri = resultSet.getString("icon_uri");

        return new InventoryElement(id, elementId, type, unlockObjective, unlockValue, name, description, iconUri);
    }

    @Override
    protected void createTableIfNotExists() {
        try {
            try (Connection connection = datasource.getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "element_id VARCHAR(255) NOT NULL UNIQUE," +
                        "type VARCHAR(25) NOT NULL," +
                        "unlock_objective VARCHAR(25)," +
                        "unlock_value FLOAT," +
                        "name VARCHAR(255) NOT NULL," +
                        "description VARCHAR(255) NOT NULL," +
                        "icon_uri VARCHAR(255) NOT NULL" +
                        ")").execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create element definition table", e);
        }
    }
}
