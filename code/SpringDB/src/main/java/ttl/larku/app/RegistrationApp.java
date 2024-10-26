package ttl.larku.app;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ttl.larku.LarkUConfig;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.service.CourseDaoService;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentDaoService;
import ttl.larku.service.StudentService;

public class RegistrationApp {

    public static void main(String[] args) {
        //ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		/*
		AnnotationConfigApplicationContext appContext = 
				new AnnotationConfigApplicationContext(LarkUConfig.class);
		*/
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.getEnvironment().setActiveProfiles("development");
        appContext.register(LarkUConfig.class);
        appContext.refresh();


        //ApplicationContext appContext = new AnnotationConfigApplicationContext(LarkUConfig.class, TwoConfig.class);

        StudentDaoService ss = appContext.getBean("studentDaoService", StudentDaoService.class);

        StudentService ss2 = appContext.getBean("studentDaoService", StudentDaoService.class);
        initStudents(ss);

        List<Student> students = ss.getAllStudents();
        students.forEach(System.out::println);

        CourseService cs = appContext.getBean("courseService", CourseDaoService.class);
        initCourses(cs);

        List<Course> courses = cs.getAllCourses();
        courses.forEach(System.out::println);
    }

    public static void initStudents(StudentDaoService ss) {
        ss.createStudent("Manoj", "282 939 9944", LocalDate.of(1999, 10, 20), Student.Status.FULL_TIME);
        ss.createStudent("Charlene", "282 898 2145", LocalDate.of(2000, 5, 20), Student.Status.FULL_TIME);
        ss.createStudent("Firoze", "228 678 8765", LocalDate.of(1972, 8, 20), Student.Status.HIBERNATING);
        ss.createStudent("Joe", "3838 678 3838", LocalDate.of(2010, 7, 20), Student.Status.PART_TIME);
    }

    public static void initCourses(CourseService cs) {
        cs.createCourse("MATH-101", "Intro To Math");
        cs.createCourse("PHY-101", "Intro To Physics");
        cs.createCourse("PHY-102", "Yet more Physics");
    }

}
