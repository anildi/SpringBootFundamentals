package ttl.larku.dao.repository.rest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ttl.larku.domain.Student;

/**
 * An Example of using Spring Data Rest to expose a Repository as a REST resource.
 *
 * https://docs.spring.io/spring-data/rest/docs/3.4.5/reference/html/#reference
 *
 * Inline examples of overriding methods etc.
 *
 */
@RepositoryRestResource
public interface StudentDataRestRepo extends JpaRepository<Student, Integer> {

    //The 'findByName' method will make Spring generate a query like this:
    //With Spring Data Rest, you get access to these through the
    //'search' url. e.g. .../students/search/findByName?name=Johnny
    //@Query("select s from Student s where s.name = :name")
    public List<Student> findByName(@Param("name") String name);

    //Find using sql like wildcards.
    //e.g. .../students/search/findByNameLike?name=%25John%25
    public List<Student> findByNameLike(@Param("name") String name);

    // Change method visibility by overriding them and
    // setting exported to false
    // Comment out the following two lines to hide the findById method
//    @RestResource(exported = false)
//    Optional<Student> findById(Integer primaryKey);

}
