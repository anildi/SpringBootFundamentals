package ttl.larku.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Student;
import ttl.larku.sql.SqlScriptBase;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
//Populate your DB.  From Most Expensive to least expensive

//This will make recreate the context after every test.
//In conjunction with appropriate 'schema[-XXX].sql' and 'data[-XXX].sql' files
//it will also drop and recreate the DB before each test.
//@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)

//Or you can just re-run the sql files before each test method
//@Sql(scripts = { "/schema-h2.sql", "/data-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)

//@Transactional works in reverse for Tests.
//It will *ROLL BACK* the transaction after
//each test, so the database will actually stay the
//same for the next test.  Which is probably the
//cheapest way to go back to the initial state.
@Transactional
@Tag("mvc")
public class StudentRepoRestControllerMvcTest extends SqlScriptBase {

    @Autowired
    private MockMvc mockMvc;

    private final int goodStudentId = 1;
    private final int badStudentId = 10000;

    @BeforeEach
    public void init() {
    }

    @Test
    public void testGetOneStudentGoodJson() throws Exception {
        MediaType accept = MediaType.APPLICATION_JSON;
        MediaType contentType = accept;

        MockHttpServletRequestBuilder builder = get("/adminrest/repostudent/{id}", goodStudentId)
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
                .perform(get("/adminrest/repostudent/{id}", badStudentId)
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

        ResultActions actions = mockMvc.perform(post("/adminrest/repostudent/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonString));

        actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isCreated());

        actions = actions.andExpect(jsonPath("$.entity.name")
                .value(Matchers.containsString("Yogita")));

        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);

    }

    @Test
    public void testUpdateStudentGood() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/repostudent/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonString).path("entity");

        Student student = mapper.treeToValue(node, Student.class);

        student.setPhoneNumber("202 383-9393");
        String updatedJson = mapper.writeValueAsString(student);

        ResultActions putActions = mockMvc.perform(put("/adminrest/repostudent/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isNoContent());

        MvcResult result = actions.andReturn();

        ResultActions postPutActions = mockMvc
                .perform(get("/adminrest/repostudent/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String postJson = postPutActions.andReturn().getResponse().getContentAsString();

        Student postStudent = mapper.treeToValue(mapper.readTree(postJson).path("entity"), Student.class);
        assertEquals("202 383-9393", postStudent.getPhoneNumber());
    }

    @Test
    public void testUpdateStudentBad() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/repostudent/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonString).path("entity");

        Student student = mapper.treeToValue(node, Student.class);
        student.setId(badStudentId);

        student.setPhoneNumber("202 383-9393");
        String updatedJson = mapper.writeValueAsString(student);

        ResultActions putActions = mockMvc.perform(put("/adminrest/repostudent/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson));

        putActions = putActions.andExpect(status().isBadRequest());

    }

    @Test
    public void testDeleteStudentGood() throws Exception {

        ResultActions actions = mockMvc
                .perform(get("/adminrest/repostudent/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        String jsonString = actions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        ResultActions deleteActions = mockMvc.perform(delete("/adminrest/repostudent/{id}", goodStudentId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        deleteActions = deleteActions.andExpect(status().isNoContent());

        ResultActions postDeleteActions = mockMvc
                .perform(get("/adminrest/repostudent/{id}", goodStudentId)
                        .accept(MediaType.APPLICATION_JSON));

        postDeleteActions = postDeleteActions.andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteStudentBad() throws Exception {

        ResultActions actions = mockMvc
                .perform(delete("/adminrest/repostudent/{id}", badStudentId)
                        .accept(MediaType.APPLICATION_JSON));
        actions.andExpect(status().isBadRequest());
    }


    @Test
    public void testGetAllStudentsGood() throws Exception {

        ResultActions actions = mockMvc.perform(get("/adminrest/repostudent/").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$.entity", hasSize(4)));
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();

        String jsonString = response.getContentAsString();
        System.out.println("resp = " + jsonString);
    }


    private String makeCall(MockHttpServletRequestBuilder builder, MediaType accept, MediaType contentType,
                            List<ResultMatcher> matchers, String content) throws Exception {
        if (accept != null) {
            builder = builder.accept(accept);
        }
        if (contentType != null) {
            builder = builder.contentType(contentType);
        }
        if (content != null) {
            builder = builder.content(content);
        }

        ResultActions actions = mockMvc.perform(builder);

        for (ResultMatcher m : matchers) {
            actions = actions.andExpect(m);
        }

        // Get the result and return it
        MvcResult result = actions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonString = response.getContentAsString();

        return jsonString;
    }

}
