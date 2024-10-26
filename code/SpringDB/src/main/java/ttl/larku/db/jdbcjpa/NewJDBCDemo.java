package ttl.larku.db.jdbcjpa;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewJDBCDemo {

    public static void registerStudentForClass(DataSource dataSource, int sid, int classId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("Insert into STUDENT_SCHEDULEDCLASS values (?, ?)")) {

            connection.setAutoCommit(false);

            statement.setInt(1, sid);
            statement.setInt(2, classId);
            int result = statement.executeUpdate();

            connection.commit();
            System.out.println("Result is: " + result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void getAllStudentsWithEveryThing(DataSource dataSource, int input_id) {

        String sql = """
                select student.id, student.name, student.phonenumber, student.dob, student.status, 
                   scheduledclass.id as class_id, scheduledclass.startdate, scheduledclass.enddate, 
                   course.id as course_id, course.code, course.title, course.credits 
                   from Student student
                   left join Student_ScheduledClass student_scheduledclass on student.id = student_scheduledclass.students_id 
                   left join ScheduledClass scheduledclass on student_scheduledclass.classes_id = scheduledclass.id 
                   left join Course course on scheduledclass.course_id = course.id 
                   where  student.id = ?; 
                """;

        String mysql_sql = """
                select student.id, student.name, student.phonenumber, student.dob, student.status, 
                   scheduledclass.id as class_id, scheduledclass.startdate, scheduledclass.enddate, 
                   course.id as course_id, course.code, course.title, course.credits 
                   from Student student
                   left join Student_ScheduledClass student_scheduledclass on student.id = student_scheduledclass.students_id 
                   left join ScheduledClass scheduledclass on student_scheduledclass.classes_id = scheduledclass.id 
                   left join Course course on scheduledclass.course_id = course.id 
                   where  student.id = ?; 
                """;
        try (Connection connection = dataSource.getConnection();
             //PreparedStatement statement = connection.prepareStatement(postgres_sql);
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            statement.setInt(1, input_id);

            ResultSet rs = statement.executeQuery();

            List<Student> students = new ArrayList<Student>();
            int currId = -1;
            Student student = null;
            while (rs.next()) {

                int id = rs.getInt("id");
                if(id != currId) { //New Student
                    currId = id;
                    String name = rs.getString("name");
                    String status = rs.getString("status");
                    String number = rs.getString("phoneNumber");
                    String dobStr = rs.getString("dob");
                    LocalDate dob = dobStr != null ? LocalDate.parse(dobStr) : null;
                    student = new Student();
                    student.setId(id);
                    student.setName(name);
                    student.setStatus(Student.Status.valueOf(status));
                    student.setPhoneNumber(number);
                    student.setDob(dob);

                    students.add(student);
                }
                String courseIdStr = rs.getString("course_id");
                if(courseIdStr != null) {
                    int courseId = Integer.valueOf(courseIdStr);
                    String courseCode = rs.getString("code");
                    String title = rs.getString("title");
                    float credits = rs.getFloat("credits");
                    Course course = new Course(courseCode, title);
                    course.setId(courseId);
                    course.setCredits(credits);

                    int scid = rs.getInt("class_id");
                    LocalDate startDate = LocalDate.parse(rs.getString("startdate"));
                    LocalDate endDate = LocalDate.parse(rs.getString("endDate"));

                    ScheduledClass sc = new ScheduledClass(course, startDate, endDate);
                    sc.setId(scid);
                    student.getClasses().add(sc);
                }
            }

            students.forEach(s -> {
                System.out.println(s);
                s.getClasses().forEach(System.out::println);
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void getAllStudents(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {

            ResultSet rs = statement.executeQuery("Select * from Student");

            List<Student> students = new ArrayList<Student>();
            while (rs.next()) {

                String name = rs.getString("name");
                String status = rs.getString("status");
                String number = rs.getString("phoneNumber");
                String dobStr = rs.getString("dob");
                LocalDate dob = dobStr != null ? LocalDate.parse(dobStr) : null;
                int id = rs.getInt("id");

                //"ORM"
                Student student = new Student();
                student.setId(id);
                student.setName(name);
                student.setStatus(Student.Status.valueOf(status));
                student.setPhoneNumber(number);
                student.setDob(dob);

                students.add(student);
            }

            students.forEach(System.out::println);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //addStudentToClass(1, 2);
        //Postgres
        String url = "jdbc:postgresql://localhost:5432/larku";
        String user = "postgres";
        String pw = "secret";

        //Mysql
//        String url = "jdbc:mysql://localhost:33060/larku";
//        String user = "root";
//        String pw = "secret";

        //Mariadb
//        String url = "jdbc:mariadb://localhost:3306/larku";
//        String user = "larku";
//        String pw = "larku";

        //H2
//        String url = "jdbc:h2:tcp://localhost:8083/./larku";
//        String user = "larku";
//        String pw = "larku";

        DataSource dataSource = new DriverManagerDataSource(url, user, pw);
//        getAllStudents(dataSource);
        getAllStudentsWithEveryThing(dataSource, 1);
    }
}
