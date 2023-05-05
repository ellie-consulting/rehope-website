package services.spice.rehope.inventory.redeem;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.model.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class CodeRepository extends Repository<Code> {
    private static final String CODES = "codes";
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeRepository.class);

    @Inject
    public CodeRepository(PostgreDatasource datasource) {
        super(datasource);
    }

    @Override
    public String getTable() {
        return CODES;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Insert a code.
     *
     * @param code
     */
    public void insertCode(@NotNull Code code) {


    }

    @Override
    protected Code mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    protected void createTableIfNotExists() {

    }
}
