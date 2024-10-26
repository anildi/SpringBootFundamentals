package ttl.larku.service;

import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public boolean registerStudentForClass(int studentId, String courseCode, LocalDate startDate) {
        Student student = studentService.getStudent(studentId);
        if (student != null) {
            List<ScheduledClass> classes = classService.getScheduledClassesByCourseCode(courseCode);
            for (ScheduledClass sc : classes) {
                if (sc.getStartDate().equals(startDate)) {
                    sc.addStudent(student);
                    student.addClass(sc);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean dropStudentFromClass(int studentId, String courseCode, LocalDate startDate) {
        Student student = studentService.getStudent(studentId);
        List<ScheduledClass> classes = classService.getScheduledClassesByCourseCode(courseCode);
        for (ScheduledClass sc : classes) {
            if (sc.getStartDate().equals(startDate)) {
                sc.removeStudent(student);
                student.dropClass(sc);
                return true;
            }
        }
        return false;
    }

    public List<Student> getStudentsForClass(String courseCode, LocalDate startDate) {
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
