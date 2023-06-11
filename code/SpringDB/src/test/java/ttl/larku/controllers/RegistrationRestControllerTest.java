package ttl.larku.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.service.ClassService;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;
import ttl.larku.sql.SqlScriptBase;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@Tag("mvc")
public class RegistrationRestControllerTest extends SqlScriptBase {

    @Resource(name = "studentRepoService")
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClassService classService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private DateTimeFormatter dtFormatter;

    /**
     * A Technique to run sql scripts just once per class.
     * This was to solve a tricky situation.  This class
     * uses the webEnvironment.MOCK context, which would
     * have already been created and cached by a previous
     * test.  Since no context is created for this test,
     * no DDL scripts are run.  So this test gets whatever
     * was in put into the database by the previous test.
     * If that was using different data, e.g. the "VersionedXXX"
     * sql scripts, then tests in this class can fail in
     * strange ways that depend on which other tests are run.
     * This trick makes sure that this test starts with the
     * data it is expecting.  The @Transactional then takes
     * care of rolling back after each test.
     *
     * We are also using the strange fact that Spring auto wiring
     * works on this static method to inject a ScriptFileProperties
     * bean, which gets properties set with @ConfigurationProperties.
     * This allows us to use properties to decide which scripts to
     * run.  The properties are in larkUContext.properties.
     * @param dataSource
     * @throws SQLException
     */
//    @BeforeAll
//    public static void runSqlScriptsOnce(@Autowired DataSource dataSource,
//                            @Autowired ScriptFileProperties props) throws SQLException {
//        try (Connection conn = dataSource.getConnection()) {
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource(props.getSchemaFile()));
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource(props.getDataFile()));
//        }
//    }

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
        dtFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    }

    @Test
    public void testGetAll() throws Exception {
        ResultActions actions = mockMvc
                .perform(get("/adminrest/class/").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity", hasSize(3)));

        MvcResult mvcr = actions.andReturn();
        String reo = (String) mvcr.getResponse().getContentAsString();
        System.out.println("Reo is " + reo);
    }

    @Test
    public void testGetOne() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/class/1").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity.startDate")
                .value("2012-10-10"));

        MvcResult mvcr = actions.andReturn();
        String reo = (String) mvcr.getResponse().getContentAsString();
        System.out.println("Reo is " + reo);
    }

    @Test
    public void testGetOnePath() throws Exception {
        ResultActions a1 = mockMvc
                .perform(get("/adminrest/course").accept(MediaType.APPLICATION_JSON));
        MvcResult m1 = a1.andReturn();
        String r1 = (String)m1.getResponse().getContentAsString();
//        System.err.println("All Courses: " + r1);

        ResultActions a2 = mockMvc
                .perform(get("/adminrest/class").accept(MediaType.APPLICATION_JSON));
        MvcResult m2 = a2.andReturn();
        String r2 = m2.getResponse().getContentAsString();
        System.err.println("All Classes: " + r2);

        ResultActions actions = mockMvc
                .perform(get("/adminrest/class/code/MATH-101")
                        .param("startDate", "2012-10-10")
                        .param("endDate", "2013-10-10")
                        .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));


        actions = actions.andExpect(jsonPath("$.entity[0].startDate")
                .value("2012-10-10"));

        MvcResult mvcr = actions.andReturn();
        String reo = (String) mvcr.getResponse().getContentAsString();
        System.out.println("Reo is " + reo);
    }

    @Test
    public void addOneQueryParams() throws Exception {
        ResultActions actions = mockMvc
                .perform(post("/adminrest/class/").param("courseCode", "BKTW-101")
                        .param("startDate", "2019-05-05")
                        .param("endDate", "2019-10-05")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isCreated());

        actions = actions.andExpect(jsonPath("$.entity.startDate").value("2019-05-05"));

        MvcResult mvcr = actions.andReturn();
        String reo = (String) mvcr.getResponse().getContentAsString();
        System.out.println("Reo is " + reo);
    }


    @Test
    public void testRegisterStudentAllGood() throws Exception {
        List<Student> students = studentService.getAllStudents();
        List<ScheduledClass> classes = classService.getAllScheduledClasses();
        int studentId = students.get(0).getId();
        int classId = classes.get(0).getId();

        ResultActions actions = mockMvc
                .perform(post("/adminrest/class/register/{studentId}/{classId}", studentId, classId)
                        .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));


        //Get the student and make sure they have to class
        Student s = studentService.getStudent(studentId);
//        List<ScheduledClass> sClasses = s.getClasses();
        Set<ScheduledClass> sClasses = s.getClasses();

        boolean hasIt = sClasses.stream().anyMatch(sc -> sc.getId() == classId);
        assertTrue(hasIt);

        MvcResult mvcr = actions.andReturn();

        String reo = (String) mvcr.getResponse().getContentAsString();
        System.out.println("Reo is " + reo);
    }
}
