package ttl.larku.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

//TODO - Need to make this into a bean
@Service
public class RegistrationService {

    private CourseService courseService;
    private StudentService studentService;
    private ClassService classService;

    @Autowired
    public RegistrationService(StudentService studentService, CourseService courseService, ClassService classService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.classService = classService;
//        courseService = new CourseService();
//        studentService = new StudentService();
//        classService = new ClassService();

        int count = studentService.getAllStudents().size();
    }


//    @PostConstruct
//    public void init() {
//        int count = studentService.getAllStudents().size();
//        System.out.println("Count is : " + count);
//
//    }


    public ScheduledClass addNewClassToSchedule(String courseCode, String startDate, String endDate) {
        ScheduledClass sClass = classService.createScheduledClass(courseCode, startDate, endDate);
        return sClass;
    }

    public void registerStudentForClass(int studentId, String courseCode, String startDate) {
        Student student = studentService.getStudent(studentId);
        List<ScheduledClass> classes = classService.getScheduledClassesByCourseCode(courseCode);
        for (ScheduledClass sc : classes) {
            if (sc.getStartDate().equals(startDate)) {
                sc.addStudent(student);
                student.addClass(sc);
                break;
            }
        }
    }

    public void dropStudentFromClass(int studentId, String courseCode, String startDate) {
        Student student = studentService.getStudent(studentId);
        List<ScheduledClass> classes = classService.getScheduledClassesByCourseCode(courseCode);
        for (ScheduledClass sc : classes) {
            if (sc.getStartDate().equals(startDate)) {
                sc.removeStudent(student);
                student.dropClass(sc);
                break;
            }
        }
    }

    public List<Student> getStudentsForClass(String courseCode, String startDate) {
        List<ScheduledClass> classes = classService.getScheduledClassesByCourseCode(courseCode);
        for (ScheduledClass sc : classes) {
            if (sc.getStartDate().equals(startDate)) {
                return sc.getStudents();
            }
        }
        return new ArrayList<Student>();
    }

    public List<ScheduledClass> getScheduledClasses() {
        return classService.getAllScheduledClasses();
    }


    public CourseService getCourseService() {
        return courseService;
    }


    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }


    public StudentService getStudentService() {
        return studentService;
    }


    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }


    public ClassService getClassService() {
        return classService;
    }


    public void setClassService(ClassService classService) {
        this.classService = classService;
    }
}
