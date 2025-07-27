package ttl.larku.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ttl.larku.TrackerTestDataConfig;
import ttl.larku.domain.Track;
import ttl.larku.service.TrackService;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TrackControllerUnitTest {

    @InjectMocks
    private TrackController controller;

    @Mock
    private TrackService trackService;

    @Mock
    private UriCreator uriCreator;

    List<Track> allTracks = new TrackerTestDataConfig().tracks();

    @BeforeEach
    public void init() { }

    @Test
    public void testGetAllWithNoParams() throws Exception {

        Map<String, String> queryStrings = Map.of();
        Mockito.when(trackService.getAllTracks()).thenReturn(allTracks);

        ResponseEntity<?> response = controller.getAllTracks(queryStrings);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Track> tracks = (List)response.getBody();
        assertEquals(6, tracks.size());

        Mockito.verify(trackService).getAllTracks();
    }

    @Test
    public void testGetAllWithSomeParams() throws Exception {

        Map<String, String> queryStrings = Map.of("title", "April");
        Mockito.when(trackService.getTracksBy(queryStrings)).thenReturn(List.of(allTracks.get(1)));

        ResponseEntity<?> response = controller.getAllTracks(queryStrings);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<Track> tracks = (List)response.getBody();
        assertEquals(1, tracks.size());

        Mockito.verify(trackService).getTracksBy(queryStrings);
        Mockito.verify(trackService, Mockito.never()).getAllTracks();
    }

    @Test
    public void testGetAllTracksWithBody() throws Exception {

        Track example = Track.title("April").build();
        Mockito.when(trackService.getTracksBy(example)).thenReturn(List.of(allTracks.get(1)));

        ResponseEntity<?> response = controller.getAllTracksWithBody(example);
        assertEquals(200, response.getStatusCodeValue());

        Mockito.verify(trackService).getTracksBy(example);
    }

    @Test
    public void testGetOneGood() throws Exception {

        int id = 1;
        Mockito.when(trackService.getTrack(id)).thenReturn(allTracks.get(0));

        ResponseEntity<?> response = controller.getTrack(id);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings({"unchecked", "rawtypes"})
        Track tracks = (Track)response.getBody();

        Mockito.verify(trackService).getTrack(id);
    }

    @Test
    public void testGetOneBad() throws Exception {

        int id = 10000;
        Mockito.when(trackService.getTrack(id)).thenReturn(null);

        ResponseEntity<?> response = controller.getTrack(id);
        assertEquals(400, response.getStatusCodeValue());
        @SuppressWarnings({"unchecked", "rawtypes"})
        Track tracks = (Track)response.getBody();

        Mockito.verify(trackService).getTrack(id);
    }

    @Test
    public void testAddTrack() throws Exception {

        int newId = 10;
        Track newTrack = Track.title("Blue Serenade").artist("The Serenaders")
                .album("Sing at the Moon").duration("06:15").date("1965-01-02").build();
        newTrack.setId(newId);
        Mockito.when(trackService.createTrack(newTrack)).thenReturn(newTrack);
        String newLoc = "http://localhost:8080/track/10";
        URI newResource = new URI(newLoc);
        Mockito.when(uriCreator.getUriFor(newId)).thenReturn(newResource);

        ResponseEntity<?> response = controller.addTrack(newTrack);
        assertEquals(201, response.getStatusCodeValue());
        String locHeader = response.getHeaders().get("Location").get(0);
        assertEquals(newLoc, locHeader);

        Mockito.verify(trackService).createTrack(newTrack);
        Mockito.verify(uriCreator).getUriFor(newId);
    }

    @Test
    public void testDeleteGood() throws Exception {

        int id = 1;
        Mockito.when(trackService.deleteTrack(id)).thenReturn(true);

        ResponseEntity<?> response = controller.deleteTrack(id);
        assertEquals(204, response.getStatusCodeValue());

        Mockito.verify(trackService).deleteTrack(id);
    }

    @Test
    public void testDeleteBad() throws Exception {
        int id = 1000;
        Mockito.when(trackService.deleteTrack(id)).thenReturn(false);

        ResponseEntity<?> response = controller.deleteTrack(id);
        assertEquals(400, response.getStatusCodeValue());

        Mockito.verify(trackService).deleteTrack(id);
    }

    @Test
    public void testUpdateGood() throws Exception {

        int id = 1;
        Track track = allTracks.get(0);
        Mockito.when(trackService.updateTrack(track)).thenReturn(true);

        ResponseEntity<?> response = controller.updateTrack(track);
        assertEquals(204, response.getStatusCodeValue());

        Mockito.verify(trackService).updateTrack(track);
    }

    @Test
    public void testUpdateBad() throws Exception {
        int id = 1000;
        Track track = allTracks.get(0);
        track.setId(id);
        Mockito.when(trackService.updateTrack(track)).thenReturn(false);

        ResponseEntity<?> response = controller.updateTrack(track);
        assertEquals(400, response.getStatusCodeValue());

        Mockito.verify(trackService).updateTrack(track);
    }

//
//    @Test
//    public void testGetWithQueryParam() throws Exception {
//
//        ResultActions actions = mockMvc.perform(get("/track")
//                .accept(MediaType.APPLICATION_JSON)
//                .queryParam("title", "April"));
//
//
//        actions = actions.andExpect(status().isOk());
//
//        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
//        actions = actions.andExpect(jsonPath("$", hasSize(1)));
//        actions = actions.andExpect(jsonPath("$[0].artist", Matchers.equalTo("Jim Hall and Ron Carter")));
//        MvcResult result = actions.andReturn();
//        String jsonResult = result.getResponse().getContentAsString();
//        System.out.println("result is " + jsonResult);
//    }
//
//    @Test
//    public void testGetWithQueryByExample() throws Exception {
//
//        String jsonExample = "{\"artist\": \"Herb\"}";
//        ResultActions actions = mockMvc.perform(post("/track/query")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonExample));
//
//
//        actions = actions.andExpect(status().isOk());
//
//        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
//        actions = actions.andExpect(jsonPath("$", hasSize(1)));
//        actions = actions.andExpect(jsonPath("$[0].title", Matchers.equalTo("Leave It to Me")));
//        MvcResult result = actions.andReturn();
//        String jsonResult = result.getResponse().getContentAsString();
//        System.out.println("result is " + jsonResult);
//    }
//
//    @Test
//    public void testGetWithQueryByExampleForMultipleProps() throws Exception {
//
//        String jsonExample = "{\"artist\": \"Herb\", \"album\": \"Moonlight\" }";
//        ResultActions actions = mockMvc.perform(post("/track/query")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonExample));
//
//
//        actions = actions.andExpect(status().isOk());
//
//        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
//        actions = actions.andExpect(jsonPath("$", hasSize(2)));
//        MvcResult result = actions.andReturn();
//        String jsonResult = result.getResponse().getContentAsString();
//        System.out.println("result is " + jsonResult);
//    }
//
//    @Test
//    public void testGetOneBad() throws Exception {
//
//        ResultActions actions = mockMvc.perform(get("/track/{id}", 10000000).accept(MediaType.APPLICATION_JSON));
//
//        actions = actions.andExpect(status().isBadRequest());
//        actions.andReturn();
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//        ResultActions actions = mockMvc.perform(get("/track/{id}", 1).accept(MediaType.APPLICATION_JSON));
//
//        actions = actions.andExpect(status().isOk());
//
//        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
//        actions = actions.andExpect(jsonPath("$.title", Matchers.containsString("Shadow")));
//        MvcResult mvcr = actions.andReturn();
//        String origString = mvcr.getResponse().getContentAsString();
//        System.out.println("orig String is " + origString);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.findAndRegisterModules();
//        Track track = mapper.readValue(origString, Track.class);
//
//        String changedTitle = "Only Lightness";
//
//        track.setTitle(changedTitle);
//
//        String newString = mapper.writeValueAsString(track);
//
//        ResultActions putActions = mockMvc.perform(put("/track/")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(newString));
//        putActions = putActions.andExpect(status().isNoContent());
//        mvcr = putActions.andReturn();
//
//        actions = mockMvc.perform(get("/track/{id}", 1).accept(MediaType.APPLICATION_JSON));
//
//        actions = actions.andExpect(status().isOk());
//
//        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
//        actions = actions.andExpect(jsonPath("$.title", Matchers.containsString(changedTitle)));
//        actions.andReturn();
//    }
//
//    @Test
//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
//    public void testDelete() throws Exception {
//        ResultActions actions = mockMvc.perform(delete("/track/{id}", 1)
//                .accept(MediaType.APPLICATION_JSON));
//
//        actions = actions.andExpect(status().isNoContent());
//        actions.andReturn();
//
//        actions = mockMvc.perform(get("/track/{id}", 1)
//                .accept(MediaType.APPLICATION_JSON));
//
//        actions = actions.andExpect(status().isBadRequest());
//        actions.andReturn();
//    }
//
//    @Test
//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
//    public void testPost() throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        Track track = Track.album("Silent Days").artist("Josh Streenberg").title("Silent Number One").build();
//        ResultActions actions = mockMvc.perform(
//                post("/track")
//                        .content(mapper.writeValueAsString(track))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//        );
//
//
//        actions = actions.andExpect(status().isCreated());
//
//        //actions = actions.andExpect(jsonPath("$.title", hasSize(6)));
//        actions = actions.andExpect(jsonPath("$.title",
//                Matchers.containsString("Silent Number One")));
//
//        MvcResult mvcr = actions.andReturn();
//        String origString = mvcr.getResponse().getContentAsString();
//        System.out.println("orig String is " + origString);
//
//        Track newTrack = mapper.readValue(origString, Track.class);
//        System.out.println("New Track: " + newTrack);
//
//        actions = mockMvc.perform(get("/track").accept(MediaType.APPLICATION_JSON));
//
//        actions = actions.andExpect(status().isOk());
//
//        actions = actions.andExpect(jsonPath("$", hasSize(7)));
//    }
    //Etc. Etc.
}
