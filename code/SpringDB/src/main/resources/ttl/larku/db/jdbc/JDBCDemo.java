package ttl.larku.db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ttl.larku.domain.Student;

public class JDBCDemo {

    public static void addStudentToClass(int sid, int classId) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            connection = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/LarkUDB", "larku", "larku");
            connection.setAutoCommit(false);


            statement = connection.prepareStatement("Insert into STUDENT_SCHEDULEDCLASS values (?, ?)");

            statement.setInt(1, sid);
            statement.setInt(2, classId);
            int result = statement.executeUpdate();

            connection.commit();
            System.out.println("Result is: " + result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    public static void getAllStudents() {
        Statement statement = null;
        Connection connection = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            connection = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/LarkUDB", "larku", "larku");

            statement = connection
                    .createStatement();

            ResultSet rs = statement.executeQuery("Select * from Student");

            List<Student> students = new ArrayList<Student>();
            while (rs.next()) {

                String name = rs.getString("name");
                String status = rs.getString("status");
                String number = rs.getString("phoneNumber");
                int id = rs.getInt("id");

                //"ORM"
                Student student = new Student();
                student.setId(id);
                student.setName(name);
                student.setStatus(Student.Status.valueOf(status));
                student.setPhoneNumber("282 484 8484");

                students.add(student);
            }

            students.forEach(System.out::println);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        //addStudentToClass(1, 2);
        getAllStudents();
    }
}
