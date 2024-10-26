package ttl.larku.beandiscovery;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author whynot
 */

@SpringBootTest
public class TestDependencyDiscoverer {

    @Autowired
    private ConfigurableApplicationContext context;

    @ParameterizedTest
    @ValueSource(strings = {"larkUConfig", "registrationService", "studentRestController", "courseDAO"})
    public void getDependantBeansFor(String beanName) {
        var bf = context.getBeanFactory();
        var dependenciesForBean = bf.getDependenciesForBean(beanName);

        var bd = bf.getBeanDefinition(beanName);

        System.out.println("Dependencies for " + beanName);
        printDependencies(beanName, dependenciesForBean, "");
        System.out.println("*****************************************************");

    }

    public void printDependencies(String beanName, String[] dependencies, String indent) {

//        System.out.println("Dependencies for: " + beanName);
        for(String name : dependencies) {
            System.out.println(indent + name);
            var innerDependencies = context.getBeanFactory().getDependenciesForBean(name);
            if(innerDependencies.length > 0) {
                printDependencies(name, innerDependencies, indent + "   ");
            }
        }

    }

    @ParameterizedTest
    @ValueSource(strings = {"larkUConfig", "registrationService", "studentRestController", "courseDAO"})
    public void getBeansDependantOn(String beanName) {
        var bf = context.getBeanFactory();
        var dependantBeans = bf.getDependentBeans(beanName);

        System.out.println("Dependants for " + beanName);
        printDependants(beanName, dependantBeans, "");
        System.out.println("*****************************************************");
    }

    public void printDependants(String beanName, String [] dependantBeans, String indent) {
        for(String name : dependantBeans) {
            System.out.println(indent + name);
            var innerDependencies = context.getBeanFactory().getDependentBeans(name);
            if(innerDependencies.length > 0) {
                printDependants(name, innerDependencies, indent + "   ");
            }
        }
    }
}
