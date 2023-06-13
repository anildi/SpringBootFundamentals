package ttl.larku.jconfig;

import org.springframework.context.annotation.Bean;
import ttl.larku.service.CourseService;

//@Configuration
public class TwoConfig {

    @Bean
    public CourseService courseService() {
       CourseService cs = new CourseService();
       return cs;
    }

}
