//package ttl.larku.dao.querydsl;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.hibernate.LazyInitializationException;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.jdbc.datasource.init.ScriptUtils;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import ttl.larku.domain.QCourse;
//import ttl.larku.domain.QScheduledClass;
//import ttl.larku.domain.QStudent;
//import ttl.larku.domain.ScheduledClass;
//import ttl.larku.domain.Student;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.PersistenceContext;
//import javax.persistence.PersistenceUnit;
//import javax.persistence.TypedQuery;
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
///**
// * @author whynot
// */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@Transactional
//public class TestQueryDsl {
//    @PersistenceUnit
//    private EntityManagerFactory emf;
//
//    @PersistenceContext
//    private EntityManager em;
//    /**
//     * A Technique to run sql scripts just once per class.
//     * This was to solve a tricky situation.  This class
//     * uses the webEnvironment.MOCK context, which would
//     * have already been created and cached by a previous
//     * test.  Since no context is created for this test,
//     * no DDL scripts are run.  So this test gets whatever
//     * was in put into the database by the previous test.
//     * If that was using different data, e.g. the "VersionedXXX"
//     * sql scripts, then tests in this class can fail in
//     * strange ways that depend on which other tests are run.
//     * This trick makes sure that this test starts with the
//     * data it is expecting.  The @Transactional then takes
//     * care of rolling back after each test.
//     * @param dataSource
//     * @throws SQLException
//     */
//    @BeforeAll
//    public static void runSqlScriptsOnce(@Autowired DataSource dataSource) throws SQLException {
//        try (Connection conn = dataSource.getConnection()) {
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource("schema-h2.sql"));
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource("data-h2.sql"));
//        }
//    }
//
//    @Test
//    public void testLeftJoinFetchStudents() {
//        TypedQuery<Student> query =
//                em.createQuery("select distinct s from Student s left join fetch s.classes sc left join fetch sc.course" , Student.class);
////        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
//
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        QStudent student = QStudent.student;
//        QScheduledClass sc = QScheduledClass.scheduledClass;
//        QCourse course = QCourse.course;
//
//        List<Student> allStudentsAndCourses = queryFactory.selectFrom(student)
//                .distinct()
//                .leftJoin(student.classes, sc).fetchJoin()
//                .leftJoin(sc.course, course).fetch();
//
//        System.out.println("allStudents.size: " + allStudentsAndCourses.size());
//        allStudentsAndCourses.forEach(System.out::println);
//
//        assertEquals(4, allStudentsAndCourses.size());
//    }
//
//    @Test
//    public void testLeftJoinFetchScheduledClasses() {
//        TypedQuery<ScheduledClass> query =
//                em.createQuery("select distinct sc from ScheduledClass sc left join fetch sc.students where " +
//                        "sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
////        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
//
//        LocalDate startDate = LocalDate.parse("2012-10-10");
//        String code = "BOT-202";
//
//        query.setParameter("startDate", startDate);
//        query.setParameter("code", code);
//
//        List<ScheduledClass> result = query.getResultList();
//        result.forEach(System.out::println);
//        assertEquals(1, result.size());
//        assertEquals(2, result.get(0).getStudents().size());
//
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        QStudent student = QStudent.student;
//        QScheduledClass sc = QScheduledClass.scheduledClass;
//        QCourse course = QCourse.course;
//
//        List<ScheduledClass> classes = queryFactory.selectFrom(sc)
//                .distinct()
//                .leftJoin(sc.students, student).fetchJoin()
//                .where(sc.startDate.eq(startDate).and(sc.course.code.eq(code))).fetch();
//
//        System.out.println("Qdsl classes.size: " + classes.size());
//        classes.forEach(System.out::println);
//
//        assertEquals(1, classes.size());
//        assertEquals(2, classes.get(0).getStudents().size());
//    }
//
//    @Test
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
//    public void testLazyInstantionException() {
//        assertThrows(LazyInitializationException.class, () -> {
//            //Note - Leaving the 'fetch' out of the left join gives ut the LIE
//            TypedQuery<ScheduledClass> query =
//                    em.createQuery("select distinct sc from ScheduledClass sc left join sc.students where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
////        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
//
//            LocalDate startDate = LocalDate.parse("2012-10-10");
//            String code = "BOT-202";
//            query.setParameter("startDate", startDate);
//            query.setParameter("code", code);
//
//            List<ScheduledClass> result = query.getResultList();
//            result.forEach(System.out::println);
//            assertEquals(1, result.size());
//            assertEquals(2, result.get(0).getStudents().size());
//        });
//    }
//
//    /**
//     * Here, we make the test method Transactional, so the Transaction will
//     * begin before this method gets called.  In this case the call to
//     * getStudents() happens in a Transaction, so no LIE.
//     */
//    @Test
//    @Transactional
//    public void testTransactions() {
//        //No join fetch here
//        TypedQuery<ScheduledClass> query =
//                em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
////        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
//
//        LocalDate startDate = LocalDate.parse("2012-10-10");
//        String code = "BOT-202";
//        query.setParameter("startDate", startDate);
//        query.setParameter("code", code);
//
//        List<ScheduledClass> result = query.getResultList();
//        result.forEach(System.out::println);
//        assertEquals(1, result.size());
//        assertEquals(2, result.get(0).getStudents().size());
//    }
//
//    /**
//     * In this test, we only fetch the ScheduledClasses first.  Then
//     * we iterate over them and do fetch the Students for each one.
//     * This would normally give us a LazyInstantionException, but we
//     * are spared this time because we are using a local EntityManager
//     * and fetching the Students in a transaction.
//     */
//    @Test
//    public void testNPlusOneIssue() {
//        EntityManager localManager = emf.createEntityManager();
//        //No join fetch here
//        TypedQuery<ScheduledClass> query = localManager.createQuery("select sc from " +
//                "ScheduledClass sc where sc.startDate = :startDate and " +
//                "sc.course.code = :code", ScheduledClass.class);
//        LocalDate startDate = LocalDate.parse("2012-10-10");
//        String code = "BOT-202";
//        query.setParameter("startDate", startDate);
//        query.setParameter("code", code);
//
//
//        //Work happens in a transaction, so no LIE.
//        //But can be costly, because you make 1 SQL call for
//        //each Student in the collection
//        localManager.getTransaction().begin();
//        List<ScheduledClass> result = query.getResultList();
//        for (ScheduledClass sc : result) {
//            List<Student> students = sc.getStudents();
//            students.forEach(System.out::println);
//        }
//        localManager.getTransaction().commit();
//        assertEquals(1, result.size());
//        assertEquals(2, result.get(0).getStudents().size());
//    }
//}
