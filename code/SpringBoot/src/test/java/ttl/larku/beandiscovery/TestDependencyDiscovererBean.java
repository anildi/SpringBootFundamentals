package ttl.larku.beandiscovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author whynot
 */
@SpringBootTest
public class TestDependencyDiscovererBean {

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
       List<BeanNode> dependencies = dd.getDependencies("org.springframework");
       dependencies.forEach(node -> {
           System.out.println("***************************************");
           System.out.println("Dependencies for Bean: " + node.getBeanName() + " : " + node.getClazz());
           dd.printBeanNode(node, "");
       });
    }

    @Test
    public void testDependantsWithDDBean() {
        List<BeanNode> dependencies = dd.getDependants("ttl.larku.service");
        dependencies.forEach(node -> {
            System.out.println("***************************************");
            System.out.println("Beans Dependant on: " + node.getBeanName() + " : " + node.getClazz());
            dd.printBeanNode(node, "   ");
        });
    }
}
