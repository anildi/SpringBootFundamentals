package ttl.larku.db.jdbcjpa.forspringboot;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import ttl.larku.jconfig.LarkUTestDataConfig;

public class JdbcClientTestTransaction {
   public static void main(String[] args) {
      new JdbcClientTestTransaction().go();
   }

   private LarkUTestDataConfig testDataConfig = new LarkUTestDataConfig();


   public void go() {
      String url = "jdbc:postgresql://localhost:5433/larku";
      String user = "larku";
      String pw = "larku";
      //DataSource dataSource = new DriverManagerDataSource(url, user, pw);
      SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
      dataSource.setAutoCommit(false);
      dataSource.setUrl(url);
      dataSource.setUsername(user);
      dataSource.setPassword(pw);

      String schemaFile = "sql/postgres/4-postgres-larku_schema.sql";

      JdbcClient jdbcClient = JdbcClient.create(dataSource);

      addStudent(jdbcClient, dataSource);

   }

   public void addStudent(JdbcClient jdbcClient, SingleConnectionDataSource dataSource) {
      String sql = "insert into student (name, dob) values ('Snark', '2020-10-10')";

      jdbcClient.sql(sql)
            .update();

      //With autocommit to false, data will not go into db without commit.
//      try {
//         dataSource.getConnection().commit();
//      } catch (SQLException e) {
//         throw new RuntimeException(e);
//      }
   }

}