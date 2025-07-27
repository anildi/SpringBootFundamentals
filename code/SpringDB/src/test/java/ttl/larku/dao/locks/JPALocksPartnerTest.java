package ttl.larku.dao.locks;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.annotations.MyTestContainers;
import ttl.larku.domain.StudentVersioned;
import ttl.larku.service.StudentDaoService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Tag("dao")
@Disabled
public class JPALocksPartnerTest { // extends {

	@PersistenceUnit
	private EntityManagerFactory emf;

	@PersistenceContext
	private EntityManager entityManager;

	@Resource(name = "studentDaoService")
	private StudentDaoService studentService;


	/**
	 * This test should be run in conjunction with the one in
	 * JPALocksTest::testLockModesWithOurOwnEntitManager.  Run that to the
	 * break point at the flush, then run this with a different lock mode to see
	 * how far it progresses.
	 */
	@Test
	public void testLockModesWithOurOwnEntityManger2() {
		EntityManager myManager = emf.createEntityManager();
		myManager.getTransaction().begin();

		TypedQuery<StudentVersioned> query = myManager.createQuery("select s from StudentVersioned s", StudentVersioned.class);
		List<StudentVersioned> students = query.getResultList();
//		StudentVersioned s = myManager.find(StudentVersioned.class, 1, LockModeType.PESSIMISTIC_READ);
		StudentVersioned s = myManager.find(StudentVersioned.class, 1, LockModeType.PESSIMISTIC_WRITE);
//		StudentVersioned s = myManager.find(StudentVersioned.class, 1, LockModeType.OPTIMISTIC);
		s.setName("JPALocksPartner");

		//Put a Break point here at the flush.
		//You should be able to read the data from psql for student with id 1, even though
		//we have the PESSIMISTIC_READ lock.
		//If you try and update the Student from psql, you should block till this
		//transaction commits.
		myManager.getTransaction().commit();
		myManager.clear();

		s = myManager.find(StudentVersioned.class, 1);
		assertEquals("JPALocksPartner", s.getName());
		System.out.println("student at end is " + s);

		myManager.close();
	}
}
