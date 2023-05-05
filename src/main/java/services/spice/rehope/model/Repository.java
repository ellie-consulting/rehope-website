package services.spice.rehope.model;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import services.spice.rehope.datasource.PostgreDatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Represents a repository that handles data.
 * @param <T>
 */
public abstract class Repository<T> {
    protected final PostgreDatasource datasource;

    public Repository(PostgreDatasource datasource) {
        this.datasource = datasource;

        createTableIfNotExists();
    }

    public abstract String getTable();

    public abstract Logger getLogger();

    public Optional<T> getById(int id) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ? WHERE id = ?");
            statement.setString(1, getTable());
            statement.setInt(2, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToType(resultSet));
            }
        } catch (SQLException e) {
            getLogger().error("failed to get data by id {}", id);
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public boolean existsByField(String field, String query) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT ? FROM ? WHERE ? = ?");
            statement.setString(1, field);
            statement.setString(2, getTable());
            statement.setString(3, field);
            statement.setString(4, query);

            return statement.executeQuery().next();
        } catch (SQLException e) {
            getLogger().error("failed to check if field {} exists by query {}", field, query);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete a user by their id.
     *
     * @param id Id to delete by.
     * @return If a user was deleted by the id.
     */
    public boolean deleteDataById(int id) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM ? WHERE id = ?");
            statement.setString(1, getTable());
            statement.setInt(2, id);
            int deleted = statement.executeUpdate();

            return deleted > 0;
        } catch (SQLException e) {
            getLogger().error("failed to delete user by id {}", id);
            e.printStackTrace();
        }

        return false;
    }


    protected abstract T mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException;

    protected abstract void createTableIfNotExists();

}
