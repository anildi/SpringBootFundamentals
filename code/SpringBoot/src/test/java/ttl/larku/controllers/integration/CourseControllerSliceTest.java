package ttl.larku.controllers.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ttl.larku.controllers.rest.CourseRestController;
import ttl.larku.controllers.rest.StudentRestController;
import ttl.larku.controllers.rest.UriCreator;
import ttl.larku.domain.Course;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@WebMvcTest(controllers = CourseRestController.class)
@WebMvcTest(controllers = {StudentRestController.class, CourseRestController.class })
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag("mvcslice")
public class CourseControllerSliceTest {

    @MockBean
    private StudentService studentService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UriCreator uriCreator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    private final String badCourseCode = "Boo";
    private final String goodCourseCode = "Math-101";
    private final int goodCourseId = 1;
    private final int badCourseId = 10000;

    List<Course> courses = Arrays.asList(new Course("Math-101", "Baby Math"),
            new Course("Math-303", "Grown up Math"));

    @BeforeEach
    public void setup() {
        int count = context.getBeanDefinitionCount();
        System.out.println("Bean count = " + count);

    }

    @Test
    public void testGetOneCourseGoodJson() throws Exception {
        int goodId = 1;
        Mockito.when(courseService.getCourse(goodId)).thenReturn(Optional.of(courses.get(0)));

        MediaType accept = MediaType.APPLICATION_JSON;
        MediaType contentType = accept;

        MockHttpServletRequestBuilder builder =
                get("/adminrest/course/{id}", goodId)
                        .accept(accept)
                        .contentType(contentType);


        ResultActions actions = mockMvc.perform(builder);

        actions = actions.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.entity.code").value(containsString("Math-101")));

        // Get the result and return it
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonString = response.getContentAsString();

        System.out.println("One course good resp = " + jsonString);

        Mockito.verify(courseService).getCourse(goodId);
    }

    @Test
    public void testGetOneCourseBadId() throws Exception {
        Mockito.when(courseService.getCourse(badCourseId)).thenReturn(Optional.empty());
        ResultActions actions = mockMvc
                .perform(get("/adminrest/course/{id}", badCourseId)
                        .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcr = actions
                .andExpect(status().is4xxClientError())
                .andReturn();

        Mockito.verify(courseService).getCourse(badCourseId);
    }

    @Test
    public void testAddCourseGood() throws Exception {

        Course course = new Course("CHEM-202", "Organic Chemistry");
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(course);

        Mockito.when(courseService.createCourse(course)).thenReturn(course);
        Mockito.when(uriCreator.getURI(0))
                .thenReturn(new URI("http://localhost:8080/adminrest/course/0"));

        ResultActions actions = mockMvc.
                perform(post("/adminrest/course")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));

        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isCreated());

