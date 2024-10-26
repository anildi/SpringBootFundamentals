package ttl.larku.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

/**
 * A projection to give you some Class Info, along
 * with only the Course Code of the related course
 *
 * @author whynot
 */
@Projection(name = "summary", types = {ScheduledClass.class})
public interface ClassWithCourseCode {

    int getId();

    LocalDate getStartDate();

    //Get the value dynamically
    @Value("#{target.getCourse().getCode()}")
    String getCourse();
}
