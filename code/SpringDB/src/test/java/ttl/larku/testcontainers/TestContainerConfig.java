package ttl.larku.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConfig {

   @Bean
   @ServiceConnection
   PostgreSQLContainer<?> postgresContainer() {
      var postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.0"))
            .withDatabaseName("larku")
            .withUsername("larku")
            .withPassword("larku");

      postgresContainer.start();
      var containerDelegate = new JdbcDatabaseDelegate(postgresContainer, "");
//
      ScriptUtils.runInitScript(containerDelegate, "sql/postgres/3-postgres-larku-schema.sql");
      ScriptUtils.runInitScript(containerDelegate, "sql/postgres/5-postgress-larku-data.sql");

      return postgresContainer;
   }



//   @Bean
//   @ServiceConnection
//   PostgreSQLContainer<?> postgresContainer() {
//      Consumer<CreateContainerCmd> cmdModifier = e ->
//            e.withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)));
//      return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.0"))
//            .withDatabaseName("larku")
//            .withUsername("larku")
//            .withPassword("larku");
////				.withExposedPorts(5432)
////				.withCreateContainerCmdModifier(cmdModifier);
//   }
}
