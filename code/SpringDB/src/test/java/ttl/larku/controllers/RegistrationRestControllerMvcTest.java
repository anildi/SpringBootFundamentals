package ttl.larku.controllers;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.service.ClassService;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;
import ttl.larku.sql.SqlScriptBase;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@Tag("mvc")
public class RegistrationRestControllerMvcTest extends SqlScriptBase {

   @Autowired
   @Qualifier("studentRepoService")
   private StudentService studentService;

   @Autowired
   private CourseService courseService;

   @Autowired
   private ClassService classService;

   @Autowired
   private WebApplicationContext wac;

   private MockMvc mockMvc;
   private DateTimeFormatter dtFormatter;

   @BeforeEach
   public void init() {
      mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .build();
      dtFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
   }

   @Test
   public void testGetAll() throws Exception {
      ResultActions actions = mockMvc
            .perform(get("/adminrest/class").accept(MediaType.APPLICATION_JSON));

      actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));

      actions = actions.andExpect(status().isOk());

      actions = actions.andExpect(jsonPath("$.entity", hasSize(3)));

      MvcResult mvcr = actions.andReturn();
      String reo = (String) mvcr.getResponse().getContentAsString();
      System.out.println("Reo is " + reo);
   }

   @Test
   public void testGetOne() throws Exception {
      String startDate = "2022-10-10";

      ResultActions actions = mockMvc
            .perform(get("/adminrest/class/1").accept(MediaType.APPLICATION_JSON));

      actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));

      actions = actions.andExpect(status().isOk());

      actions = actions.andExpect(jsonPath("$.entity.startDate")
            .value(startDate));

      MvcResult mvcr = actions.andReturn();
      String reo = (String) mvcr.getResponse().getContentAsString();
      System.out.println("Reo is " + reo);
   }

   @Test
   public void testGetOnePath() throws Exception {
      ResultActions a1 = mockMvc
            .perform(get("/adminrest/course").accept(MediaType.APPLICATION_JSON));
      MvcResult m1 = a1.andReturn();
      String r1 = (String) m1.getResponse().getContentAsString();
//        System.err.println("All Courses: " + r1);

      ResultActions a2 = mockMvc
            .perform(get("/adminrest/class").accept(MediaType.APPLICATION_JSON));
      MvcResult m2 = a2.andReturn();
      String r2 = m2.getResponse().getContentAsString();
      System.err.println("All Classes: " + r2);

      String startDate = "2024-12-10";
      String endDate = "2025-10-10";
      ResultActions actions = mockMvc
            .perform(get("/adminrest/class/code/MATH-101")
                  .param("startDate", startDate)
                  .param("endDate", endDate)
                  .accept(MediaType.APPLICATION_JSON));

      actions = actions.andExpect(status().isOk());

      actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));


      actions = actions.andExpect(jsonPath("$.entity[0].startDate")
            .value(startDate));

      MvcResult mvcr = actions.andReturn();
      String reo = (String) mvcr.getResponse().getContentAsString();
      System.out.println("Reo is " + reo);
   }

   @Test
   public void addOneQueryParams() throws Exception {
      String startDate = "2019-05-05";
      String endDate = "2019-10-05";
      ResultActions actions = mockMvc
            .perform(post("/adminrest/class").param("courseCode", "BKTW-101")
                  .param("startDate", startDate)
                  .param("endDate", endDate)
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON));

      actions = actions.andExpect(status().isCreated());

      actions = actions.andExpect(jsonPath("$.entity.startDate").value(startDate));

      MvcResult mvcr = actions.andReturn();
      String reo = (String) mvcr.getResponse().getContentAsString();
      System.out.println("Reo is " + reo);
   }


   @Test
   public void testRegisterStudentAllGood() throws Exception {
      List<Student> students = studentService.getAllStudents();
      List<ScheduledClass> classes = classService.getAllScheduledClasses();
      int studentId = students.get(0).getId();
      int classId = classes.get(0).getId();

      ResultActions actions = mockMvc
            .perform(post("/adminrest/class/register/{studentId}/{classId}", studentId, classId)
                  .accept(MediaType.APPLICATION_JSON));

      actions = actions.andExpect(status().isOk());

      actions = actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));


      //Get the student and make sure they have to class
      Student s = studentService.getStudent(studentId);
//        List<ScheduledClass> sClasses = s.getClasses();
      Set<ScheduledClass> sClasses = s.getClasses();

      boolean hasIt = sClasses.stream().anyMatch(sc -> sc.getId() == classId);
      assertTrue(hasIt);

      MvcResult mvcr = actions.andReturn();

      String reo = (String) mvcr.getResponse().getContentAsString();
      System.out.println("Reo is " + reo);
   }
}
