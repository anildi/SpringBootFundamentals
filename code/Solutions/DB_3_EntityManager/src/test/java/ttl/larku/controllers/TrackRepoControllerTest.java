package ttl.larku.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Track;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//This annotation makes the test run with the same Spring context
//As our main application 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//To be able to inject MockMvc
@AutoConfigureMockMvc
//Dirtying the context after each method to start with a
//fresh db for each test.  This is *expensive*.
//Better option is to do it on selective methods.
//Also, see the DBLabSolution for a cheaper way to recreate a DB for each TEST.
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class TrackRepoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() { }

    @Test
    public void testGetAll() throws Exception {

        ResultActions actions = mockMvc.perform(get("/trackrepo").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$", hasSize(6)));
        MvcResult result = actions.andReturn();
        System.out.println("result is " + result);
    }

    @Test
    public void testGetOneGood() throws Exception {

        ResultActions actions = mockMvc.perform(get("/trackrepo/{id}", 1).accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
        actions = actions.andExpect(jsonPath("$.title", Matchers.containsString("Shadow")));
        MvcResult result = actions.andReturn();
        System.out.println("result is " + result);
    }

    @Test
    public void testGetWithQueryParam() throws Exception {

        ResultActions actions = mockMvc.perform(get("/trackrepo")
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("title", "April"));


        actions = actions.andExpect(status().isOk());

        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
        actions = actions.andExpect(jsonPath("$", hasSize(1)));
        actions = actions.andExpect(jsonPath("$[0].artist", Matchers.equalTo("Jim Hall and Ron Carter")));
        MvcResult result = actions.andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println("result is " + jsonResult);
    }

    @Test
    public void testGetWithQueryByExample() throws Exception {

        String jsonExample = "{\"artist\": \"Herb\"}";
        ResultActions actions = mockMvc.perform(post("/trackrepo/query")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonExample));


        actions = actions.andExpect(status().isOk());

        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
        actions = actions.andExpect(jsonPath("$", hasSize(1)));
        actions = actions.andExpect(jsonPath("$[0].title", Matchers.containsString("Leave It to Me")));
        MvcResult result = actions.andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println("result is " + jsonResult);
    }

    @Test
    public void testGetWithQueryByExampleForMultipleProps() throws Exception {

        String jsonExample = "{\"artist\": \"Herb\", \"album\": \"Moonlight\" }";
        ResultActions actions = mockMvc.perform(post("/trackrepo/query")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonExample));


        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$", hasSize(2)));
        MvcResult result = actions.andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println("result is " + jsonResult);
    }

    @Test
    public void testGetOneBad() throws Exception {

        ResultActions actions = mockMvc.perform(get("/trackrepo/{id}", 10000000).accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isBadRequest());
        actions.andReturn();
    }

    @Test
    public void testUpdate() throws Exception {
        ResultActions actions = mockMvc.perform(get("/trackrepo/{id}", 1).accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
        actions = actions.andExpect(jsonPath("$.title", Matchers.containsString("Shadow")));
        MvcResult mvcr = actions.andReturn();
        String origString = mvcr.getResponse().getContentAsString();
        System.out.println("orig String is " + origString);

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        Track track = mapper.readValue(origString, Track.class);

        String changedTitle = "Only Lightness";

        track.setTitle(changedTitle);

        String newString = mapper.writeValueAsString(track);

        ResultActions putActions = mockMvc.perform(put("/trackrepo/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newString));
        putActions = putActions.andExpect(status().isNoContent());
        mvcr = putActions.andReturn();

        actions = mockMvc.perform(get("/trackrepo/{id}", 1).accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
        actions = actions.andExpect(jsonPath("$.title", Matchers.containsString(changedTitle)));
        actions.andReturn();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDelete() throws Exception {
        ResultActions actions = mockMvc.perform(delete("/trackrepo/{id}", 1)
                .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isNoContent());
        actions.andReturn();

        actions = mockMvc.perform(get("/trackrepo/{id}", 1)
                .accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isBadRequest());
        actions.andReturn();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testPost() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Track track = Track.album("Silent Days").artist("Josh Streenberg").title("Silent Number One").build();
        ResultActions actions = mockMvc.perform(
                post("/trackrepo")
                        .content(mapper.writeValueAsString(track))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );


        actions = actions.andExpect(status().isCreated());

        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
        actions = actions.andExpect(jsonPath("$.title",
                Matchers.containsString("Silent Number One")));

        MvcResult mvcr = actions.andReturn();
        String origString = mvcr.getResponse().getContentAsString();
        System.out.println("orig String is " + origString);

        Track newTrack = mapper.readValue(origString, Track.class);
        System.out.println("New Track: " + newTrack);

        actions = mockMvc.perform(get("/trackrepo").accept(MediaType.APPLICATION_JSON));

        actions = actions.andExpect(status().isOk());

        actions = actions.andExpect(jsonPath("$", hasSize(7)));
    }
    //Etc. Etc.
}
