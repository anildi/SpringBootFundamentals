//package ttl.larku.db.jdbcjpa.forspringboot;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import javax.sql.DataSource;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.jdbc.core.simple.JdbcClient;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.jdbc.datasource.init.ScriptUtils;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import ttl.larku.domain.Student;
//import ttl.larku.jconfig.LarkUTestDataConfig;
//
//import static java.lang.System.out;
//
//public class InitDB {
//
//   private LarkUTestDataConfig testDataConfig = new LarkUTestDataConfig();
//
//   public static void main(String[] args) {
//      InitDB idb = new InitDB();
//
//      idb.go();
//   }
//
//   public void go() {
//      String url = "jdbc:postgresql://localhost:5433/larku";
//      String user = "larku";
//      String pw = "larku";
//      DriverManagerDataSource dataSource = new DriverManagerDataSource(url, user, pw);
//      String schemaFile = "sql/postgres/3-postgres-larku_schema.sql";
//      String dataFile = "sql/postgres/4-postgress-larku-data.sql";
//
//      JdbcClient jdbcClient = JdbcClient.create(dataSource);
//
//      runSchemaFile(dataSource, schemaFile);
//      runSchemaFile(dataSource, dataFile);
//
////      addCourses(jdbcClient);
////      addScheduledClasses(jdbcClient);
////      addStudentsWithPhoneNumbers(jdbcClient);
////      addStudentRegistrations(jdbcClient);
//   }
//
//   public void runSchemaFile(DataSource dataSource, String schemaFile) {
//      try (Connection conn = dataSource.getConnection()) {
//
//         System.err.println("Running schemaFile: " + schemaFile);
//         ScriptUtils.executeSqlScript(conn, new ClassPathResource(schemaFile));
//
//      } catch (SQLException e) {
//         throw new RuntimeException(e);
//      }
//   }
//
//   public void addStudentsWithPhoneNumbers(JdbcClient jdbcClient) {
//      String insertStudentSql = "insert into student (name, dob, status) values(?, ?, ?)";
//
//      String insertPhoneNumbersSql = "insert into phonenumber (type, number, student_id) values(?, ?, ?)";
//      List<Integer> newStudentKeys = new ArrayList<>();
//
//      List<Student> students = List.of(
//            testDataConfig.student1(),
//            testDataConfig.student2(),
//            testDataConfig.student3(),
//            testDataConfig.student4()
//      );
//
//      List<Object[]> studentParams = students.stream()
//            .map(s -> new Object[]{s.getName() + "-JDBC", s.getDob(), s.getStatus().toString()})
//            .toList();
//
//      List<Object[]> phoneParams = students.stream()
//            .flatMap(s -> s.getPhoneNumbers().stream()
//                  .map(pn -> new Object[]{pn.getType().toString(), pn.getNumber(), s.getId()}))
//            .toList();
//
//      out.println("Running sql: " + insertStudentSql);
//      var keyHolder = new GeneratedKeyHolder();
//      for (Object[] params : studentParams) {
//         jdbcClient.sql(insertStudentSql)
//               .params(params)
//               .update(keyHolder);
//
////         out.println("Keys for new student: " + keyHolder.getKeys());
//         newStudentKeys.add((int) keyHolder.getKeys().get("id"));
//      }
//
//
//      out.println("Running sql: " + insertPhoneNumbersSql);
//      for (Object[] params : phoneParams) {
//         jdbcClient.sql(insertPhoneNumbersSql)
//               .params(params)
//               .update(keyHolder);
//      }
//
//      out.println("students added : " + newStudentKeys.size());
//      newStudentKeys.forEach(out::println);
//   }
//
//
//   public void addCourses(JdbcClient jdbcClient) {
//      String insertSql = "insert into course (code, title, credits)" +
//            " values(?, ?, ?)";
//      int rowsAffected = 0;
//      var newKeys = new ArrayList<Integer>();
//
//      var courses = List.of(
//            testDataConfig.course1(),
//            testDataConfig.course2(),
//            testDataConfig.course3()
//      );
//
//      List<List<Object>> input = courses.stream()
//            .map(course -> List.<Object>of(course.getCode(), course.getTitle(), course.getCredits()))
//            .toList();
//
//      out.println("Running sql: " + insertSql);
//      var keyHolder = new GeneratedKeyHolder();
//      for (List<Object> params : input) {
//         rowsAffected += jdbcClient.sql(insertSql)
//               .params(params)
//               .update(keyHolder);
//
//         newKeys.add((int) keyHolder.getKeys().get("id"));
//      }
//      out.println("rowsAffected: " + rowsAffected + ", newIds: " + newKeys);
//   }
//
//   public void addScheduledClasses(JdbcClient jdbcClient) {
//      String insertSql = "insert into scheduledclass (startdate, enddate, course_id)" +
//            " values(?, ?, ?)";
//      int rowsAffected = 0;
//      var newKeys = new ArrayList<Integer>();
//
//
//      List<List<Object>> input = List.of(
//            List.of(LocalDate.parse("2024-02-26"), LocalDate.parse("2024-06-07"), 1),
//            List.of(LocalDate.parse("2025-02-26"), LocalDate.parse("2025-06-07"), 1),
//            List.of(LocalDate.parse("2024-05-26"), LocalDate.parse("2024-09-07"), 2)
//      );
//
//      out.println("Running sql: " + insertSql);
//      var keyHolder = new GeneratedKeyHolder();
//      for (List<Object> params : input) {
//         rowsAffected += jdbcClient.sql(insertSql)
//               .params(params)
//               .update(keyHolder);
//
//         newKeys.add((int) keyHolder.getKeys().get("id"));
//      }
//
//      out.println("rowsAffected: " + rowsAffected + ", newKeys: " + newKeys);
//   }
//
//   public void addStudentRegistrations(JdbcClient jdbcClient) {
//      String sql = "insert into student_scheduledclass (student_id, classes_id) values (?, ?)";
//
//      List<List<Object>> args = List.of(
//            List.of(1, 2),
//            List.of(2, 3),
//            List.of(1, 3)
//      );
//      var rowsAffected = 0;
//      for(List<Object> lo : args) {
//         out.println("running sql: " + sql);
//         rowsAffected += jdbcClient.sql(sql)
//               .params(lo)
//               .update();
//      }
//
//      out.println("rowsAffected: " + rowsAffected);
//   }
//}