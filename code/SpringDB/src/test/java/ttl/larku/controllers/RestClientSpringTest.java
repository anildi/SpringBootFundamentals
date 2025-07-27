package ttl.larku.controllers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.controllers.rest.RestResultWrapper.Status;
import ttl.larku.domain.Student;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("expensive")
public class RestClientSpringTest {

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
    public void testGetOneStudentUsingAutoUnmarshalling() throws IOException {
        Student s = getStudentWithId(2);
        assertTrue(s.getName().contains("Ana"));
//        return s;
    }

    public Student getStudentWithId(int id) throws IOException {
        //This is the Spring REST mechanism to create a paramterized type
        ParameterizedTypeReference<RestResultWrapper<Student>>
                ptr = new ParameterizedTypeReference<RestResultWrapper<Student>>() {
        };

        ResponseEntity<RestResultWrapper<Student>> response = restClient.get()
                .uri("/adminrest/student/{id}", id)
                .retrieve()
                .toEntity(ptr);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        RestResultWrapper<Student> rr = response.getBody();
        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        Student s = rr.getEntity();
        System.out.println("Student is " + s);

        return s;
    }

    @Test
    public void testGetOneStudentWithManualJson() throws IOException {
        ResponseEntity<String> response = restClient.get()
                .uri(oneStudentUrl, 2)
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        String raw = response.getBody();
        JsonNode root = mapper.readTree(raw);
        Status status = Status.valueOf(root.path("status").asText());
        assertTrue(status == Status.Ok);

        JsonNode entity = root.path("entity");
        Student s = mapper.treeToValue(entity, Student.class);
        System.out.println("Student is " + s);
        assertTrue(s.getName().contains("Ana"));
    }

    @Test
    public void testGetOneStudentBadId() throws IOException {
        ResponseEntity<String> response = restClient.get()
                .uri(oneStudentUrl, 1000)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    //Do Nothing because we are going to deal with it with
                    //An assertion
//                    System.out.println("Got 4xxxClientError");
                })
                .toEntity(String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        String raw = response.getBody();
        JsonNode root = mapper.readTree(raw);
        Status status = Status.valueOf(root.path("status").asText());
        assertTrue(status == Status.Error);

        JsonNode errors = root.path("errors");
        assertTrue(errors != null);

        StringBuffer sb = new StringBuffer(100);
        errors.forEach(node -> {
            sb.append(node.asText());
        });
        String reo = sb.toString();
        System.out.println("Error is " + reo);
        assertTrue(reo.contains("not found"));
    }

    @Test
    public void testGetAllUsingAutoUnmarshalling() throws IOException {
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

        assertEquals(4, students.size());
    }

    /**
     * Here we test getting the response as Json and then
     * picking our way through it using the ObjectMapper
     * We use RestResultGeneric here
     *
     * @throws IOException
     */
    @Test
    public void testGetAllWithJsonUsingRestResultGeneric() throws IOException {

        ResponseEntity<String> response = restClient.get()
                .uri(rootUrl)
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String raw = response.getBody();
        JsonNode root = mapper.readTree(raw);

        Status status = Status.valueOf(root.path("status").asText());
        assertTrue(status == Status.Ok);

        //Have to make this complicated mapping to get
        //ResutResultGeneric<List<Student>>
        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, Student.class);
        JavaType type = mapper.getTypeFactory()
                .constructParametricType(RestResultWrapper.class, listType);

        //We could unmarshal the whole entity
        RestResultWrapper<List<Student>> rr = mapper.readerFor(type).readValue(root);
        System.out.println("List is " + rr.getEntity());

        List<Student> l1 = rr.getEntity();

        // Create the collection type (since it is a collection of Authors)

        //Or we could step through the json to the entity and just unmarshal that
        JsonNode entity = root.path("entity");
        List<Student> l2 = mapper.readerFor(listType).readValue(entity);
        System.out.println("l2 is " + l2);
    }

    /**
     * Here we are using RestResultGeneric having Jackson
     * do all the unmarshalling and give us the correct object
     *
     * @throws IOException
     */
    @Test
    public void testPostOneStudent() throws IOException {
       postOneStudent();
    }

    public Student postOneStudent() throws IOException {
        //This is the Spring REST mechanism to create a parameterized type
        ParameterizedTypeReference<RestResultWrapper<Student>>
                ptr = new ParameterizedTypeReference<RestResultWrapper<Student>>() {
        };

        Student student = new Student("Curly", "339 03 03030", LocalDate.of(2000, 10, 10), Student.Status.HIBERNATING);


        ResponseEntity<RestResultWrapper<Student>> response = restClient.post()
                .uri(rootUrl)
                .body(student)
                .retrieve()
                .toEntity(ptr);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        RestResultWrapper<Student> rr = response.getBody();
        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        Student newStudent = rr.getEntity();
        return newStudent;
    }

    /**
     * Here we are using RestResultGeneric having Jackson
     * do all the unmarshalling and give us the correct object
     *
     * @throws IOException
     */
    @Test
    public void testUpdateOneStudent() throws IOException {

        //This is the Spring REST mechanism to create a parameterized type
        ParameterizedTypeReference<RestResultWrapper<Void>>
                ptr = new ParameterizedTypeReference<RestResultWrapper<Void>>() {
        };

        Student newStudent = postOneStudent();
        String newPhoneNumber = "888 777-333456";
        newStudent.setPhoneNumber(newPhoneNumber);

        ResponseEntity<RestResultWrapper<Void>> response = restClient.put()
                .uri(rootUrl)
                .body(newStudent)
                .retrieve()
                .toEntity(ptr);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Student updatedStudent = getStudentWithId(newStudent.getId());
        assertEquals(newPhoneNumber, updatedStudent.getPhoneNumber());
    }

    @Test
    public void testUpdateOneStudentBadId() throws IOException {

        //This is the Spring REST mechanism to create a parameterized type
        ParameterizedTypeReference<RestResultWrapper<Void>>
                ptr = new ParameterizedTypeReference<RestResultWrapper<Void>>() {
        };

        Student newStudent = postOneStudent();
        String newPhoneNumber = "888 777-333456";
        newStudent.setPhoneNumber(newPhoneNumber);
        newStudent.setId(9999);

        ResponseEntity<RestResultWrapper<Void>> response = restClient.put()
                .uri(rootUrl)
                .body(newStudent)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    //Do Nothing
                })
                .toEntity(ptr);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
