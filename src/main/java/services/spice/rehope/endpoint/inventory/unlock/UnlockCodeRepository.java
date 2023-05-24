package services.spice.rehope.endpoint.inventory.unlock;

import io.javalin.http.BadRequestResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.datasource.exception.DatabaseError;
import services.spice.rehope.endpoint.inventory.element.ElementRepository;
import services.spice.rehope.model.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UnlockCodeRepository extends Repository<UnlockCode> {
    private static final String TABLE = "unlock_codes";
    private static final Logger LOGGER = LoggerFactory.getLogger(UnlockCodeRepository.class);

    @Inject
    public UnlockCodeRepository(PostgreDatasource datasource) {
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
     * Insert a code.
     *
     * @param code Code to insert.
     */
    public void insertCode(@NotNull UnlockCode code) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE + " (code, redeem_limit, uses, active, unlock_element_id) " +
                            "VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, code.code());
            statement.setInt(2, code.redeemLimit());
            statement.setInt(3, code.uses());
            statement.setBoolean(4, code.active());
            statement.setInt(5, code.unlockElementId());

            if (statement.executeUpdate() == 0) {
                throw new BadRequestResponse();
            }
        } catch (SQLException e) {
            getLogger().error("failed to insert code code {}", code);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    /**
     * Code to delete.
     *
     * @param code Code to delete.
     */
    public void deleteCode(@NotNull String code) {
        deleteData("code", code);
    }

    /**
     * Update a code to be active.
     *
     * @param code Code to update.
     * @param state The new state to set.
     */
    public void setCodeActive(@NotNull String code, boolean state) {
        updateField("code", code, "active", state);
    }

    /**
     * Attempt to redeem a code.
     * </br>
     * First it will ensure it can actually be redeemed.
     * Then it will update the uses in the database.
     * And finally, return the constructed code.
     * </br>
     * It will return null if redemption was not successful.
     *
     * @param code Code to redeem.
     * @return A code successfully redeemed.
     */
    @NotNull
    public UnlockCode tryRedeem(@NotNull String code) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM " + TABLE + " WHERE code = ? AND active = ?");
            selectStatement.setString(1, code);
            selectStatement.setBoolean(2, true);

            ResultSet resultSet = selectStatement.executeQuery();
            if (!resultSet.next()) {
                // Code not found
                throw new BadRequestResponse("code invalid");
            }

            int redeemLimit = resultSet.getInt("redeem_limit");
            int uses = resultSet.getInt("uses");

            if (redeemLimit != -1 && uses >= redeemLimit) {
                // Code usage limit reached
                throw new BadRequestResponse("code used");
            }

            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE " + TABLE + " SET uses = uses + 1 WHERE code = ?");
            updateStatement.setString(1, code);
            updateStatement.executeUpdate();

            return mapResultSetToType(resultSet);
        } catch (SQLException e) {
            getLogger().error("failed to redeem code {}", code);
            e.printStackTrace();
            throw new DatabaseError();
        }
    }

    @Override
    protected UnlockCode mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String code = resultSet.getString("code");
        int redeemLimit = resultSet.getInt("redeem_limit");
        int uses = resultSet.getInt("uses");
        boolean active = resultSet.getBoolean("active");
        int unlockElementId = resultSet.getInt("unlock_element_id");

        return new UnlockCode(id, code, redeemLimit, uses, active, unlockElementId);
    }

    @Override
    protected void createTableIfNotExists() {
        try {
            try (Connection connection = datasource.getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "code VARCHAR(100) NOT NULL UNIQUE," +
                        "redeem_limit INTEGER NOT NULL," +
                        "uses INTEGER NOT NULL," +
                        "active BOOLEAN DEFAULT false," +
                        "unlock_element_id INT REFERENCES " + ElementRepository.TABLE + "(id)" +
                        ")").execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create element definition table", e);
        }
    }
}
