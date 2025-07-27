package ttl.larku.testcontainers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.lang.System.out;

/**
 * @author whynot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.sql.init.mode=never"})
@Transactional
@Tag("container")
@Disabled
public class PostgresContainerTest {

    //    @LocalServerPort
    private int port;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:11.1")
                .withDatabaseName("larku")
                .withUsername("larku")
                .withPassword("larku");
//                .withInitScript("sql/postgres/3-postgress-larku-schema.sql")
//                .withInitScript("sql/postgres/4-postgress-larku-data.sql");
//            .withExposedPorts(9999)
//            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
//                new HostConfig().withPortBindings(
//                        new PortBinding(Ports.Binding.bindPort(9999), new ExposedPort(9999)))
//            ));

        postgresContainer.start();
        //Initialize
        var containerDelegate = new JdbcDatabaseDelegate(postgresContainer, "");

        ScriptUtils.runInitScript(containerDelegate, "sql/postgres/3-postgres-larku-schema.sql");
        //ScriptUtils.runInitScript(containerDelegate, "sql/postgres/4-postgress-larku-data.sql");
        ScriptUtils.runInitScript(containerDelegate, "sql/postgres/5-postgress-larku-data.sql");

    }

    @AfterAll
    public static void afterAll() {
        postgresContainer.stop();
    }

    @Autowired
    private StudentRepo studentRepo;

    @Test
    public void testPostgresContainer() {
//        Integer firstMappedPort = postgresContainer.getFirstMappedPort();
        out.println("Test ran");

        List<Student> students = studentRepo.findAll();

        out.println("Students: " + students.size());
        students.forEach(out::println);
        assertEquals(4, students.size());
    }
}
