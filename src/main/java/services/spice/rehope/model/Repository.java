package services.spice.rehope.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import services.spice.rehope.datasource.PostgreDatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a repository that handles data.
 * @param <T> Value type stored in this repository.
 */
public abstract class Repository<T> {
    protected final PostgreDatasource datasource;

    public Repository(@NotNull PostgreDatasource datasource) {
        this.datasource = datasource;

        System.out.println("hi " + getClass().getSimpleName());
        createTableIfNotExists();
    }

    public abstract String getTable();

    public abstract Logger getLogger();

    /**
     * @return Get all values in the repository.
     */
    @NotNull
    public List<T> getAll() {
        List<T> elements = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM " + getTable());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                elements.add(mapResultSetToType(resultSet));
            }

        } catch (SQLException e) {
            getLogger().error("failed to get repository elements");
            e.printStackTrace();
        }

        return elements;
    }

    /**
     * Get all elements in a repository with a filter.
     *
     * @param filterField Filter field.
     * @param filter Filter value.
     * @return Matching values.
     */
    @NotNull
    protected List<T> getAllWithFilter(@NotNull String filterField, Object filter) {
        List<T> res = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getTable() + " WHERE ? = ?");
            statement.setString(1, filterField);
            statement.setObject(2, filter);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(mapResultSetToType(resultSet));
            }
        } catch (SQLException e) {
            getLogger() .error("failed to get elements where {}={}", filterField, filter);
        }

        return res;
    }

    /**
     * Get a value with a query.
     *
     * @param queryField Field to check.
     * @param query Value to compare.
     * @return The value, or empty if doesn't exist.
     */
    @NotNull
    protected Optional<T> getByField(@NotNull String queryField, Object query) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getTable() + " WHERE " + queryField + " = ?");
            statement.setObject(1, query);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToType(resultSet));
            }
        } catch (SQLException e) {
            getLogger().error("failed to get element where {}={}", queryField, query);
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Get a value by its databased assigned id.
     *
     * @param id Id.
     * @return The value, or empty if doesn't exist.
     */
    @NotNull
    public Optional<T> getById(int id) {
        return getByField("id", id);
    }

    /**
     * Check if a field with a value exists.
     *
     * @param field Field to check.
     * @param query Value to compare.
     * @return If this value is already set in the table.
     */
    protected boolean existsByField(@NotNull String field, @NotNull String query) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT " + field + " FROM " + getTable() + " WHERE " + field + " = ?");
            statement.setString(1, query);

            return statement.executeQuery().next();
        } catch (SQLException e) {
            getLogger().error("failed to check if field {} exists by query {}", field, query);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update a field.
     *
     * @param queryField Lookup field.
     * @param query Comparison.
     * @param updateField Field to update.
     * @param newValue New value to use.
     * @return If was updated successfully.
     */
    protected boolean updateField(@NotNull String queryField, @NotNull Object query, @NotNull String updateField, @Nullable Object newValue) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + getTable() + " SET " + updateField + " = ? WHERE " + queryField + " = ?");
            statement.setObject(1, newValue);
            statement.setObject(2, query);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            getLogger().error("failed to update field for {}={}: {} -> {}", queryField, query, updateField, newValue);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update a field by its database assigned ID.
     *
     * @param id Id to update.
     * @param updateField The field to update.
     * @param newValue The new value to replace with.
     * @return If the update was successfully.
     * @see Repository#updateField(String, Object, String, Object)
     */
    protected boolean updateFieldById(int id, @NotNull String updateField, @Nullable Object newValue) {
        return updateField("id", id, updateField, newValue);
    }

    /**
     * Delete an element by a query.
     *
     * @param queryField Lookup field.
     * @param query Comparison.
     * @return If deleted successfully.
     */
    protected boolean deleteData(@NotNull String queryField, Object query) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + getTable() + " WHERE " + queryField + " = ?");
            statement.setObject(1, query);
            int deleted = statement.executeUpdate();

            return deleted > 0;
        } catch (SQLException e) {
            getLogger().error("failed to delete table element where {}={}", queryField, query);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete an element by its id.
     *
     * @param id Id to delete by.
     * @return If deleted successfully.
     */
    protected boolean deleteDataById(int id) {
        return deleteData("id", id);
    }


    protected abstract T mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException;

    protected abstract void createTableIfNotExists();

}
