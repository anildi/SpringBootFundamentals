package ttl.larku.dao;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.jpahibernate.JPAStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.sql.SqlScriptBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
//@Sql(scripts = { "/ttl/larku/db/createDB-h2.sql", "/ttl/larku/db/populateDB-h2.sql" }, executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
@Tag("dao")
public class StudentDAOExtrasTest extends SqlScriptBase {

	private String name1 = "Bloke";
	private String name2 = "Blokess";
	private String newName = "Different Bloke";
	private Student student1;
	private Student student2;

	@Autowired
//	@Qualifier("jpaStudentDAO")
	private JPAStudentDAO jpaStudentDAO;

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
	//Turn off Transactions by uncommenting @Transactional
	//If you then try and print the collection, you will
	//get a Lazy Instantiation Exception.
//	@Transactional(propagation = Propagation.REQUIRED)
	public void testGetAll() {
//		List<Student> students = dao.getAll();
		List<Student> students = jpaStudentDAO.getAllForLIE();
		//With @Transactional, this will show the n + 1 problem.
		//Will do 5 selects instead of 1.
		//With no @Transactional, this will throw a LazyInstantiationException.
		students.forEach(s -> System.out.println("Class size for : " + s.getId() + ": " + s.getClasses().size()));
		assertEquals(4, students.size());
	}

	//The correct way to fetch a one to many relationship
	@Test
	public void testGetBigStudents() {
		List<Student> students = jpaStudentDAO.getAllWithCourses();
		//This will show the n + 1 problem.  Will do 5 selects instead of 1
//		System.out.println(students);
		assertEquals(4, students.size());
	}

	@Test
	public void testGetByName() {
		List<Student> students = jpaStudentDAO.getByName("Manoj");
		System.out.println("students.size: " + students.size());
		students.forEach(System.out::println);

		assertEquals(1, students.size());
	}
}
