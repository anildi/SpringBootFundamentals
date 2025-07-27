package ttl.larku.controllers.expensive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.controllers.rest.RestResult;
import ttl.larku.domain.Student;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ttl.larku.controllers.rest.RestResult.Status;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("expensive")
public class RestClientSpringTest_RR {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rt;
    @Autowired
    private ObjectMapper mapper;

    // GET with url parameters
    private String rootUrl;
    private String oneStudentUrl;

    @BeforeEach
    public void setup() {
        //rootUrl = "http://localhost:" + port + "/adminrest/student";
        rootUrl = "/adminrest/student_rr";
        oneStudentUrl = rootUrl + "/{id}";
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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<RestResult> response = rt.exchange("/adminrest/student/{id}",
                HttpMethod.GET, entity, RestResult.class, id);
        assertEquals(200, response.getStatusCodeValue());

        RestResult rr = response.getBody();
        RestResult.Status status = rr.getStatus();
        assertTrue(status == RestResult.Status.Ok);

        //Still need the mapper to convert the entity Object
        //which should be represented by a map of student properties
        //Student s = mapper.convertValue(rr.getEntity(), Student.class);
        Student s = mapper.convertValue(rr.getEntity(), Student.class);
//        Student s = rr.getEntity(Student.class);
        System.out.println("Student is " + s);

        return s;
    }

    @Test
    public void testGetOneStudentWithManualJson() throws IOException {
        ResponseEntity<String> response = rt.getForEntity(oneStudentUrl, String.class, 2);
        assertEquals(200, response.getStatusCodeValue());

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
        ResponseEntity<String> response = rt.getForEntity(oneStudentUrl, String.class, 10000);
        assertEquals(400, response.getStatusCodeValue());

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<RestResult> response = rt.exchange(rootUrl,
                HttpMethod.GET, entity, RestResult.class);

        assertEquals(200, response.getStatusCodeValue());

        RestResult rr = response.getBody();
        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        List<Student> students = mapper.convertValue(rr.getEntity(), new TypeReference<List<Student>>() { });
        System.out.println("l2 is " + students);

    }

    /**
     * Here we test getting the response as Json and then
     * picking our way through it using the ObjectMapper
     *
     * @throws IOException
     */
    @Test
    public void testGetAllWithJsonUsingRestResult() throws IOException {
        ResponseEntity<String> response = rt.getForEntity(rootUrl, String.class);
        assertEquals(200, response.getStatusCodeValue());
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
        RestResult rr = mapper.readerFor(RestResult.class).readValue(root);
        System.out.println("List is " + rr.getEntity());

        List<Student> students = mapper.convertValue(rr.getEntity(), new TypeReference<List<Student>>() { });

        // Create the collection type (since it is a collection of Authors)

        //Or we could step through the json to the entity and just unmarshal that
        JsonNode entity = root.path("entity");
        List<Student> l2 = mapper.readerFor(listType).readValue(entity);
        System.out.println("l2 is " + l2);

        assertTrue(l2.size() > 0);

    }

    /**
     * Here we are using RestResultGeneric having Jackson
     * do all the unmarshalling and give us the correct object
     *
     * @throws IOException
     */
    @Test
    public void testGetAllUsingRestResult() throws IOException {
        //This is the Spring REST mechanism to create a paramterized type
        ParameterizedTypeReference<RestResultWrapper<List<Student>>>
                ptr = new ParameterizedTypeReference<RestResultWrapper<List<Student>>>() {
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<RestResult> response = rt.exchange(rootUrl,
                HttpMethod.GET, entity, RestResult.class);

        assertEquals(200, response.getStatusCodeValue());
        RestResult rr = response.getBody();

        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        List<Student> students = mapper.convertValue(rr.getEntity(), new TypeReference<List<Student>>() { });
        assertTrue(students.size() > 0);

        Student s = students.get(0);
    }

    /**
     * Here we are using RestResult and having Jackson
     * do all the unmarshalling and give us the correct object
     *
     * @throws IOException
     */
    @Test
    public void testPostOneStudent() throws IOException {
        postOneStudent();
    }

    private Student postOneStudent() throws IOException {
        //This is the Spring REST mechanism to create a parameterized type
        ParameterizedTypeReference<RestResultWrapper<Student>>
                ptr = new ParameterizedTypeReference<RestResultWrapper<Student>>() {
        };

        Student student = new Student("Curly", "339 03 03030", Student.Status.HIBERNATING);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Student> entity = new HttpEntity<>(student, headers);

        ResponseEntity<RestResult> response = rt.exchange(rootUrl,
                HttpMethod.POST, entity, RestResult.class);

        assertEquals(201, response.getStatusCodeValue());

        RestResult rr = response.getBody();
        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        Student newStudent = mapper.convertValue(rr.getEntity(), Student.class);
        assertTrue(newStudent.getId() > 4);
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Student> entity = new HttpEntity<>(newStudent, headers);

        ResponseEntity<RestResult> response = rt.exchange(rootUrl,
                HttpMethod.PUT, entity, RestResult.class);

        assertEquals(204, response.getStatusCodeValue());

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Student> entity = new HttpEntity<>(newStudent, headers);

        ResponseEntity<RestResult> response = rt.exchange(rootUrl,
                HttpMethod.PUT, entity, RestResult.class);

        assertEquals(400, response.getStatusCodeValue());
    }
}