        actions = actions.andExpect(jsonPath("$.entity.code").value(Matchers.containsString("CHEM-202")));


        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);

        Mockito.verify(courseService).createCourse(course);
        Mockito.verify(uriCreator).getURI(0);

    }

    @Test
    public void testUpdateCourseGood() throws Exception {

        Course c = courses.get(0);
        c.setId(goodCourseId);
        Mockito.when(courseService.getCourse(goodCourseId))
              .thenReturn(Optional.of(courses.get(0)));

        ResultActions actions = mockMvc
                .perform(get("/adminrest/course/{id}", goodCourseId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonString).path("entity");

        Course course = mapper.treeToValue(node, Course.class);
        course.setTitle(course.getTitle().toUpperCase());

        String title = course.getTitle();

        String updatedJson = mapper.writeValueAsString(course);

        Mockito.when(courseService.updateCourse(course)).thenReturn(true);
        ResultActions putActions = mockMvc.perform(put("/adminrest/course")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isNoContent());

        MvcResult result = putActions.andReturn();

        Mockito.when(courseService.getCourse(course.getId())).thenReturn(Optional.of(course));
        ResultActions postPutActions = mockMvc
                .perform(get("/adminrest/course/{id}", goodCourseId)
                        .accept(MediaType.APPLICATION_JSON));
        String postJson = postPutActions.andReturn().getResponse().getContentAsString();

        Course postCourse = mapper.treeToValue(mapper.readTree(postJson).path("entity"), Course.class);
        assertEquals(title , postCourse.getTitle());

        Mockito.verify(courseService, times(2)).getCourse(goodCourseId);
        Mockito.verify(courseService).updateCourse(course);
    }

    @Test
    public void testUpdateCourseBadId() throws Exception {

        Course c = courses.get(0);
        c.setId(goodCourseId);
        Mockito.when(courseService.getCourse(c.getId())).thenReturn(Optional.of(c));

        ResultActions actions = mockMvc
                .perform(get("/adminrest/course/{id}", c.getId())
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonString).path("entity");

        Course course = mapper.treeToValue(node, Course.class);
        course.setId(badCourseId);

        String updatedJson = mapper.writeValueAsString(course);

        Mockito.when(courseService.getCourse(course.getId())).thenReturn(Optional.empty());
        ResultActions putActions = mockMvc.perform(put("/adminrest/course")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isBadRequest());

        Mockito.verify(courseService, times(1)).getCourse(anyInt());
        Mockito.verify(courseService).updateCourse(course);

    }

    @Test
    public void testDeleteCourseGood() throws Exception {

        Course firstCourse = courses.get(0);
        firstCourse.setId(goodCourseId);

        Mockito.when(courseService.getCourse(firstCourse.getId()))
                .thenReturn(Optional.of(firstCourse));
        ResultActions actions = mockMvc
                .perform(get("/adminrest/course/{id}", firstCourse.getId())
                        .accept(MediaType.APPLICATION_JSON));

        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        Mockito.when(courseService.deleteCourse(firstCourse.getId())).thenReturn(true);
        ResultActions deleteActions = mockMvc.perform(delete("/adminrest/course/{id}", firstCourse.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        deleteActions = deleteActions.andExpect(status().isNoContent());

        Mockito.when(courseService.getCourse(firstCourse.getId())).thenReturn(Optional.empty());
        ResultActions postDeleteActions = mockMvc
                .perform(get("/adminrest/course/{id}", firstCourse.getId())
                        .accept(MediaType.APPLICATION_JSON));

        postDeleteActions = postDeleteActions.andExpect(status().isBadRequest());

        Mockito.verify(courseService, times(2)).getCourse(anyInt());
        Mockito.verify(courseService).deleteCourse(firstCourse.getId());
    }

    @Test
    public void testDeleteStudentBad() throws Exception {

        ResultActions actions = mockMvc
                .perform(delete("/adminrest/course/{id}", badCourseId)
                        .accept(MediaType.APPLICATION_JSON));
        actions.andExpect(status().isBadRequest());

        Mockito.verify(courseService).deleteCourse(anyInt());
    }


    @Test
    public void testGetAllStudentsGood() throws Exception {

        Mockito.when(courseService.getAllCourses()).thenReturn(courses);

        ResultActions actions = mockMvc.perform(get("/adminrest/course").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity", hasSize(greaterThan(0))));
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        String jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);

        Mockito.verify(courseService).getAllCourses();

    }

    @Test
    public void testGetGoodCourseByCode() throws Exception {

        Mockito.when(courseService.getCourseByCode(goodCourseCode)).thenReturn(Optional.of(courses.get(0)));
        ResultActions actions = mockMvc.perform(get("/adminrest/course/code/{code}", goodCourseCode)
                .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity.code").value(Matchers.containsString(goodCourseCode)));
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        String jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);

        Mockito.verify(courseService).getCourseByCode(goodCourseCode);
    }

    @Test
    public void testGetGoodCourseByBadCode() throws Exception {

        Mockito.when(courseService.getCourseByCode(badCourseCode)).thenReturn(Optional.empty());
        ResultActions actions = mockMvc.perform(get("/adminrest/course/code/{code}", badCourseCode)
                .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isBadRequest());

        Mockito.verify(courseService).getCourseByCode(badCourseCode);
    }


}
