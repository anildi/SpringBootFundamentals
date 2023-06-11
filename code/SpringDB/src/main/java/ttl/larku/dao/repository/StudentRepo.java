package ttl.larku.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.domain.Student;
import ttl.larku.domain.StudentCourseCodeSummary;
import ttl.larku.domain.StudentPhoneSummary;

import javax.persistence.NamedQuery;
import java.util.List;

/**
 * Examples of creating custom query methods.  Naming conventions are explained at:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.repositories
 *
 * Look at Section 6.3.2, for method naming rules.
 *
 */
@Repository
@Transactional
public interface StudentRepo extends JpaRepository<Student, Integer> {

    //This is a more efficient way of fetching the student *and* all their classes
    //than the default N + 1 approach that Hibernate/Spring will use.
    //If you leave this commented out and run a 'findAll' test, you
    //should see 1 select call for all the students (N), and then N select calls for
    //the classes for each student.  Uncommenting should give you just the one select call
    //with the left join fetch.
    //The @Override signifies that we are overriding a built-in method.
	@Override
    @Query("select distinct s from Student s left join fetch s.classes sc left join fetch sc.course")
    public List<Student> findAll();

    public Student bigSelectOne(@Param("id") int id);



    //The 'findByName' method will make Spring generate a query like this:
//    @Query("select s from Student s where s.name = :name")
    public List<Student> findByName(@Param("name") String name);

    //Find using sql wildcards
    public List<Student> findByNameIgnoreCaseContains(@Param("name") String name);

    //Projections can allow you to get a part (projection) of a
    //domain object.  In this case we only want name, id and phoneNumber.
    // See domain.StudentPhoneSummary
    public StudentPhoneSummary findPhoneSummaryById(Integer id);

    //Strange naming required to return a List of Projections.
    //For example usage, see StudentRepoTest::testProjectPhoneSummary
    public List<StudentPhoneSummary> findAllStudentPhoneSummariesBy();

    @EntityGraph(attributePaths = {"classes"})
    public List<StudentCourseCodeSummary> findStudentCourseCodesBy();

    //Paging with a Projection.
    //For example usage, see StudentRepoTest::testProjectionStudentClassCourseCode.
    //For an example of Paging a whole object, see StudentRepoTest::testPaging
    @EntityGraph(attributePaths = {"classes"})
    public Page<StudentCourseCodeSummary> findPageCourseCodeBy(Pageable p);
}
