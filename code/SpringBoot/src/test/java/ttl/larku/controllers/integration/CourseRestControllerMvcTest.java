package ttl.larku.controllers.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ttl.larku.domain.Course;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag("integration")
public class CourseRestControllerMvcTest {


    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    private final int goodCourseId = 1;
    private final int deleteCourseId = 2;
    private final int badCourseID = 10000;
    private final String goodCourseCode = "BKTW-101";
    private final String badCourseCode = "NOT-THERE";


    @BeforeEach
    public void init() {
    }

    @Test
    public void testGetOneCourseGoodJson() throws Exception {
        MediaType accept = MediaType.APPLICATION_JSON;
        MediaType contentType = accept;

        MockHttpServletRequestBuilder builder = get("/adminrest/course/{id}", goodCourseId)
            .accept(accept)
            .contentType(contentType);


        ResultActions actions = mockMvc.perform(builder);

        actions = actions.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.entity.code").value(containsString("BKTW-101")));

        // Get the result and return it
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonString = response.getContentAsString();

        System.out.println("One course good resp = " + jsonString);
    }

    @Test
    public void testGetOneCourseBadId() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/course/{id}", badCourseID)
                        .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcr = actions
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void testAddCourseGood() throws Exception {

        Course course = new Course("CHEM-202", "Organic Chemistry");
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(course);

        ResultActions actions = mockMvc.
                perform(post("/adminrest/course/")
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

    }

    @Test
    public void testUpdateCourseGood() throws Exception {

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

        ResultActions putActions = mockMvc.perform(put("/adminrest/course/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isNoContent());

        MvcResult result = putActions.andReturn();

        ResultActions postPutActions = mockMvc
                .perform(get("/adminrest/course/{id}", goodCourseId)
                        .accept(MediaType.APPLICATION_JSON));
        String postJson = postPutActions.andReturn().getResponse().getContentAsString();

        Course postCourse = mapper.treeToValue(mapper.readTree(postJson).path("entity"), Course.class);
        assertEquals(title , postCourse.getTitle());
    }

    @Test
    public void testUpdateCourseBadId() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/course/{id}", goodCourseId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonString).path("entity");

        Course course = mapper.treeToValue(node, Course.class);
        course.setId(badCourseID);

        String updatedJson = mapper.writeValueAsString(course);

        ResultActions putActions = mockMvc.perform(put("/adminrest/course/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isBadRequest());

    }

    @Test
    public void testDeleteCourseGood() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/course/{id}", deleteCourseId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        ResultActions deleteActions = mockMvc.perform(delete("/adminrest/course/{id}", deleteCourseId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        deleteActions = deleteActions.andExpect(status().isNoContent());

        ResultActions postDeleteActions = mockMvc
                .perform(get("/adminrest/course/{id}", deleteCourseId)
                        .accept(MediaType.APPLICATION_JSON));

        postDeleteActions = postDeleteActions.andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteStudentBad() throws Exception {

        ResultActions actions = mockMvc
                .perform(delete("/adminrest/course/{id}", badCourseID)
                        .accept(MediaType.APPLICATION_JSON));
        actions.andExpect(status().isBadRequest());
    }


    @Test
    public void testGetAllStudentsGood() throws Exception {

        ResultActions actions = mockMvc.perform(get("/adminrest/course/").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity", hasSize(greaterThan(0))));
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        String jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);
    }

    @Test
    public void testGetGoodCourseByCode() throws Exception {

        ResultActions actions = mockMvc.perform(get("/adminrest/course/code/{code}", goodCourseCode)
                .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity.code").value(Matchers.containsString(goodCourseCode)));
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        String jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);
    }

    @Test
    public void testGetGoodCourseByBadCode() throws Exception {

        ResultActions actions = mockMvc.perform(get("/adminrest/course/code/{code}", badCourseCode)
                .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isBadRequest());
    }
}
