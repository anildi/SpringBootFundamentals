package ttl.larku.dao;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Student;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@Sql(scripts = {"/schema-h2.sql", "/data-h2.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/ttl/larku/db/createVersionedDB-h2.sql",
		"/ttl/larku/db/populateVersionedDB-h2.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Tag("expensive")
public class JPACriteria {

	@Autowired
	private EntityManager em;

	@Test
	@Transactional
	public void testSimpleCriteria() {

		TypedQuery<Student> tq = em.createQuery("select distinct s from Student s",
				Student.class);
		List<Student> tqStudents = tq.getResultList();
		assertEquals(9, tqStudents.size());

		//Get the builder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//Create a query that will return Students
		CriteriaQuery<Student> cq = builder.createQuery(Student.class);
		
		//Student is also going to be the (only) root entity we will
		//be searching from.  This need not always be the same as the 
		//type returned from the query.  This is the 'From' clause.
		Root<Student> queryRoot = cq.from(Student.class);
		//no where clauses so we just do a select.
		cq.select(queryRoot);
		
		
		//And now run it
		TypedQuery<Student> runnableQuery = em.createQuery(cq);
		List<Student> students = runnableQuery.getResultList();
		assertEquals(9, students.size());
		
		for(Student s : students) {
			out.println(s);
		}
		
	}
	
	@Test
	public void testNotSoSimpleCriteria() {
		
        TypedQuery<Student> query =
                em.createQuery("select distinct s from Student s "
                		+ "left join fetch s.classes sc left join fetch sc.course" , Student.class);
		List<Student> tqStudents = query.getResultList();
		out.println("TQStudents: ");
		tqStudents.forEach(out::println);
		assertEquals(9, tqStudents.size());

		//Get the builder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//Create a query that will return Students
		CriteriaQuery<Student> cq = builder.createQuery(Student.class);
		
		//Student is also going to be the (only) root entity we will
		//be searching from.  This need not always be the same as the 
		//type returned from the query.  This is the 'From' clause.
		Root<Student> queryRoot = cq.from(Student.class);
		queryRoot.fetch("classes", JoinType.LEFT); //.fetch("course", JoinType.LEFT);
		cq.select(queryRoot).distinct(true);

		
		List<Student> students = em.createQuery(cq).getResultList();
		for(Student s : students) {
			out.println(s);
		}

		assertEquals(9, students.size());
	}

	@Test
	public void testWithParameters() {
		
        TypedQuery<Student> query =
                em.createQuery("select distinct s from Student s "
                		+ "left join fetch s.classes sc left join fetch sc.course where s.id = :id" , Student.class);
		query.setParameter("id", 2);
		Student tqStudent = query.getSingleResult();
        out.println("TQStudent: " + tqStudent);
		assertTrue(tqStudent.getName().contains("Ana"));

		//Get the builder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//Create a query that will return Students
		CriteriaQuery<Student> cq = builder.createQuery(Student.class);
		
		//Student is also going to be the (only) root entity we will
		//be searching from.  This need not always be the same as the 
		//type returned from the query.  This is the 'From' clause.
		Root<Student> queryRoot = cq.from(Student.class);
		queryRoot.fetch("classes", JoinType.LEFT); //.fetch("course", JoinType.LEFT);

		ParameterExpression<Integer> p = builder.parameter(Integer.class);
		cq.select(queryRoot).distinct(true).where(builder.equal(queryRoot.get("id"), p));

		Student student = em.createQuery(cq).setParameter(p, 2).getSingleResult();

		out.println("Student: " + student);
		assertTrue(student.getName().contains("Ana"));
	}

	@Test
	public void testWithParametersForStatus() {

		TypedQuery<Student> query =
				em.createQuery("select distinct s from Student s "
						+ "left join fetch s.classes sc left join fetch sc.course where s.status = :status" , Student.class);
		query.setParameter("status", Student.Status.FULL_TIME);
		List<Student> tqStudents = query.getResultList();
		out.println("TQStudents: ");
		tqStudents.forEach(System.out::println);
		assertEquals(2, tqStudents.size());

		//Get the builder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//Create a query that will return Students
		CriteriaQuery<Student> cq = builder.createQuery(Student.class);

		//Student is also going to be the (only) root entity we will
		//be searching from.  This need not always be the same as the
		//type returned from the query.  This is the 'From' clause.
		Root<Student> queryRoot = cq.from(Student.class);
		queryRoot.fetch("classes", JoinType.LEFT); //.fetch("course", JoinType.LEFT);

		ParameterExpression<Student.Status> p = builder.parameter(Student.Status.class);
		cq.select(queryRoot).distinct(true).where(builder.equal(queryRoot.get("status"), p));

		List<Student> students = em.createQuery(cq).setParameter(p, Student.Status.FULL_TIME).getResultList();

		out.println("Students: ");
		students.forEach(System.out::println);
		assertEquals(2, students.size());
	}

	/**
	 * Group By is used to aggregate results according to some criteria.
	 * e.g. below, we are selecting students and grouping them by
	 * status.  Our result contains the Status and the count for that
	 * status.
	 *
	 * With Group By, you can't select whole entities.  You can only select
	 * properties of Entities that ARE ALSO IN THE GROUP BY clause, and you
	 * can have aggregate functions like count, sum, avg, min and max.
	 */
	@Test
	public void testWithGroupBy() {

		TypedQuery<Object[]> query =
				em.createQuery("select s.status, count(s) from Student s "
						+ "group by s.status" , Object[].class);
		List<Object[]> outerListTq = query.getResultList();
		outerListTq.forEach(oi -> out.println("Status: " + oi[0] + ", count: " + oi[1]));

		//Get the builder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//Create a query that will return Students
		CriteriaQuery<Object[]> cq = builder.createQuery(Object[].class);

		//Student is also going to be the (only) root entity we will
		//be searching from.  This need not always be the same as the
		//type returned from the query.  This is the 'From' clause.
		Root<Student> queryRoot = cq.from(Student.class);

		cq.multiselect(queryRoot.get("status"), builder.count(queryRoot));
		cq.groupBy(queryRoot.get("status"));

		List<Object[]> outerList = em.createQuery(cq).getResultList();

		out.println("Criteria Results");
		outerList.forEach(oi -> out.println("Status: " + oi[0] + ", count: " + oi[1]));

		assertEquals(3, outerList.size());
	}

	/**
	 * The having part of group by can be use as a filter to only have
	 * certain groups in the result.  E.g. in the example below we are
	 * only going to get statuses with counts greater than 2.
	 */
	@Test
	public void testWithGroupByHaving() {

		TypedQuery<Object[]> query =
				em.createQuery("select s.status, count(s) from Student s "
						+ "group by s.status having count(s) > 2" , Object[].class);
		List<Object[]> outerListTq = query.getResultList();
		outerListTq.forEach(oi -> out.println("Status: " + oi[0] + ", count: " + oi[1]));

		//Get the builder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//Create a query that will return Students
		CriteriaQuery<Object[]> cq = builder.createQuery(Object[].class);

		//Student is also going to be the (only) root entity we will
		//be searching from.  This need not always be the same as the
		//type returned from the query.  This is the 'From' clause.
		Root<Student> queryRoot = cq.from(Student.class);

		//Select s.status, count(s)
		cq.multiselect(queryRoot.get("status"), builder.count(queryRoot));
		//Group by s.status
		cq.groupBy(queryRoot.get("status"));
		//Having count(s) > 2
		cq.having(builder.gt(builder.count(queryRoot), 2));

		List<Object[]> outerList = em.createQuery(cq).getResultList();

		out.println("Criteria Results");
		outerList.forEach(oi -> out.println("Status: " + oi[0] + ", count: " + oi[1]));

		assertEquals(2, outerList.size());
	}

	@Test
	public void testOrderBy() {

		TypedQuery<Student> query =
				em.createQuery("select distinct s from Student s "
						+ "left join fetch s.classes sc left join fetch sc.course order by s.status" , Student.class);
		List<Student> tqStudents = query.getResultList();
		out.println("TQStudents: ");
		tqStudents.forEach(out::println);
		assertEquals(9, tqStudents.size());

		//Get the builder
		CriteriaBuilder builder = em.getCriteriaBuilder();
		//Create a query that will return Students
		CriteriaQuery<Student> cq = builder.createQuery(Student.class);

		//Student is also going to be the (only) root entity we will
		//be searching from.  This need not always be the same as the
		//type returned from the query.  This is the 'From' clause.
		Root<Student> queryRoot = cq.from(Student.class);
		queryRoot.fetch("classes", JoinType.LEFT); //.fetch("course", JoinType.LEFT);
		cq.select(queryRoot).distinct(true);

		//Order by
		cq.orderBy(builder.asc(queryRoot.get("status")));


		List<Student> students = em.createQuery(cq).getResultList();
		out.println("Criteria results:");
		for(Student s : students) {
			out.println(s);
		}

		assertEquals(9, students.size());
	}
}
