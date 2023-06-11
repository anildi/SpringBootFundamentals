package ttl.larku.domain;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

/**
 * A projection to get the student and some specific
 * information about their Classes, as specified
 * by ClassWithCourseCode
 *
 * @author whynot
 */
@Projection(name = "summary", types = {Student.class})
public interface StudentCourseCodeSummary {

    int getId();

    String getName();

    //This relies on another projection to get selected
    //information about each class
    List<ClassWithCourseCode> getClasses();
}
