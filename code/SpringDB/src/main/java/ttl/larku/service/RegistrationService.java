package ttl.larku.service;

import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class RegistrationService {

    private CourseService courseService;
    private StudentService studentService;
    private ClassService classService;

    public RegistrationService() {
    }


    public ScheduledClass addNewClassToSchedule(String courseCode, LocalDate startDate, LocalDate endDate) {
        ScheduledClass sClass = classService.createScheduledClass(courseCode, startDate, endDate);
        return sClass;
    }

    public void registerStudentForClass(int studentId, String courseCode, LocalDate startDate) {
        Student student = studentService.getStudent(studentId);
        List<ScheduledClass> classes = classService.getScheduledClassesByCourseCodeAndStartDate(courseCode, startDate);
        if (classes.size() > 0) {
            ScheduledClass sc = classes.get(0);
            sc.addStudent(student);
            student.addClass(sc);
        }
    }

    public void dropStudentFromClass(int studentId, String courseCode, LocalDate startDate) {
        Student student = studentService.getStudent(studentId);
        List<ScheduledClass> classes = classService.getScheduledClassesByCourseCodeAndStartDate(courseCode, startDate);
        if (classes.size() > 0) {
            ScheduledClass sc = classes.get(0);
            sc.removeStudent(student);
            student.dropClass(sc);
        }
    }

    /**
     * This just give you the students for the first found class
     * right now.
     *
     * @param courseCode
     * @param startDate
     * @return
     */
    public List<Student> getStudentsForClass(String courseCode, LocalDate startDate) {
        List<ScheduledClass> classes =
                classService.getScheduledClassesByCourseCodeAndStartDate(courseCode, startDate);
        List<Student> result = new ArrayList<>();
        if (classes.size() > 0) {
            ScheduledClass sc = classes.get(0);
            result = sc.getStudents();
//            List<Student> students = sc.getStudents();
//            if (students.size() > 0) {
//                result =  students;
//            }
        }
        return result;
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
