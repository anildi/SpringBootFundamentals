package ttl.larku.dao;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.controllers.rest.RestResultGeneric;
import ttl.larku.controllers.rest.RestResultGeneric.Status;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;
import ttl.larku.domain.StudentVersioned;
import ttl.larku.service.StudentDaoService;

import javax.annotation.Resource;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest(properties = {"spring.h2.console.enabled=true" }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@Sql(scripts = {"/schema-h2.sql", "/data-h2.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/ttl/larku/db/createVersionedDB-h2.sql",
		"/ttl/larku/db/populateVersionedDB-h2.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Transactional
//@Rollback(false)
@Disabled
@Tag("dao")
public class JPALocksTest {

	@PersistenceUnit
	private EntityManagerFactory emf;

	@PersistenceContext
	private EntityManager entityManager;

	@Resource(name = "studentDaoService")
	private StudentDaoService studentService;

	@Test
	@Transactional
	public void testCreateStudent() {
		Student s = new Student("Joe");
		assertEquals(0, s.getId());

		entityManager.persist(s);

		assertNotEquals(0, s.getId());

		System.out.println("post persist student is " + s);
	}

	@Test
//    @Transactional(isolation = Isolation.READ_COMMITTED)
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
	// @Transactional(isolation = Isolation.SERIALIZABLE)
	public void testUpdateStudentVersioned() {
		try {
			StudentVersioned s = entityManager.find(StudentVersioned.class, 1); 
			studentService.updateStudentVersioned(s);

			s = entityManager.find(StudentVersioned.class, 1);
			assertEquals("Myrtle", s.getName());
			System.out.println("student at end is " + s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test Isolation Levels.
	 */
	@Test
	@Transactional(isolation = Isolation.READ_COMMITTED)
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
	// @Transactional(isolation = Isolation.SERIALIZABLE)
	public void testUpdateStudent() {

		TypedQuery<Student> query = entityManager.createQuery("select s from Student s", Student.class);
		List<Student> students = query.getResultList();
		Student s = entityManager.find(Student.class, 1, LockModeType.PESSIMISTIC_READ);
		s.setName("Myrtle");
		s.getClasses().remove(0);

		// assertEquals(2, s.getClasses().size());
		entityManager.flush();
		entityManager.clear();

		s = entityManager.find(Student.class, 1);
		assertEquals("Myrtle", s.getName());
		assertEquals(2, s.getClasses().size());
		System.out.println("student at end is " + s);
	}

	/**
	 * Test Isolation Levels.
	 */
	@Test
	public void testUpdateStudentWithStudentService() {

		TypedQuery<Student> query = entityManager.createNamedQuery("Student.bigSelectOne", Student.class);
		query.setParameter("id", 2);
		Student student = query.getSingleResult();

		student.setName("Myrtle");
		student.getClasses().remove(0);

		RestResultGeneric<Student> rr = studentService.updateStudentR(student);

		assertTrue(rr.getStatus() == Status.Ok);

		query = entityManager.createNamedQuery("Student.bigSelectOne", Student.class);
		query.setParameter("id", 2);
		student = query.getSingleResult();

		assertEquals("Myrtle", student.getName());
		assertEquals(2, student.getClasses().size());
		System.out.println("student at end is " + student);
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

		s.getClasses().add(sc);
		// Have to handle both sides of the relationship
		// in memory - Hibernate will take care of this in
		// the database for us because of Cascade.persist
		sc.getStudents().add(s);

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
	public void testMergeStudentCascade() {
		EntityManager localManager = emf.createEntityManager();
		localManager.getTransaction().begin();

		Student s = localManager.find(Student.class, 1);
		assertEquals(1, s.getId());

		Course course = localManager.find(Course.class, 1);
		ScheduledClass sc = new ScheduledClass(course, LocalDate.parse("2019-10-10"), LocalDate.parse("2020-10-10"));

		s.getClasses().add(sc);
		sc.getStudents().add(s);

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
		assertEquals(2, sc.getStudents().size());
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
		Student student = localManager.find(Student.class, 1);
		System.out.println("After find student: " );
//		System.out.println("student is " + student);

		// They are registered for class with Id 2
		ScheduledClass sc = localManager.find(ScheduledClass.class, 2);
		System.out.println("class is " + sc + "students " + sc.getStudents());
		assertEquals(2, sc.getStudents().size());

		// Try and remove the class
		localManager.remove(sc);
		localManager.getTransaction().commit();
		// And flush and clear to your heart's content
//		localManager.flush();
//		localManager.clear();

		// But guess who is still there - everybody
		ScheduledClass sc2 = localManager.find(ScheduledClass.class, 2);
		Student s2 = localManager.find(Student.class, 1);

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

		ScheduledClass sc = entityManager.find(ScheduledClass.class, 2);
		System.out.println("class is " + sc + "students " + sc.getStudents());
		assertEquals(2, sc.getStudents().size());
		assertEquals(2, student.getClasses().size());

		// Remove all the students for this class
		// Expensive!!!
		for (Student s : sc.getStudents()) {
			// Use an iterator because we want to change the
			// List while iterating
			Iterator<ScheduledClass> it = s.getClasses().iterator();
			while (it.hasNext()) {
				ScheduledClass studentClass = it.next();
				if (studentClass.getId() == sc.getId()) {
					it.remove();
					break;
				}
			}
		}

		entityManager.remove(sc);
		entityManager.flush();

		ScheduledClass sc2 = entityManager.find(ScheduledClass.class, 2);
		Student s2 = entityManager.find(Student.class, 1);

		System.out.println("student after delete is " + s2);
		System.out.println("sc after delete is " + sc2);

		assertNull(sc2);
		assertEquals(1, s2.getClasses().size());
	}

	@Test
//	@Transactional(propagation = Propagation.REQUIRED)
	public void testDeleteScheduledClassDirectly() {
		int classToDelete = 1;
		EntityManager localManager = emf.createEntityManager();
		localManager.setFlushMode(FlushModeType.COMMIT);
		try {
			localManager.getTransaction().begin();

			// Remove all links in link table
			Query query = localManager
					.createNativeQuery("delete from student_scheduledclass where " + "classes_id = :id");
			query.setParameter("id", classToDelete);
			int rows = query.executeUpdate();

			query = localManager.createNativeQuery("delete from scheduledclass where " + "id = :id");
			query.setParameter("id", classToDelete);
			rows = query.executeUpdate();

			localManager.getTransaction().commit();

			//Should not exist
			ScheduledClass gone = localManager.find(ScheduledClass.class, classToDelete);

			TypedQuery<Student> tq = localManager.createNamedQuery("Student.bigSelectOne", Student.class);
			tq.setParameter("id", 1);

			Student student = tq.getSingleResult();

			assertNull(gone);
			assertEquals(1, student.getClasses().size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
