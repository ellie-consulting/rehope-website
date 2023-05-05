package services.spice.rehope.inventory.element;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.inventory.element.model.ElementRarity;
import services.spice.rehope.inventory.element.model.ElementType;
import services.spice.rehope.inventory.element.model.InventoryElement;
import services.spice.rehope.model.Repository;

import java.sql.*;

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
     * @return If it was added (no dupes)
     */
    public boolean addItem(InventoryElement element) {
        if (existsByField("element_id", element.name())) {
            return false;
        }

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("INSERT INTO " + TABLE + " (element_id, type, rarity, " +
                            "name, description, icon_uri) VALUES (?, ?, ?, ?, ?, ?)");

            statement.setString(1, element.elementId());
            statement.setString(2, element.type().toString());
            statement.setString(3, element.rarity().toString());
            statement.setString(4, element.name());
            statement.setString(5, element.description());
            statement.setString(6, element.iconUri());
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
                    connection.prepareStatement("UPDATE " + TABLE +" SET type = ?, rarity = ?, name = ?, " +
                            "description = ?, icon_uri = ? WHERE element_id = ?");

            statement.setString(1, element.type().toString());
            statement.setString(2, element.rarity().toString());
            statement.setString(3, element.name());
            statement.setString(4, element.description());
            statement.setString(5, element.iconUri());
            statement.setString(6, element.elementId());
            statement.execute();
        } catch (SQLException e) {
            getLogger().error("failed to insert inventory element {}", element);
            e.printStackTrace();
        }
    }

    @Override
    protected InventoryElement mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String elementId = resultSet.getString("element_id");
        ElementType type = ElementType.valueOf(resultSet.getString("type"));
        ElementRarity rarity = ElementRarity.valueOf(resultSet.getString("rarity"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String iconUri = resultSet.getString("iconUri");

        return new InventoryElement(id, elementId, type, rarity, name, description, iconUri);
    }

    @Override
    protected void createTableIfNotExists() {
        try {
            try (Connection connection = datasource.getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "element_id VARCHAR(255) NOT NULL UNIQUE," +
                        "type VARCHAR(25) NOT NULL," +
                        "rarity VARCHAR(25) NOT NULL," +
                        "name VARCHAR(255) NOT NULL," +
                        "description VARCHAR(255) NOT NULL," +
                        "iconUri VARCHAR(255) NOT NULL" +
                        ")").execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create element definition table", e);
        }
    }
}
