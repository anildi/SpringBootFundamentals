package ttl.larku.misc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ttl.larku.dao.BaseDAO;
import ttl.larku.dao.inmemory.InMemoryStudentDAO;
import ttl.larku.domain.Student;
import ttl.larku.service.CourseService;
import ttl.larku.service.StudentService;

import javax.annotation.Resource;

@Component
public class ExamplesForSlides
{
    //Better be 1 and only 1 bean of type CourseService.
    //Or, Just to make things confusing, if there is
    //more than 1 bean of type CourseService, then
    //only 1 of them should have the name courseService
    @Autowired
    private CourseService courseService;

    //Two features in one example.
    //Won't fail if nothing matches.
    //And will bring in *all* beans of
    //type CourseService in the context.
    @Autowired(required = false)
    private CourseService[] courseServices;

}

class Autowired2 {
    //Useful and safe for injecting
    //framework objects that we *know* there
    //will be only one of.
    @Autowired
    private ApplicationContext context;

    //Can qualify which bean to use
    //in case of clashes
    @Autowired
    @Qualifier("us-east")
    private BaseDAO<Student> studentDAO;
}

@Component
@Qualifier("us-east")
class DAOForEast extends InMemoryStudentDAO {

}

@Component
class ConstructorInjectionDemo {
    //@Autowired
    private final CourseService courseService;

    //Prefer constructor injection to field injection.
    //Can use the @Autowired annotation to specify which
    //constructor Spring should use.  All arguments passed
    //in to the constructor should be beans.
    public ConstructorInjectionDemo(CourseService courseService) {
       this.courseService = courseService;
    }
}

@Component
class TricksWithResource {
    //Better be a bean named courseService.
    //Or, to make things confusing, if there is
    //more than one bean named courseService, than
    //only one of them should be of type CourseService
    @Resource
    private CourseService courseService;

    //This would work either if there was a bean
    //named "cs" or a bean of type CourseService
    //called something else.
    @Resource
    private CourseService cs;

    //Always use a name with @Resource
    @Resource(name = "studentService")
    private StudentService studentService;
}
