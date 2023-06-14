package ttl.larku.controllers.integration.slice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ttl.larku.LarkUTestDataConfig;
import ttl.larku.controllers.rest.CourseRestController;
import ttl.larku.controllers.rest.RegistrationRestController;
import ttl.larku.controllers.rest.StudentRestController;
import ttl.larku.controllers.rest.UriCreator;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.service.ClassService;
import ttl.larku.service.CourseService;
import ttl.larku.service.RegistrationService;
import ttl.larku.service.StudentService;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@WebMvcTest(controllers = {RegistrationRestController.class})
@WebMvcTest(controllers = {RegistrationRestController.class,
        StudentRestController.class, CourseRestController.class })
//@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag("mvcslice")
public class RegistrationRestControllerSliceTest {

    @MockBean
    private StudentService studentService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private ClassService classService;

    @MockBean
    private UriCreator uriCreator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    private LarkUTestDataConfig testData = new LarkUTestDataConfig();

    private DateTimeFormatter dtFormatter;

    private Course course = new Course("Math-101", "Baby Math");
    private List<ScheduledClass> classes = List.of(
            new ScheduledClass(course, LocalDate.of(2023, 10, 10), LocalDate.of(2024, 5, 2)),
            new ScheduledClass(course, LocalDate.of(2024, 10, 10), LocalDate.of(2025, 5, 2))
    );

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
        dtFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    }

    @Test
    public void testGetAll() throws Exception {
        Mockito.when(classService.getAllScheduledClasses()).thenReturn(classes);

        ResultActions actions = mockMvc
                .perform(get("/adminrest/class/").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        Mockito.verify(classService).getAllScheduledClasses();
    }

    @Test
    public void testGetOne() throws Exception {

        Mockito.when(classService.getScheduledClass(1)).thenReturn(classes.get(0));

        ResultActions actions = mockMvc
                .perform(get("/adminrest/class/1").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        Mockito.verify(classService).getScheduledClass(1);
    }

    @Test
    public void testGetOnePath() throws Exception {

        Mockito.when(classService.getScheduledClassesByCourseCode(course.getCode()))
                .thenReturn(classes);
        ResultActions actions = mockMvc
                .perform(get("/adminrest/class/code/" + course.getCode())
                        .param("startDate", "2012-10-10")
                        .param("endDate", "2013-10-10")
                        .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        Mockito.verify(classService).getScheduledClassesByCourseCode(course.getCode());
    }

    @Test
    public void addOneQueryParams() throws Exception {

        Mockito.when(registrationService.addNewClassToSchedule(any(String.class),
                any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(classes.get(0));
        Mockito.when(uriCreator.getUriFor(classes.get(0).getId()))
                .thenReturn(new URI("http://localhost:8080/class/0"));
        ResultActions actions = mockMvc
                .perform(post("/adminrest/class/")
                        .param("courseCode", course.getCode())
                        .param("startDate", "2019-05-05")
                        .param("endDate", "2019-10-05")
                        .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isCreated());

        Mockito.verify(registrationService).addNewClassToSchedule(any(String.class),
                any(LocalDate.class), any(LocalDate.class));
    }


//    @Test
//    public void testRegisterStudentAllGood() throws Exception {
//        List<Student> students = studentService.getAllStudents();
//        List<ScheduledClass> classes = classService.getAllScheduledClasses();
//        int studentId = students.get(0).getId();
//        int classId = classes.get(0).getId();
//
//        ResultActions actions = mockMvc
//                .perform(post("/adminrest/class/register/{studentId}/{classId}", studentId, classId)
//                        .accept(MediaType.APPLICATION_JSON));
//
//        actions = actions.andExpect(status().isOk());
//
//        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
//
//
//        //Get the student and make sure they have to class
//        Student s = studentService.getStudent(studentId);
//        List<ScheduledClass> sClasses = s.getClasses();
//
//        boolean hasIt = sClasses.stream().anyMatch(sc -> sc.getId() == classId);
//        assertTrue(hasIt);
//
//        MvcResult mvcr = actions.andReturn();
//
//        String reo = (String) mvcr.getResponse().getContentAsString();
//        System.out.println("Reo is " + reo);
//    }
}
