package ttl.larku.sql;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.init.ScriptUtils;


/**
 * A Technique to run sql scripts just once per class.
 * It relies on a JUnit 5 feature, where if a class
 * is annotated with @TestInstance(TestInstance.Lifecycle.PER_CLASS)
 * then you can use the @BeforeAll annotation on an instance method,
 * since there will be only one instance used for all tests, hence functionally
 * the same as a 'static' method.
 *
 * This class is meant to be a sub class for test cases.
 *
 * It runs SQL initialization scripts in the @BeforeAll method.
 *
 * It uses the ScriptFileProperties @ConfigurationProperties class to
 * get access to script locations.
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Transactional
public abstract class SqlScriptBase {

    /**
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
                System.err.println("Running schemaFile: " + schemaFile);
                ScriptUtils.executeSqlScript(conn, context.getResource(schemaFile));
            });

            props.getAllDataLocs().forEach(dataFile -> {
                System.err.println("Running dataFile: " + dataFile);
                ScriptUtils.executeSqlScript(conn, context.getResource(dataFile));
            });
        }
    }
}
