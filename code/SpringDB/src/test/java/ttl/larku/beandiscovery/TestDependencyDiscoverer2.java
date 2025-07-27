package ttl.larku.beandiscovery;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author whynot
 */

@SpringBootTest
@Disabled
public class TestDependencyDiscoverer2 {

    @Autowired
    private ConfigurableApplicationContext context;

    @ParameterizedTest
    @ValueSource(strings = {"ttl"})
    public void getDependantBeansFor(String packagePattern) {
        var bf = context.getBeanFactory();
        var bni = bf.getBeanNamesIterator();
        List<String> beanNamesAndClass = streamFromIterator(bni)
                .map(beanName -> {
                    var clazz = context.getType(beanName);
                    var packageName = clazz.getPackageName();
//                    var packageName = context.getBean(beanName).getClass().getPackageName();
                    return beanName + ":" + clazz.getCanonicalName();
                })
                .filter(pkg -> pkg.contains(packagePattern))
                .peek(pkg -> {
                    System.out.println("beanName:pkg -> " + pkg);
                })
                .toList();

        beanNamesAndClass.forEach(bnc -> {
            var arr = bnc.split(":");
            var beanName = arr[0];
            var clazz = arr[1];

            var dependenciesForBean = bf.getDependenciesForBean(beanName);

            System.out.println("Dependencies for " + beanName);
            printDependencies(beanName, dependenciesForBean, "");
            System.out.println("*****************************************************");
        });
    }

//    public void getDependantsSingle(String beanName) {
//        var dependenciesForBean = bf.getDependenciesForBean(beanName);
//
//        var bd = bf.getBeanDefinition(beanName);
//
//        System.out.println("Dependencies for " + beanName);
//        printDependencies(beanName, dependenciesForBean, "");
//        System.out.println("*****************************************************");
//
//    }

    public void printDependencies(String beanName, String[] dependencies, String indent) {

//        System.out.println("Dependencies for: " + beanName);
        for (String name : dependencies) {
            System.out.println(indent + name);
            var innerDependencies = context.getBeanFactory().getDependenciesForBean(name);
            if (innerDependencies.length > 0) {
                printDependencies(name, innerDependencies, indent + "   ");
            }
        }

    }

    @ParameterizedTest
    @ValueSource(strings = {"ttl"})
    public void getBeansDependantOn(String packagePattern) {
        var bf = context.getBeanFactory();
        var bni = bf.getBeanNamesIterator();
        List<String> beanNamesAndClass = streamFromIterator(bni)
                .map(beanName -> {
                    var clazz = context.getType(beanName);
//                    var packageName = context.getBean(beanName).getClass().getPackageName();
                    return beanName + ":" + clazz.getCanonicalName();
                })
                .filter(pkg -> pkg.contains(packagePattern))
                .peek(pkg -> {
                    System.out.println("beanName:pkg -> " + pkg);
                })
                .toList();

        beanNamesAndClass.forEach(bnc -> {
            var arr = bnc.split(":");
            var beanName = arr[0];
            var clazz = arr[1];

            var dependantBeans = bf.getDependentBeans(beanName);

            System.out.println("Dependant Beans for " + beanName);
            printDependants(beanName, dependantBeans, "");
            System.out.println("*****************************************************");
        });
    }

//    @ParameterizedTest
    @ValueSource(strings = {"larkUConfig", "registrationService", "studentRestController", "courseDAO"})
    public void getBeansDependantOnSingle(String beanName) {
        var bf = context.getBeanFactory();
        var dependantBeans = bf.getDependentBeans(beanName);

        System.out.println("Dependants for " + beanName);
        printDependants(beanName, dependantBeans, "");
        System.out.println("*****************************************************");
    }

    public void printDependants(String beanName, String[] dependantBeans, String indent) {
        for (String name : dependantBeans) {
            System.out.println(indent + name);
            var innerDependencies = context.getBeanFactory().getDependentBeans(name);
            if (innerDependencies.length > 0) {
                printDependants(name, innerDependencies, indent + "   ");
            }
        }
    }

    public <T> Stream<T> streamFromIterator(Iterator<T> iterator) {
        var result = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
        return result;
    }
}
