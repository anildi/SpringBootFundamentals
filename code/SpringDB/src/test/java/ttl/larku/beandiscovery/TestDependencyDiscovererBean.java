package ttl.larku.beandiscovery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import org.springframework.context.ApplicationContext;

/**
 * @author whynot
 */
@SpringBootTest
//@Disabled
public class TestDependencyDiscovererBean {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DependancyDiscoverer dd;

    @Autowired
    private ObjectMapper mapper;

    private ObjectWriter writer;

    @PostConstruct
    private void uberInit() {
        writer = mapper.writerWithDefaultPrettyPrinter();
    }

    @Test
    public void testDependenciesWithDDBean() {
       List<BeanNode> dependencies = dd.getDependencies("ttl.larku.service.Course");
       dependencies.forEach(node -> {
           System.out.println("***************************************");
           System.out.println("Dependencies for Bean: " + node.getBeanName() + " : " + node.getClazz());
           dd.printBeanNode(node, "");
       });
    }

    @Test
    public void testDependantsWithDDBean() {
        List<BeanNode> dependencies = dd.getDependants("ttl.larku.service.CourseService");
        dependencies.forEach(node -> {
            System.out.println("***************************************");
            System.out.println("Beans Dependant on: " + node.getBeanName() + " : " + node.getClazz());
            dd.printBeanNode(node, "   ");
//            try {
//                //System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
//                System.out.println(writer.writeValueAsString(node));
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
        });
    }
}
