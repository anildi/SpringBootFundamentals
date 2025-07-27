package ttl.larku.sql;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public abstract class SqlScriptBase {

    private final static Logger logger = LoggerFactory.getLogger(SqlScriptBase.class);

    /**
     * A Technique to run sql scripts just once per class.
     * This was to solve a tricky situation.  This class
     * uses the webEnvironment.MOCK context, which would
     * have already been created and cached by a previous
     * test.  Since no context is created for this test,
     * no DDL scripts are run.  So this test gets whatever
     * was in put into the database by the previous test.
     * If that was using different data, e.g. the "VersionedXXX"
     * sql scripts, then tests in this class can fail in
     * strange ways that depend on which other tests are run.
     * This trick makes sure that this test starts with the
     * data it is expecting.  The @Transactional then takes
     * care of rolling back after each test.
     *
     * We are also using the strange fact that Spring auto wiring
     * works on this static method, to inject a ScriptFileProperties
     * bean, which gets properties set with @ConfigurationProperties.
     * This allows us to use properties to decide which scripts to
     * run.  The properties are in larkUContext.properties.
     * @param dataSource
     * @throws SQLException
     */
    @Autowired DataSource dataSource;
    @Autowired ScriptFileProperties props;
    @Autowired ApplicationContext context;

    @BeforeAll
    public void runSqlScriptsOnce() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            props.getAllSchemaLocs().forEach(schemaFile -> {
                logger.info("Running schemaFile: {}", schemaFile);
                ScriptUtils.executeSqlScript(conn, context.getResource(schemaFile));
            });

            props.getAllDataLocs().forEach(dataFile -> {
                logger.info("Running dataFile: {}", dataFile);
                ScriptUtils.executeSqlScript(conn, context.getResource(dataFile));
            });
        }
    }
}
