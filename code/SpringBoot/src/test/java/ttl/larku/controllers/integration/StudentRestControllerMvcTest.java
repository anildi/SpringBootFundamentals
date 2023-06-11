package ttl.larku.controllers.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import ttl.larku.LarkUTestDataConfig;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag("integration")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentRestControllerMvcTest {


    private LarkUTestDataConfig testData = new LarkUTestDataConfig();

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    private final int goodStudentId = 1;
    private final int deleteStudentId = 2;
    private final int badStudentId = 10000;


    @BeforeEach
    public void init() {
        //Note - tricky initialzation/test order issues here.
        //May have to Re-init data if a test make assumptions.
        //about initial state.
        //This code assumes that we are using an InMemoryStudentDAO.
//        studentService.clear();
//        testData.initStudentDAO(studentService.getStudentDAO());
    }

    @Test
    public void testGetOneStudentGoodJson() throws Exception {
        MediaType accept = MediaType.APPLICATION_JSON;
        MediaType contentType = accept;

        MockHttpServletRequestBuilder builder = get("/adminrest/student/{id}", goodStudentId)
            .accept(accept)
            .contentType(contentType);


        ResultActions actions = mockMvc.perform(builder);

        actions = actions.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.entity.name").value(containsString("Manoj")));

        // Get the result and return it
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonString = response.getContentAsString();

        System.out.println("One student good resp = " + jsonString);
    }

    @Test
    public void testGetOneStudentBadId() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/student/{id}", badStudentId)
                        .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcr = actions
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void testAddStudentGood() throws Exception {

        Student student = new Student("Yogita");
        student.setPhoneNumber("202 383-9393");
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(student);

        ResultActions actions = mockMvc.
                perform(post("/adminrest/student/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString));

        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isCreated());
        //Check the location header
        actions = actions.andExpect(header().string("Location", Matchers.containsString("/adminrest/student/5")));

        actions = actions.andExpect(jsonPath("$.entity.name").value(Matchers.containsString("Yogita")));


        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);

    }

    @Test
    public void testUpdateStudentGood() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/student/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonString).path("entity");

        Student student = mapper.treeToValue(node, Student.class);

        student.setPhoneNumber("202 383-9393");
        String updatedJson = mapper.writeValueAsString(student);

        ResultActions putActions = mockMvc.perform(put("/adminrest/student/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isNoContent());

        MvcResult result = actions.andReturn();

        ResultActions postPutActions = mockMvc
                .perform(get("/adminrest/student/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String postJson = postPutActions.andReturn().getResponse().getContentAsString();

        Student postStudent = mapper.treeToValue(mapper.readTree(postJson).path("entity"), Student.class);
        assertEquals("202 383-9393", postStudent.getPhoneNumber());
    }

    @Test
    public void testUpdateStudentBad() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/student/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonString).path("entity");

        Student student = mapper.treeToValue(node, Student.class);
        student.setId(badStudentId);

        student.setPhoneNumber("202 383-9393");
        String updatedJson = mapper.writeValueAsString(student);

        ResultActions putActions = mockMvc.perform(put("/adminrest/student/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isBadRequest());

    }

    @Test
    public void testDeleteStudentGood() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/student/{id}", deleteStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        ResultActions deleteActions = mockMvc.perform(delete("/adminrest/student/{id}", deleteStudentId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        deleteActions = deleteActions.andExpect(status().isNoContent());

        ResultActions postDeleteActions = mockMvc
                .perform(get("/adminrest/student/{id}", deleteStudentId)
                        .accept(MediaType.APPLICATION_JSON));

        postDeleteActions = postDeleteActions.andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteStudentBad() throws Exception {

        ResultActions actions = mockMvc
                .perform(delete("/adminrest/student/{id}", badStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        actions.andExpect(status().isBadRequest());
    }


    @Test
    public void testGetAllStudentsGood() throws Exception {

        ResultActions actions = mockMvc.perform(get("/adminrest/student/").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity", hasSize(greaterThan(0))));
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        String jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);
    }


}
