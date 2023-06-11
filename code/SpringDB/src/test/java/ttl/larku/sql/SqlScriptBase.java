package ttl.larku.sql;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.ScriptFileProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@Transactional
public abstract class SqlScriptBase {

    public static String schemaFile;
    public static String dataFile;

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
    @BeforeAll
    public static void runSqlScriptsOnce(@Autowired DataSource dataSource,
                            @Autowired ScriptFileProperties props) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            schemaFile = props.getSchemaFile();
            dataFile = props.getDataFile();
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(schemaFile));
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(dataFile));
        }
    }
}
