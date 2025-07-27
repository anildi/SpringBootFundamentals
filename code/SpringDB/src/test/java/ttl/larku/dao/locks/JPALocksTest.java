package ttl.larku.dao.locks;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.domain.StudentVersioned;
import ttl.larku.service.StudentDaoService;
import ttl.larku.sql.SqlScriptBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@Sql(scripts = {"/schema-h2.sql", "/data-h2.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(scripts = { "/ttl/larku/db/createVersionedDB-h2.sql",
//		"/ttl/larku/db/populateVersionedDB-h2.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Transactional
//@Rollback(false)
@Disabled
@Tag("dao")
public class JPALocksTest extends SqlScriptBase {

	@PersistenceUnit
	private EntityManagerFactory emf;

	@PersistenceContext
	private EntityManager entityManager;

	@Resource(name = "studentDaoService")
	private StudentDaoService studentService;

	@BeforeEach
	public void beforeEach() throws SQLException {
		this.runSqlScriptsOnce();
	}


	/**
	 * Test Isolation Levels.
	 * Use this test along with JPALocksPartnerTest to test the effect
	 * of different Lock Modes.  Best run in debug mode with breakpoints
	 * set as per comments in the code.
	 * Some things to observe.
	 * - Try doing a find with one of the PESSIMISTIC lock modes.
	 *   Stop at a break point before the commit.
	 *   Try and execute an update statement from psql for Student id 1.
	 *   You will see the update block till you stop past the commit in
	 *   this code.
	 * - Using an Optimistic Lock mode will not block the psql update.
	 *   But _note that to use Optimistic Lock mode you need to make
	 *   sure the @Version in StudentVersioned is not commented out.
	 * - Start both tests in debug mode and make them get
	 *   the PESSIMISTIC_READ lock.  Neither of the tests should block
	 *   for the lock.  But the update in psql should still block.
	 * - If one OR both tests want a PESSIMISTIC_WRITE lock
	 *   then the last one to try getting the lock will wait till the
	 *   other has committed.
	 *
	 */
	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void testLockModesWithOurOwnEntityManger() {
		EntityManager myManager = emf.createEntityManager();
		myManager.getTransaction().begin();

		TypedQuery<StudentVersioned> query = myManager.createQuery("select s from StudentVersioned s", StudentVersioned.class);
		List<StudentVersioned> students = query.getResultList();
//		StudentVersioned s = myManager.find(StudentVersioned.class, 1, LockModeType.PESSIMISTIC_READ);
		StudentVersioned s = myManager.find(StudentVersioned.class, 1, LockModeType.PESSIMISTIC_WRITE);
//		StudentVersioned s = myManager.find(StudentVersioned.class, 1, LockModeType.OPTIMISTIC);
		s.setName("Myrtle");

		//Put a Break point here at the flush.
		//You should be able to read the data from psql for student with id 1, even though
		//we have the PESSIMISTIC_READ lock.
		//If you try and update the Student from psql, you should block till this
		//transaction commits.
		myManager.getTransaction().commit();
		myManager.clear();

		s = myManager.find(StudentVersioned.class, 1);
		assertEquals("Myrtle", s.getName());
		System.out.println("student at end is " + s);

		myManager.close();
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void testPersistStudentCascade() {
		// Use a local EntityManager here, just because
		EntityManager entityManager = emf.createEntityManager();

		Student s = new Student("Joe");

		entityManager.getTransaction().begin();

		Course course = entityManager.find(Course.class, 1);
		ScheduledClass sc = new ScheduledClass(course, LocalDate.parse("2019-10-10"), LocalDate.parse("2020-10-10"));
		assertEquals(0, s.getId());

		s.addClass(sc);
		// Have to handle both sides of the relationship
		// in memory - Hibernate will take care of this in
		// the database for us because of Cascade.persist
		sc.addStudent(s);

		entityManager.persist(s);
		// entityManager.flush();
		// We can commit if we own the EntityManager
		entityManager.getTransaction().commit();

		assertNotEquals(0, s.getId());
		assertNotEquals(0, sc.getId());

		// Now read it back to see if it all worked
		Student s2 = entityManager.find(Student.class, s.getId());

		assertEquals(1, s2.getClasses().size());

		System.out.println("post persist cascade student is " + s2);
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void testUpdateCascade() {
		EntityManager localManager = emf.createEntityManager();
		localManager.getTransaction().begin();

		Student s = localManager.find(Student.class, 1);
		assertEquals(1, s.getId());

		Course course = localManager.find(Course.class, 1);
		ScheduledClass sc = new ScheduledClass(course, LocalDate.parse("2019-10-10"), LocalDate.parse("2020-10-10"));

		s.addClass(sc);
		sc.addStudent(s);

		localManager.getTransaction().commit();
		// entityManager.merge(s);
		// entityManager.flush();

		// Now read it back to hopefully get the new class
		Student s2 = localManager.find(Student.class, 1);
		ScheduledClass sc2 = localManager.find(ScheduledClass.class, sc.getId());

		assertEquals(3, s2.getClasses().size());
		assertEquals(1, sc2.getStudents().size());
		assertEquals(1, sc2.getStudents().get(0).getId());

		System.out.println("post persist student is " + s2);
	}

	/**
	 * The ScheduledClass should *not* be removed
	 */
	@Test
	@Transactional
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void testRemoveStudentCascade() {
		Student s = entityManager.find(Student.class, 1);
		assertEquals(1, s.getId());
		assertEquals(2, s.getClasses().size());

		entityManager.remove(s);
		entityManager.flush();

		// Student should be null
		Student s2 = entityManager.find(Student.class, 1);
		// Should still get the Scheduled Class because DELETE is not cascaded.
		ScheduledClass sc = entityManager.find(ScheduledClass.class, 2);

		assertNull(s2);
		assertNotNull(sc);
	}

	@Test
	@Transactional
	public void testGetScheduledClass() {
		ScheduledClass sc = entityManager.find(ScheduledClass.class, 2);
		System.out.println("class is " + sc + "student size " + sc.getStudents().size());
		assertEquals(1, sc.getStudents().size());
	}

	/**
	 * This test shows that you have to delete from the owning side of a
	 * relationship for the persistence layer to do anything. Just deleting the
	 * ScheduledClass does nothing. See testDeleteScheduledClassCorrectly for an
	 * implementation that works but could be horrendously expensive.
	 */
//	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
//	@Transactional(propagation = Propagation.REQUIRED)
	public void testDeleteScheduledClassWrongly() {
		EntityManager localManager = emf.createEntityManager();
		localManager.getTransaction().begin();
		;
		// Get student with Id 1
		Student student = localManager.find(Student.class, 2);
		System.out.println("After find student: " );
//		System.out.println("student is " + student);

		// They are registered for class with Id 2
		ScheduledClass sc = localManager.find(ScheduledClass.class, 2);
		System.out.println("class is " + sc + "students " + sc.getStudents());
		assertEquals(1, sc.getStudents().size());

		// Try and remove the class
		localManager.remove(sc);
		localManager.getTransaction().commit();
		// And flush and clear to your heart's content
//		localManager.flush();
//		localManager.clear();

		// But guess who is still there - everybody
		ScheduledClass sc2 = localManager.find(ScheduledClass.class, 2);
		Student s2 = localManager.find(Student.class, 2);

		System.out.println("student after delete is " + s2);
		System.out.println("sc after delete is " + sc2);

		assertNotNull(sc2);
		assertNotNull(s2);
	}

	/**
	 * To properly delete a dependent object, you have to delete from where it is
	 * managed. In the case of ScheduledClass, we have to iterate through all the
	 * Students for the class, then iterate through the ScheduledClasses for each
	 * Student and remove this one.
	 */
	@Test
	@Transactional
	public void testDeleteScheduledClassCorrectly() {
		Student student = entityManager.find(Student.class, 1);
		System.out.println("student is " + student);

		ScheduledClass sc = entityManager.find(ScheduledClass.class, 3);
		System.out.println("class is " + sc + "students " + sc.getStudents());
		assertEquals(2, sc.getStudents().size());
		assertEquals(2, student.getClasses().size());

		// Remove all the students for this class
		for (Student s : sc.getStudents()) {
			s.dropClass(sc);
		}

		entityManager.remove(sc);
		entityManager.flush();

		ScheduledClass sc2 = entityManager.find(ScheduledClass.class, 3);
		Student s2 = entityManager.find(Student.class, 1);

		System.out.println("student after delete is " + s2);
		System.out.println("sc after delete is " + sc2);

		assertNull(sc2);
		assertEquals(1, s2.getClasses().size());
	}

	@Test
//	@Transactional(propagation = Propagation.REQUIRED)
	public void testDeleteScheduledClassDirectly() {
		int classToDelete = 2;
		EntityManager localManager = emf.createEntityManager();
		try {
			//First Check to see that the student has 2 classes currently
			TypedQuery<Student> tq = localManager.createNamedQuery("Student.bigSelectOne", Student.class);
			tq.setParameter("id", 1);

			Student student = tq.getSingleResult();
			Set<ScheduledClass> classes = student.getClasses();
			assertEquals(2, student.getClasses().size());

			long count = classes.stream()
					.filter(sc -> sc.getId() == classToDelete)
					.count();
			assertEquals(1, count);

			localManager.close();
			/*********************************************************/

			localManager = emf.createEntityManager();

			localManager.getTransaction().begin();
			// Remove all links to classToDelete in the link table
			Query query = localManager
					.createNativeQuery("delete from student_scheduledclass where classes_id = :id");
			query.setParameter("id", classToDelete);
			int rows = query.executeUpdate();
			assertEquals(1, rows);

			//Delete the class
			query = localManager.createNativeQuery("delete from scheduledclass where " + "id = :id");
			query.setParameter("id", classToDelete);
			rows = query.executeUpdate();
			assertEquals(1, rows);

			localManager.getTransaction().commit();
			/*********************************************************************/
			//Sanity checks.

			//The classToDelte should not exist
			ScheduledClass gone = localManager.find(ScheduledClass.class, classToDelete);
			assertNull(gone);

			//Get the student again
			tq = localManager.createNamedQuery("Student.bigSelectOne", Student.class);
			tq.setParameter("id", 1);

			student = tq.getSingleResult();

			assertEquals(1, student.getClasses().size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
