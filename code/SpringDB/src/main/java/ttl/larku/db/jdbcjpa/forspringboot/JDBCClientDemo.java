//package ttl.larku.db.jdbcjpa.forspringboot;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import org.springframework.jdbc.core.ResultSetExtractor;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.JdbcClient;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import ttl.larku.domain.Course;
//import ttl.larku.domain.PhoneNumber;
//import ttl.larku.domain.ScheduledClass;
//import ttl.larku.domain.Student;
//import ttl.larku.jconfig.LarkUTestDataConfig;
//
//import static java.lang.System.out;
//
//public class JDBCClientDemo {
//
//   LarkUTestDataConfig testDataConfig = new LarkUTestDataConfig();
//
//   public static void main(String[] args) {
//      JDBCClientDemo jtd = new JDBCClientDemo();
//      String url = "jdbc:postgresql://localhost:5433/larku";
//      String user = "larku";
//      String pw = "larku";
//      DriverManagerDataSource dataSource = new DriverManagerDataSource(url, user, pw);
//
//      JdbcClient jdbcClient = JdbcClient.create(dataSource);
//
////      jtd.getSimpleWithOneColumn(jdbcClient);
////      jtd.getWholeObjectButOnlyStudent(jdbcClient);
////      jtd.getWholeObjectWithRelationShipWrongWay(jdbcClient);
////      jtd.getWholeObjectWithRelationShipWithResultSetExtractor(jdbcClient);
//
////      jtd.addStudentsWithPhoneNumbers(jdbcClient);
//
////      jtd.addCourses(jdbcClient);
////      jtd.addScheduledClasses(jdbcClient);
//
//      jtd.dumpScheduledClasses(jdbcClient);
//   }
//
//   public void getSimpleWithOneColumn(JdbcClient jdbcClient) {
//      String sql = "select name from Student where id = ?";
//
//      String name = jdbcClient.sql(sql)
//            .param(1)
//            .query(String.class).single();
//
//      out.println("name: " + name);
//   }
//
//   public void getWholeObjectButOnlyStudent(JdbcClient jdbcClient) {
//      String sql = "select * from Student where id = ?";
//
//      RowMapper<Student> rowMapper = (resultSet, rowNum) -> {
//         int id = resultSet.getInt("id");
//         String name = resultSet.getString("name");
//         LocalDate dob = resultSet.getObject("dob", LocalDate.class);
//
//         Student.Status status = Student.Status.valueOf(resultSet.getString("status"));
//
//         Student student = new Student(name, "", dob, status);
//         return student;
//      };
//
//      Student student = jdbcClient.sql(sql)
//            .param(1)
//            .query(rowMapper)
//            .single();
//
//      Student student2 = jdbcClient.sql(sql)
//            .param(1)
//            .query(Student.class)
//            .single();
//
//      out.println("student: " + student);
//      out.println("student2: " + student2);
//   }
//
//   public void getWholeObjectWithRelationShipWrongWay(JdbcClient jdbcClient) {
//      String sql = "select * from student s left join phonenumber p on s.id = p.student_id order by s.id";
//
//      RowMapper<Student> rowMapper = (resultSet, rowNum) -> {
//         int id = resultSet.getInt("id");
//         String name = resultSet.getString("name");
//         LocalDate dob = resultSet.getObject("dob", LocalDate.class);
//
//
//         Student.Status status = Student.Status.valueOf(resultSet.getString("status"));
//
//         Student student = new Student(name, "", dob, status);
//         String type = resultSet.getString("type");
//         if (type != null) {
//            String number = resultSet.getString("number");
//
//
//            PhoneNumber pn = new PhoneNumber(PhoneNumber.Type.valueOf(type), number);
//            student.addPhoneNumber(pn);
//         }
//         return student;
//      };
//
//      List<Student> students = jdbcClient.sql(sql)
//            .query(rowMapper)
//            .list();
//
//      students.forEach(out::println);
//   }
//
//   public void getWholeObjectWithRelationShipWithResultSetExtractor(JdbcClient jdbcClient) {
//      String sql = "select * from student s left join phonenumber p on s.id = p.student_id order by s.id";
//
//      List<Student> result = new ArrayList<>();
//      ResultSetExtractor<List<Student>> rowMapper = (resultSet) -> {
//         int lastStudentId = -1;
//         Student currStudent = null;
//
//         while (resultSet.next()) {
//            int id = resultSet.getInt("id");
//            if (id != lastStudentId) {
//               lastStudentId = id;
//
//               String name = resultSet.getString("name");
//               LocalDate dob = resultSet.getObject("dob", LocalDate.class);
//
//
//               Student.Status status = Student.Status.valueOf(resultSet.getString("status"));
//
//               currStudent = new Student(name, "", dob, status);
//               currStudent.setId(id);
//               result.add(currStudent);
//            }
//            String type = resultSet.getString("type");
//            if (type != null) {
//               String number = resultSet.getString("number");
//
//
//               PhoneNumber pn = new PhoneNumber(PhoneNumber.Type.valueOf(type), number);
//               currStudent.addPhoneNumber(pn);
//            }
//         }
//         return result;
//      };
//
//      List<Student> students = jdbcClient.sql(sql)
//            .query(rowMapper);
//
//
//      students.forEach(out::println);
//   }
//
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
//   public void dumpScheduledClasses(JdbcClient jdbcClient) {
//      String classSql = "select * from scheduledclass";
//      String courseSql = "select * from course where id = ?";
//
//      RowMapper<ScheduledClass> classMapper = (resultSet, rowNum) -> {
//         int id = resultSet.getInt("id");
//         LocalDate startDate = resultSet.getObject("startdate", LocalDate.class);
//         LocalDate endDate = resultSet.getObject("enddate", LocalDate.class);
//         int course_id = resultSet.getInt("course_id");
//
//         //Now go get the course
//         out.println("running sql: " + courseSql);
//         Course course = jdbcClient.sql(courseSql)
//               .param(course_id)
//               .query(Course.class)
//               .single();
//
//         ScheduledClass sc = new ScheduledClass(course, startDate, endDate);
//         return sc;
//      };
//
//      out.println("running sql: " + classSql);
//      List<ScheduledClass> classesWithCourse = jdbcClient.sql(classSql)
//            .query(classMapper)
//            .list();
//
//      classesWithCourse.forEach(out::println);
//
//   }
//}
