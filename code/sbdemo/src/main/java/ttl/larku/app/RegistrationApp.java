package ttl.larku.app;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ttl.larku.domain.Course;
import ttl.larku.domain.Student;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

import java.util.List;

public class RegistrationApp {

    public static void main(String[] args) {
        //ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
//        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(LarkUConfig.class);
		//Setting a profile on an Application Context.

		AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
		appContext.getEnvironment().setActiveProfiles("development");
        //Turn off or on Bean definition overloading.
        //true == allowed, false == exception thrown if two
        //beans have same name
        DefaultListableBeanFactory dlb = (DefaultListableBeanFactory)appContext.getBeanFactory();
        dlb.setAllowBeanDefinitionOverriding(false);

		appContext.scan("ttl.larku");
		appContext.refresh();

        //Using more than one config file
        //ApplicationContext appContext = new AnnotationConfigApplicationContext(LarkUConfig.class, TwoConfig.class);

        StudentService ss = appContext.getBean("studentService", StudentService.class);

        StudentService ss2 = appContext.getBean("studentService", StudentService.class);
        initStudents(ss);

        List<Student> students = ss.getAllStudents();
        students.forEach(System.out::println);

        CourseService cs = appContext.getBean("courseService", CourseService.class);
        initCourses(cs);

        List<Course> courses = cs.getAllCourses();
        courses.forEach(System.out::println);
    }

    public static void initStudents(StudentService ss) {
        ss.createStudent("Manoj", "282 939 9944", Student.Status.FULL_TIME);
        ss.createStudent("Charlene", "282 898 2145", Student.Status.FULL_TIME);
        ss.createStudent("Firoze", "228 678 8765", Student.Status.HIBERNATING);
        ss.createStudent("Joe", "3838 678 3838", Student.Status.PART_TIME);
    }

    public static void initCourses(CourseService cs) {
        cs.createCourse("MATH-101", "Intro To Math");
        cs.createCourse("PHY-101", "Intro To Physics");
        cs.createCourse("PHY-102", "Yet more Physics");
    }

}
