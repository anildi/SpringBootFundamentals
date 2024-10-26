package ttl.larku.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;
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
    private ObjectMapper mapper;

    private RestClient restClient;

    private String baseUrl;
    private String rootUrl;
    private String oneTrackUrl;

    @PostConstruct
    public void init() {
        baseUrl = "http://localhost:" + port;
        rootUrl = "/track";
        oneTrackUrl = rootUrl + "/{id}";

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Test
    public void testGetOneTrackUsingAutoUnmarshalling() throws IOException {


        ResponseEntity<Track> response = restClient.get()
                .uri(oneTrackUrl, 2)
                .retrieve()
                .toEntity(Track.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Track track = response.getBody();
        System.out.println("Track = " + track);

        assertTrue(track.getTitle().contains("April"));
    }

    @Test
    public void testGetOneTrackWithManualJson() throws IOException {
        ResponseEntity<String> response = restClient.get()
                .uri(oneTrackUrl, 2)
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        String raw = response.getBody();
        JsonNode root = mapper.readTree(raw);

        Track track = mapper.treeToValue(root, Track.class);
        System.out.println("Track is " + track);
        assertTrue(track.getTitle().contains("April"));
    }

    @Test
    public void testGetOneStudentBadId() throws IOException {
        ResponseEntity<?> response = restClient.get()
                .uri(oneTrackUrl, 1000)
                .retrieve()
                .onStatus(st -> st == HttpStatus.BAD_REQUEST, (req, resp) -> {})
                .toBodilessEntity();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetAllUsingAutoUnmarshalling() throws IOException {

        //This is the Spring REST mechanism to create a paramterized type
        ParameterizedTypeReference<List<Track>>
                ptr = new ParameterizedTypeReference<List<Track>>() {
        };

        ResponseEntity<List<Track>> response = restClient.get()
                .uri(rootUrl)
                .retrieve()
                .toEntity(ptr);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Track> l2 = response.getBody();
        System.out.println("l2 is " + l2);
    }

    /**
     * Here we test getting the response as Json and then
     * picking our way through it using the ObjectMapper
     *
     * @throws IOException
     */
    @Test
    public void testGetAllWithJson() throws IOException {
        ResponseEntity<String> response = restClient.get()
                .uri(rootUrl)
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        String raw = response.getBody();
        System.out.println("raw is " + raw);
        JsonNode root = mapper.readTree(raw);

        //This is to represent a List<Track>
        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, Track.class);

        List<Track> l2 = mapper.readerFor(listType).readValue(root);
        System.out.println("List is " + l2);

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


        ResponseEntity<List<Track>> response = restClient.get()
                .uri(rootUrl)
                .retrieve()
                .toEntity(ptr);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Track> l1 = response.getBody();

        assertEquals(6, l1.size());
    }
}
