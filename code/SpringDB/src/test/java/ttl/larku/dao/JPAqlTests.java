package ttl.larku.dao;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = {"/schema-h2.sql", "/data-h2.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Tag("dao")
public class JPAqlTests {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @PersistenceContext
    private EntityManager em;


    @Test
    @Transactional
    public void testWierdBehaviourWithToStringAndManyToMany() {
        //This has to do with weird interaction with the IDE.
        // If we include 'classes' in the toString method of Student,
        // it seems to invoke eager loading of the
        //classes.  It makes sense if the toString method was getting called,
        //because then you are accessing the 'classes' collection, and so they have to
        //get loaded.  But it does not seem to be getting called in
        //any way that is visible to the debugger.  Strange but true.
        //What is happening is that the IDE calls the toString method to
        //give you those nice value hints for variables.  In this case
        //that accesses the 'classes' collection, causing it to be
        //loaded.

        // So either we turn off that feature in the IDE
        //(File | Settings | Build, Execution, Deployment | Debugger | Data Views | Java)
        //Enable toString() object view:  - Turn it off.
        //Or we leave out the collection property from the toString method.

        //This test works best if you step through the code in a debugger.
        //And with Hibernate tracing turned on at the highest level.
        //With 'classes' commented out in the Student::toString you will
        //see the first find will just get the student.  The call to
        //getClasses will initiate the select of the scheduled classes.
        //With 'classes' uncommented you will see the second select
        //happen during the call to 'find'.
        Student s = em.find(Student.class, 1);
        System.out.println("After Find");
        System.out.println("Class size: " + s.getClasses().size());
    }

    @Test
    public void testLeftJoinFetchStudents() {
        TypedQuery<Student> query =
                em.createQuery("select distinct s from Student s left join fetch s.classes sc left join fetch sc.course" , Student.class);
//        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);


        List<Student> result = query.getResultList();
        result.forEach(System.out::println);
        assertEquals(4, result.size());
    }

    @Test
    public void testLeftJoinFetchScheduledClasses() {
        TypedQuery<ScheduledClass> query =
                em.createQuery("select distinct sc from ScheduledClass sc left join fetch sc.students where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
//        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
        LocalDate startDate = LocalDate.parse("2012-10-10");
        String code = "BOT-202";
        query.setParameter("startDate", startDate);
        query.setParameter("code", code);


        List<ScheduledClass> result = query.getResultList();
        result.forEach(System.out::println);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getStudents().size());
    }

    @Test
    public void testLazyInstantionException() {
        assertThrows(LazyInitializationException.class, () -> {
            //Note - Leaving the 'fetch' out of the left join gives ut the LIE
            TypedQuery<ScheduledClass> query =
                    em.createQuery("select distinct sc from ScheduledClass sc left join sc.students where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
//        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);

            LocalDate startDate = LocalDate.parse("2012-10-10");
            String code = "BOT-202";
            query.setParameter("startDate", startDate);
            query.setParameter("code", code);

            List<ScheduledClass> result = query.getResultList();
            result.forEach(System.out::println);
            assertEquals(1, result.size());
            assertEquals(2, result.get(0).getStudents().size());
        });
    }

    /**
     * Here, we make the test method Transactional, so the Transaction will
     * begin before this method gets called.  In this case the call to
     * getStudents() happens in a Transaction, so no LIE.
     */
    @Test
    @Transactional
    public void testTransactions() {
        //No join fetch here
        TypedQuery<ScheduledClass> query =
                em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);
//        em.createQuery("select distinct sc from ScheduledClass sc where sc.startDate = :startDate and sc.course.code = :code", ScheduledClass.class);

        LocalDate startDate = LocalDate.parse("2012-10-10");
        String code = "BOT-202";
        query.setParameter("startDate", startDate);
        query.setParameter("code", code);

        List<ScheduledClass> result = query.getResultList();
        result.forEach(System.out::println);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getStudents().size());
    }

    /**
     * In this test, we only fetch the ScheduledClasses first.  Then
     * we iterate over them and do fetch the Students for each one.
     * This would normally give us a LazyInstantionException, but we
     * are spared this time because we are using a local EntityManager
     * and fetching the Students in a transaction.
     */
    @Test
    public void testNPlusOneIssue() {
        EntityManager localManager = emf.createEntityManager();
        //No join fetch here
        TypedQuery<ScheduledClass> query = localManager.createQuery("select sc from " +
                "ScheduledClass sc where sc.startDate = :startDate and " +
                "sc.course.code = :code", ScheduledClass.class);
        LocalDate startDate = LocalDate.parse("2012-10-10");
        String code = "BOT-202";
        query.setParameter("startDate", startDate);
        query.setParameter("code", code);


        //Work happens in a transaction, so no LIE.
        //But can be costly, because you make 1 SQL call for
        //each Student in the collection
        localManager.getTransaction().begin();
        List<ScheduledClass> result = query.getResultList();
        for (ScheduledClass sc : result) {
            List<Student> students = sc.getStudents();
            students.forEach(System.out::println);
        }
        localManager.getTransaction().commit();
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getStudents().size());
    }
}

/*
For this JPAQL:        
	TypedQuery<ScheduledClass> query = localManager.createQuery("select distinct sc from " +
                "ScheduledClass sc where sc.startDate = :startDate and " +
                "sc.course.code = :code", ScheduledClass.class);

Here is Hibernate code:
//Get the ScheduledClass and Student info
select
            distinct scheduledc0_.id as id1_1_0_,
            student2_.id as id1_2_1_,
            scheduledc0_.course_id as course_i4_1_0_,
            scheduledc0_.endDate as enddate2_1_0_,
            scheduledc0_.startDate as startdat3_1_0_,
            student2_.name as name2_2_1_,
            student2_.phoneNumber as phonenum3_2_1_,
            student2_.status as status4_2_1_,
            students1_.classes_id as classes_2_3_0__,
            students1_.students_id as students1_3_0__ 
        from
            ScheduledClass scheduledc0_ 
        left outer join
            Student_ScheduledClass students1_ 
                on scheduledc0_.id=students1_.classes_id 
        left outer join
            Student student2_ 
                on students1_.students_id=student2_.id cross 
        join
            Course course3_ 
        where
            scheduledc0_.course_id=course3_.id 
            and scheduledc0_.startDate=? 
            and course3_.code=?
            
//Get the course Info
Hibernate: 
    select
        course0_.id as id1_0_0_,
        course0_.code as code2_0_0_,
        course0_.credits as credits3_0_0_,
        course0_.title as title4_0_0_ 
    from
        Course course0_ 
    where
        course0_.id=?
*/

/*
Here's hand crafted SQL which will give you a big table that you will 
have to pick through to make the ScheduledClass and it's list of students and its 
associated course.

select sc.id, sc.startdate, sc.enddate, s.id as student_id, s.name, s.phonenumber, s.status, c.code, c.credits, c.title  from scheduledclass sc 
     left join student_scheduledclass ssc on sc.id = ssc.classes_id
     left join student s on s.id = ssc.students_id
     join course c on sc.course_id = c.id; 
     --where c.code = 'BOT-202'; 
 
*/
