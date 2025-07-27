package ttl.larku.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.controllers.rest.RestResultWrapper.Status;
import ttl.larku.domain.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"h2", "production", "makemanystudents"})
public class RestClientPagingTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    private RestClient restClient;

    private String baseUrl;
    private String rootUrl;
    private String oneStudentUrl;

    @PostConstruct
    public void init() {
        baseUrl = "http://localhost:" + port;
        rootUrl = "/adminrest/student";
        oneStudentUrl = rootUrl + "/{id}";

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }


    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetAllWithoutSearchingOrPaging() throws IOException {
        //This is the Spring REST mechanism to create a paramterized type
        ParameterizedTypeReference<RestResultWrapper<List<Student>>>
                ptr = new ParameterizedTypeReference<RestResultWrapper<List<Student>>>() {
        };

        ResponseEntity<RestResultWrapper<List<Student>>> response = restClient.get()
                .uri(rootUrl)
                .retrieve()
                .toEntity(ptr);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        RestResultWrapper<List<Student>> rr = response.getBody();
        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        List<Student> students = rr.getEntity();
        System.out.println("l2 is " + students);

        assertEquals(104, students.size());
    }



    @Test
    public void testGetAllWithPaging() throws IOException {
        //This is the Spring REST mechanism to create a paramterized type
//        ParameterizedTypeReference<RestResultWrapper<ResultWithPageData<Student>>>
//              ptr = new ParameterizedTypeReference<>() {
//        };
        ParameterizedTypeReference<RestResultWrapper<List<Student>>>
              ptr = new ParameterizedTypeReference<>() {
        };

        var response = restClient.get()
              .uri(rootUrl + "/search?page={page}", 2)
              .retrieve()
              .toEntity(ptr);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        RestResultWrapper<List<Student>> rr = response.getBody();
        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        List<Student> students = rr.getEntity();
        System.out.println("l2 is " + students);

        assertEquals(10, students.size());

        //Not doing null checks bad
        int page = (int)rr.getProp("page");
        assertEquals(2, page);

        int pageSize = (int)rr.getProp("pageSize");
        assertEquals(10, pageSize);

        int totalElements = (int)rr.getProp("totalElements");
        assertEquals(104, totalElements);

        int totalPages = (int)rr.getProp("totalPages");
        assertEquals(11, totalPages);
    }
}
