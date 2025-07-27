package ttl.larku.dao;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Student;
import ttl.larku.sql.SqlScriptBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
//@Sql(scripts = { "/ttl/larku/db/createDB-h2.sql", "/ttl/larku/db/populateDB-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
@Tag("dao")
public class StudentDAOTest extends SqlScriptBase {

	private String name1 = "Bloke";
	private String name2 = "Blokess";
	private String newName = "Different Bloke";
	private Student student1;
	private Student student2;

	@Autowired
//	@Qualifier("jpaStudentDAO")
	private BaseDAO<Student> studentDAO;

	@Autowired
	private ApplicationContext context;

	@BeforeEach
	public void setup() {
		student1 = new Student(name1, "383 939 9393", LocalDate.of(1956, 4, 6), Student.Status.FULL_TIME);
		student2 = new Student(name2, "383 939 54784394", LocalDate.of(1996, 3, 6), Student.Status.PART_TIME);
//        for(String name: context.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }
//        System.out.println(context.getBeanDefinitionCount() + " beans");
	}


	@Test
	public void testFindOne() {
		Student student = studentDAO.findById(1);
		//This will show the n + 1 problem.  Will do 5 selects instead of 1
//		System.out.println(students);
		assertEquals(1, student.getId());
	}

	@Test
	public void testCreate() {

		int newId = studentDAO.insert(student1).getId();

		Student resultStudent = studentDAO.findById(newId);

//		System.out.println(resultStudent);

		assertEquals(newId, resultStudent.getId());
	}

	@Test
	public void testUpdate() {
		int newId = studentDAO.insert(student1).getId();

		Student resultStudent = studentDAO.findById(newId);

		assertEquals(newId, resultStudent.getId());

		resultStudent.setName(newName);
		studentDAO.update(resultStudent);

		resultStudent = studentDAO.findById(resultStudent.getId());
		assertEquals(newName, resultStudent.getName());
	}

	@Test
	public void testDelete() {
		int id1 = studentDAO.insert(student1).getId();

		Student resultStudent = studentDAO.findById(id1);
		assertEquals(resultStudent.getId(), id1);

		studentDAO.delete(resultStudent);

		resultStudent = studentDAO.findById(id1);

		assertEquals(null, resultStudent);
	}

}
