package ttl.larku.dao;

import java.util.ResourceBundle;

import ttl.larku.dao.inmemory.InMemoryCourseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.dao.jpa.JpaCourseDAO;
import ttl.larku.dao.jpa.JpaStudentDAO;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

public class MyFactory {

    private static BaseDAO<Student> studentDAO;

    public static BaseDAO<Student> studentDAO() {
        //NOT Thread safe.  But this will normally be called first
        //during application start up, which should generally happen
        //in a single Thread.  But still, this should probably be either
        //a java Singleton, or we should return a new instance each time
        if (studentDAO == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("larkUContext");
            String profile = bundle.getString("larku.profile.active");
            switch (profile) {
                case "dev":
                    return studentDAO = new InMemoryStudentDAO();
                case "prod":
                    return studentDAO = new JpaStudentDAO();
                default:
                    throw new RuntimeException("No profile set");
            }
        } else {
            return studentDAO;
        }
    }

    //The way we have this set up, courseDAO's will
    //have "Prototype" scope, i.e. new instance returned
    //with each call.
    public static BaseDAO<Course> courseDAO() {
        ResourceBundle bundle = ResourceBundle.getBundle("larkUContext");
        String profile = bundle.getString("larku.profile.active");
        switch (profile) {
            case "dev":
                return new InMemoryCourseDAO();
            case "prod":
                return new JpaCourseDAO();
            default:
                throw new RuntimeException("No profile set");
        }
    }

    public static StudentService studentService() {
        StudentService ss = new StudentService();

        ss.setStudentDAO(studentDAO());

        return ss;
    }

    public static CourseService courseService() {
        CourseService ss = new CourseService();

        ss.setCourseDAO(courseDAO());

        return ss;
    }
}
