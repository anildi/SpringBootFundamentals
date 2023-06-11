package ttl.larku.service.reg.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Different ways of creating the Context.  From Most Expensive to
// Not So Expensive.
/**********************
 //This is the most expensive way to make the context.  The entire
 //Spring Context will be created. About 104 beans, at this writing.
 **********************/
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)

/**********************
 //This next approach will just create the beans in the given
 //configuration classes. //About 13 beans at this writing.
 //Note that we need to include StudentService.class here only because
 //we have declared it with @Service, and no classpath scanning
 //will take place if we use this approach.  Alternative would
 //be to declare the StudentService in LarkUConfig.
 ***********************/
//@SpringBootTest(classes = {LarkUConfig.class, LarkUTestDataConfig.class, StudentService.class})

/*****************************
 * Finally, we can simply pull in the exact classes we need
 * for the test.  This will still pull in some other beans.
 * About 10 beans at this writing.
 **********/
//@SpringBootTest(classes = {StudentService.class, InMemoryStudentDAO.class})

/*************************
 * And finally finally, we can use a TestConfiguration, as we are doing here.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//@SpringBootTest(classes = {MyTestConfig.class})
//@EnabledIf(expression = "#{environment.acceptsProfiles('development')}", loadContext = true)
//@EnabledIf(expression = "#{'${spring.profiles.active}' == 'bory'}", loadContext = true)
@Tag("integration")
public class StudentServiceTestWithTestConfig {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Karl Jung";
    private String phoneNumber1 = "290 298 4790";
    private String phoneNumber2 = "3838 939 93939";

    @Resource(name = "studentService")
    private StudentService studentService;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    public void setup() {
//        for(String name : context.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }
//        System.out.println(context.getBeanDefinitionCount() + " beans");
        //studentService = new StudentService();
        //studentService = applicationContext.getBean("studentService", StudentService.class);
        studentService.clear();
//        for(String name: context.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }
//        System.out.println(getClass().getName() + ":" + context.getBeanDefinitionCount() + " beans");
    }

    @Test
    public void testCreateStudent() {
        Student newStudent = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);

        Student result = studentService.getStudent(newStudent.getId());

        System.out.println("result: " + result);

        assertTrue(result.getName().contains(name1));
        assertEquals(1, studentService.getAllStudents().size());
    }

    @Test
    public void testDeleteStudent() {
        Student student1 = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);
        Student student2 = new Student(name1, phoneNumber1, Student.Status.FULL_TIME);
        student2 = studentService.createStudent(student2);

        assertEquals(2, studentService.getAllStudents().size());

        studentService.deleteStudent(student1.getId());

        assertEquals(1, studentService.getAllStudents().size());
        assertTrue(studentService.getAllStudents().get(0).getName().contains(name1));
    }

    @Test
    public void testDeleteNonExistentStudent() {
        Student student1 = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);
        Student student2 = new Student(name1, phoneNumber1, Student.Status.FULL_TIME);
        student2 = studentService.createStudent(student2);

        assertEquals(2, studentService.getAllStudents().size());

        //Non existent Id
        studentService.deleteStudent(9999);

        assertEquals(2, studentService.getAllStudents().size());
    }

    @Test
    public void testUpdateStudent() {
        Student student1 = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);

        assertEquals(1, studentService.getAllStudents().size());

        student1.setName(name2);
        studentService.updateStudent(student1);

        assertEquals(1, studentService.getAllStudents().size());
        assertTrue(studentService.getAllStudents().get(0).getName().contains(name2));
    }


    @Test
    public void testGetByName() {
        Student student1 = studentService.createStudent(name1, phoneNumber1, Student.Status.FULL_TIME);
        Student student2 = studentService.createStudent(name2, phoneNumber2, Student.Status.FULL_TIME);

        assertEquals(2, studentService.getAllStudents().size());
        String searchName = "InMem: " + name1;
        List<Student> blokes = studentService.getByName(searchName);

        assertEquals(1, blokes.size());
    }
}


