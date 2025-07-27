package ttl.larku.service;

import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import java.util.ArrayList;
import java.util.List;

//TODO - Need to make this into a bean
public class RegistrationService {

    //TODO - something required here
    private CourseService courseService;
    private StudentService studentService;
    private ClassService classService;

    public RegistrationService() {
        courseService = new CourseService();
        studentService = new StudentService();
        classService = new ClassService();
    }


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
