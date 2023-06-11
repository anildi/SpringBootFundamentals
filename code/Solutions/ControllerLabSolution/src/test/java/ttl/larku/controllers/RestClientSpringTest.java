package ttl.larku.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ttl.larku.domain.Track;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestClientSpringTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rt;
    @Autowired
    private ObjectMapper mapper;

    // GET with url parameters
    private String rootUrl;
    private String oneTrackUrl;

    @BeforeEach
    public void setup() {
        rootUrl = "http://localhost:" + port + "/track";
        oneTrackUrl = rootUrl + "/{id}";
    }

    @Test
    public void testGetOneTrackUsingAutoUnmarshalling() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Track> response = rt.exchange(oneTrackUrl,
                HttpMethod.GET, entity, Track.class, 2);
        assertEquals(200, response.getStatusCodeValue());

        Track track = response.getBody();
        System.out.println("Track = " + track);

        assertTrue(track.getTitle().contains("April"));
    }

    @Test
    public void testGetOneTrackWithManualJson() throws IOException {
        ResponseEntity<String> response = rt.getForEntity(oneTrackUrl, String.class, 2);
        assertEquals(200, response.getStatusCodeValue());

        String raw = response.getBody();
        JsonNode root = mapper.readTree(raw);

        Track track = mapper.treeToValue(root, Track.class);
        System.out.println("Track is " + track);
        assertTrue(track.getTitle().contains("April"));
    }

    @Test
    public void testGetOneStudentBadId() throws IOException {
        ResponseEntity<String> response = rt.getForEntity(oneTrackUrl, String.class, 10000);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testGetAllUsingAutoUnmarshalling() throws IOException {

        //This is the Spring REST mechanism to create a paramterized type
        ParameterizedTypeReference<List<Track>>
                ptr = new ParameterizedTypeReference<List<Track>>() {
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Track>> response = rt.exchange(rootUrl,
                HttpMethod.GET, entity, ptr);

        assertEquals(200, response.getStatusCodeValue());

        List<Track> l2 = response.getBody();
        System.out.println("l2 is " + l2);

        assertEquals(6, l2.size());
    }

    /**
     * Here we test getting the response as Json and then
     * picking our way through it using the ObjectMapper
     *
     * @throws IOException
     */
    @Test
    public void testGetAllWithJson() throws IOException {
        ResponseEntity<String> response = rt.getForEntity(rootUrl, String.class);
        assertEquals(200, response.getStatusCodeValue());

        String raw = response.getBody();
        System.out.println("raw is " + raw);
        JsonNode root = mapper.readTree(raw);

        //This is to represent a List<Track>
        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, Track.class);

        List<Track> l2 = mapper.readerFor(listType).readValue(root);
        System.out.println("List is " + l2);

        assertEquals(6, l2.size());

    }

    /**
     * Here we are having Jackson do all the unmarshalling
     * for a List<Track>
     *
     * @throws IOException
     */
    @Test
    public void testGetAllUsingAutoMarshalling() throws IOException {
        //This is the Spring REST mechanism to create a paramterized type
        ParameterizedTypeReference<List<Track>>
                ptr = new ParameterizedTypeReference<List<Track>>() {
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Track>> response = rt.exchange(rootUrl,
                HttpMethod.GET, entity, ptr);

        assertEquals(200, response.getStatusCodeValue());
        List<Track> l1 = response.getBody();

        assertEquals(6, l1.size());
    }
}
