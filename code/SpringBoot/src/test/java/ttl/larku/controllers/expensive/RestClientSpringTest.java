package ttl.larku.controllers.expensive;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ttl.larku.controllers.rest.RestResultGeneric;
import ttl.larku.controllers.rest.RestResultGeneric.Status;
import ttl.larku.domain.Student;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("expensive")
public class RestClientSpringTest {

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
        rootUrl = "/adminrest/student";
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
        ParameterizedTypeReference<RestResultGeneric<Student>>
                ptr = new ParameterizedTypeReference<RestResultGeneric<Student>>() {
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<RestResultGeneric<Student>> response = rt.exchange("/adminrest/student/{id}",
                HttpMethod.GET, entity, ptr, id);
        assertEquals(200, response.getStatusCodeValue());

        RestResultGeneric<Student> rr = response.getBody();
        RestResultGeneric.Status status = rr.getStatus();
        assertTrue(status == RestResultGeneric.Status.Ok);

        //Still need the mapper to convert the entity Object
        //which should be represented by a map of student properties
        //Student s = mapper.convertValue(rr.getEntity(), Student.class);
        Student s = rr.getEntity();
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
        ParameterizedTypeReference<RestResultGeneric<List<Student>>>
                ptr = new ParameterizedTypeReference<RestResultGeneric<List<Student>>>() {
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<RestResultGeneric<List<Student>>> response = rt.exchange(rootUrl,
                HttpMethod.GET, entity, ptr);

        assertEquals(200, response.getStatusCodeValue());

        RestResultGeneric<List<Student>> rr = response.getBody();
        RestResultGeneric.Status status = rr.getStatus();
        assertTrue(status == RestResultGeneric.Status.Ok);

        //Jackson mechanism to represent a Generic Type List<Student>
//        CollectionType listType = mapper.getTypeFactory()
//                .constructCollectionType(List.class, Student.class);
//        List<Student> students = mapper.convertValue(rr.getEntity(), listType);
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
                .constructParametricType(RestResultGeneric.class, listType);

        //We could unmarshal the whole entity
        RestResultGeneric<List<Student>> rr = mapper.readerFor(type).readValue(root);
        System.out.println("List is " + rr.getEntity());

        List<Student> l1 = rr.getEntity();

        // Create the collection type (since it is a collection of Authors)

        //Or we could step through the json to the entity and just unmarshal that
        JsonNode entity = root.path("entity");
        List<Student> l2 = mapper.readerFor(listType).readValue(entity);
        System.out.println("l2 is " + l2);

        assertTrue(l2.size() > 4);

    }

    /**
     * Here we are using RestResultGeneric having Jackson
     * do all the unmarshalling and give us the correct object
     *
     * @throws IOException
     */
    @Test
    public void testGetAllUsingRestResultGeneric() throws IOException {
        //This is the Spring REST mechanism to create a paramterized type
        ParameterizedTypeReference<RestResultGeneric<List<Student>>>
                ptr = new ParameterizedTypeReference<RestResultGeneric<List<Student>>>() {
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<RestResultGeneric<List<Student>>> response = rt.exchange(rootUrl,
                HttpMethod.GET, entity, ptr);

        assertEquals(200, response.getStatusCodeValue());
        RestResultGeneric<List<Student>> rr = response.getBody();

        Status status = rr.getStatus();
        assertTrue(status == Status.Ok);

        List<Student> l1 = rr.getEntity();
        assertEquals(4, l1.size());
    }

    /**
     * Here we are using RestResultGeneric having Jackson
     * do all the unmarshalling and give us the correct object
     *
     * @throws IOException
     */
    @Test
    public Student testPostOneStudent() throws IOException {
        //This is the Spring REST mechanism to create a parameterized type
        ParameterizedTypeReference<RestResultGeneric<Student>>
                ptr = new ParameterizedTypeReference<RestResultGeneric<Student>>() {
        };

        Student student = new Student("Curly", "339 03 03030", Student.Status.HIBERNATING);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Student> entity = new HttpEntity<>(student, headers);

        ResponseEntity<RestResultGeneric<Student>> response = rt.exchange(rootUrl,
                HttpMethod.POST, entity, ptr);

        assertEquals(201, response.getStatusCodeValue());

        RestResultGeneric<Student> rr = response.getBody();
        RestResultGeneric.Status status = rr.getStatus();
        assertTrue(status == RestResultGeneric.Status.Ok);

        Student newStudent = rr.getEntity();
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
        ParameterizedTypeReference<RestResultGeneric<Void>>
                ptr = new ParameterizedTypeReference<RestResultGeneric<Void>>() {
        };

        Student newStudent = testPostOneStudent();
        String newPhoneNumber = "888 777-333456";
        newStudent.setPhoneNumber(newPhoneNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Student> entity = new HttpEntity<>(newStudent, headers);

        ResponseEntity<RestResultGeneric<Void>> response = rt.exchange(rootUrl,
                HttpMethod.PUT, entity, ptr);

        assertEquals(204, response.getStatusCodeValue());

        Student updatedStudent = getStudentWithId(newStudent.getId());
        assertEquals(newPhoneNumber, updatedStudent.getPhoneNumber());
    }

    @Test
    public void testUpdateOneStudentBadId() throws IOException {

        //This is the Spring REST mechanism to create a parameterized type
        ParameterizedTypeReference<RestResultGeneric<Void>>
                ptr = new ParameterizedTypeReference<RestResultGeneric<Void>>() {
        };

        Student newStudent = testPostOneStudent();
        String newPhoneNumber = "888 777-333456";
        newStudent.setPhoneNumber(newPhoneNumber);
        newStudent.setId(9999);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Student> entity = new HttpEntity<>(newStudent, headers);

        ResponseEntity<RestResultGeneric<Void>> response = rt.exchange(rootUrl,
                HttpMethod.PUT, entity, ptr);

        assertEquals(400, response.getStatusCodeValue());
    }
}
