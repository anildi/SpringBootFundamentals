package ttl.larku.testcontainers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ttl.larku.dao.repository.SimpleStudentRepo;
import ttl.larku.dao.repository.StudentRepo;
import ttl.larku.domain.Student;
import ttl.larku.domain.StudentCourseCodeSummary;
import ttl.larku.domain.StudentPhoneSummary;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.sql.init.mode=never"})
@Testcontainers
@Transactional
@Tag("container")
@Disabled
public class StudentRepoMysqlContainerTest {

    private String name1 = "Bloke";
    private String name2 = "Blokess";
    private String newName = "Different Bloke";
    private Student student1;
    private Student student2;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private SimpleStudentRepo simple;

    @Autowired
    private ApplicationContext context;

    @Container
    @ServiceConnection
    private static MySQLContainer<?> mysqlContainer;

    static {
        mysqlContainer = new MySQLContainer<>("mysql:8.0.26")
                .withDatabaseName("larku")
                .withUsername("larku")
                .withPassword("larku");
//                .withInitScript("sql/postgres/3-postgress-larku-schema.sql")
//                .withInitScript("sql/postgres/4-postgress-larku-data.sql");
//            .withExposedPorts(9999)
//            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
//                new HostConfig().withPortBindings(
//                        new PortBinding(Ports.Binding.bindPort(9999), new ExposedPort(9999)))
//            ));

    }
    @BeforeAll
    public static void beforeAll() {
        mysqlContainer.start();
        //Initialize
        var containerDelegate = new JdbcDatabaseDelegate(mysqlContainer, "");

        ScriptUtils.runInitScript(containerDelegate, "sql/mysql/3-mysql-larku-schema.sql");
        ScriptUtils.runInitScript(containerDelegate, "sql/mysql/4-mysql-larku-data.sql");
    }

    @AfterAll
    public static void afterAll() {
        mysqlContainer.stop();
    }

    @BeforeEach
    public void setup() {

        student1 = new Student(name1);
        student2 = new Student(name2);

//        for(String name: context.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }
//        System.out.println(context.getBeanDefinitionCount() + " beans");
    }

    @Test
    public void testGetOneStudent() {
        Student student = studentRepo.bigSelectOne(1);

        System.out.println("bigSelect: " + student + ", classes: " + student.getClasses());

        assertEquals(2, student.getClasses().size());
    }

    @Test
    //Turn off Transactions by uncommenting @Transactional
    //If you then try and print the collection, you will
    //get a Lazy Instantiation Exception.
    @Transactional
    public void testGetAll() {
        //Also check out the definition of findAll in the StudentRepo.
        //You can set it up there with a custom query to do a
        //left join fetch.  In which case you will not get
        //either an LIE or the n + 1 selects.
        List<Student> students = studentRepo.findAll();
        //With @Transactional, this will show the n + 1 problem.
        //Will do 5 selects instead of 1.
        //With no @Transactional, this will throw a LazyInstantiationException.
        students.forEach(s -> System.out.println("classes size for " + s.getName() + " is " + s.getClasses().size()));

        assertEquals(4, students.size());
    }


    @Test
    public void testCreate() {

        int newId = studentRepo.save(student1).getId();

        Student resultStudent = studentRepo.findById(newId).orElse(null);

        assertEquals(newId, resultStudent.getId());
    }

    @Test
    public void testUpdate() {
        int newId = studentRepo.save(student1).getId();

        Student resultStudent = studentRepo.findById(newId).orElse(null);

        assertEquals(newId, resultStudent.getId());

        resultStudent.setName(newName);
        studentRepo.save(resultStudent);

        resultStudent = studentRepo.findById(newId).orElse(null);
        assertEquals(newName, resultStudent.getName());
    }

    @Test
    public void testDelete() {
        int newId = studentRepo.save(student1).getId();

        Student resultStudent = studentRepo.findById(newId).orElse(null);
        assertEquals(newId, resultStudent.getId());

        studentRepo.delete(resultStudent);

        resultStudent = studentRepo.findById(newId).orElse(null);

        assertEquals(null, resultStudent);
    }

    @Test
    public void testFindByName() {
        Student newManoj = new Student("Manoj");
        studentRepo.save(newManoj);
        List<Student> manojes = studentRepo.findByNameIgnoreCaseContains("Manoj");

        assertEquals(2, manojes.size());
    }

    /**
     * Test Paging.
     */
    @Test
    public void testPaging() {
        // first add a bunch of student so we have something to page through
        //Our Transaction will get rolled back at the end, so no harm done.
        for (int i = 0; i < 50; i++) {
            Student s = new Student("Fake #" + i);
            studentRepo.save(s);
        }

        int currPage = 0;
        int size = 20;
        int totalElements = 0;
        //Set up sorting criteria
        Sort sort = Sort.by("name").descending();
        //Use the paging variation of the findAll method.
        Page<Student> page = null;
        do {
            page = studentRepo.findAll(PageRequest.of(currPage++, size, sort));
            totalElements += page.getNumberOfElements();
            System.out.println("Number: " + page.getNumber() + ", numElements: " + page.getNumberOfElements());
            page.forEach(System.out::println);
        }while(page.hasNext());

        assertEquals(54, totalElements);
        assertEquals(2, page.getNumber());
        }

    @Test
    public void testProjectionPhoneSummaryById() {
        StudentPhoneSummary phoneSummary = studentRepo.findPhoneSummaryById(2);
        assertNotNull(phoneSummary);
    }

    @Test
    public void testProjectionPhoneSummary() {
        List<StudentPhoneSummary> l = studentRepo.findAllStudentPhoneSummariesBy();
        assertEquals(4, l.size());

        l.forEach(sp -> System.out.println(sp.getId() + ": " + sp.getName() + ", " + sp.getPhoneNumber()));
        l.forEach(System.out::println);

    }

    @Test
    public void testStudentCourseCodeSummary() {
        List<StudentCourseCodeSummary> l = studentRepo.findStudentCourseCodesBy();
        assertEquals(4, l.size());

        l.forEach(sp -> {
            System.out.println(sp.getId() + ": " + sp.getName());
            var x = sp.getClasses();
            sp.getClasses().forEach(it -> System.out.println("course: " + it.getCourse() + ", id: " + it.getId() + it.getStartDate()));
        });


    }

    /**
     * An example of using a Pageable with a Projection
     */
    @Test
    public void testProjectionStudentClassCourseCode() {
        // first add a bunch of student so we have something to page through
        for (int i = 0; i < 50; i++) {
            Student s = new Student("Fake #" + i);
            studentRepo.save(s);
        }

        int currPage = 0;
        int size = 20;
        Sort sort = Sort.by("name").descending();
        Page<StudentCourseCodeSummary> page = studentRepo.findPageCourseCodeBy(PageRequest.of(currPage++, size, sort));
        System.out.println("Number: " + page.getNumber() + ", numElements: " + page.getNumberOfElements());
        dumpPage(page);
        while (page.hasNext()) {
            page = studentRepo.findPageCourseCodeBy(PageRequest.of(currPage++, size, sort));
            System.out.println("Number: " + page.getNumber() + ", numElements: " + page.getNumberOfElements());
            dumpPage(page);
        }

    }

    public void dumpPage(Page<StudentCourseCodeSummary> page) {
        page.forEach(sp -> {
            System.out.println(sp.getId() + ": " + sp.getName());
            sp.getClasses().forEach(s -> System.out.println("     " + s.getCourse() + ", " + s.getStartDate()));
        });

    }
}
