package ttl.larku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//Populate your DB.  From Most Expensive to least expensive

//This will make recreate the context after every test.
//In conjunction with appropriate 'schema[-XXX].sql' and 'data[-XXX].sql' files
//it will also drop and recreate the DB before each test.
//@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

//Or you can just re-run the sql files before each test method
@Sql(scripts = { "/ttl/larku/db/createDB-h2.sql", "/ttl/larku/db/populateDB-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)

//This next one will roll back the transaction after
//each test, so the database will actually stay the
//same for the next test.
//@Transactional
@Tag("integration")
public class RegistrationRepoServiceTest {

    private String name1 = "Manoj";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";

    private String code1 = "MATH-101";
    private String code2 = "BOT-202";
    private String badCode = "BADNESS";
    private String title1 = "Intro To Botany";
    private String title2 = "Outtro To Botany";

    private LocalDate startDate1 = LocalDate.parse("2012-10-10");
    private LocalDate startDate2 = LocalDate.parse("2013-10-10");
    private LocalDate endDate1 = LocalDate.parse("2013-05-10");
    private LocalDate endDate2 = LocalDate.parse("2014-05-10");

    private LocalDate badStartDate = LocalDate.parse("2099-12-12");

    private Course course1;
    private Course course2;
    private ScheduledClass class1;
    private ScheduledClass class2;
    private Student student1;
    private Student student2;
	
	/*
	private ClassService classService;
	private CourseService courseService;
	private StudentService studentService;
	*/

    @Resource(name = "registrationRepoService")
    private RegistrationService regService;

    @Autowired
    private ApplicationContext appContext;

    @BeforeEach
    public void setup() {
        student1 = new Student(name1);
        student2 = new Student(name2);
        course1 = new Course(code1, title1);
        course2 = new Course(code2, title2);

        class1 = new ScheduledClass(course1, startDate1, endDate1);
        class2 = new ScheduledClass(course2, startDate2, endDate2);

    }

    @Test
    public void testAddClassAndStudent() {
        ScheduledClass sClass = regService.addNewClassToSchedule(code1, startDate1, endDate1);
        Student student1 = regService.getStudentService().createStudent("Josephine");


        regService.registerStudentForClass(student1.getId(), code1, startDate1);

        List<Student> students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(2, students.size());

        assertEquals(1, regService.getStudentService().getStudent(student1.getId()).getClasses().size());
    }

    @Test
    public void testDropStudent() {
        ScheduledClass sClass = regService.addNewClassToSchedule(code1, startDate1, endDate1);
        Student student1 = regService.getStudentService().getStudent(1);
        Student student2 = regService.getStudentService().getStudent(2);

        regService.registerStudentForClass(student1.getId(), code1, startDate1);
//        regService.registerStudentForClass(student2.getId(), code1, startDate1);

        List<Student> students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(2, students.size());
        assertTrue(regService.getStudentService().getStudent(student1.getId()).getName().contains(name1));

        regService.dropStudentFromClass(student1.getId(), code1, startDate1);

        students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(1, students.size());
        assertEquals(student2.getName(), students.get(0).getName());
    }

    @Test
    public void testRegisterForNonExistentClass() {
        ScheduledClass sClass = regService.addNewClassToSchedule(code1, startDate1, endDate1);

        regService.registerStudentForClass(student1.getId(), code1, badStartDate);

        List<Student> students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(1, students.size());
    }

    @Test
    public void testDropStudentFromNonExistentClass() {
        ScheduledClass sClass = regService.addNewClassToSchedule(code1, startDate1, endDate1);
        Student student1 = regService.getStudentService().getStudent(1);
        Student student2 = regService.getStudentService().getStudent(2);
        regService.registerStudentForClass(student1.getId(), code1, startDate1);
//        regService.registerStudentForClass(student2.getId(), code1, startDate1);

        List<Student> students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(2, students.size());
        regService.dropStudentFromClass(student1.getId(), code1, badStartDate);

        students = regService.getStudentsForClass(code1, startDate1);

        assertEquals(2, students.size());
    }

    @Test
    public void testGetStudentsFromNonExistentClass() {

        List<Student> students = regService.getStudentsForClass(code1, badStartDate);

        assertEquals(0, students.size());
    }

    @Test
    public void testGetAllScheduledClasses() {
        ScheduledClass sClass = regService.addNewClassToSchedule(code1, startDate1, endDate1);

        List<ScheduledClass> classes = regService.getScheduledClasses();

        assertEquals(4, classes.size());

        assertEquals(sClass.getStartDate(), regService.getClassService().getScheduledClass(sClass.getId()).getStartDate());
    }
}
