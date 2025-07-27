package ttl.larku.beandiscovery;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author whynot
 */

@Component
public class DependancyDiscoverer {

    @Autowired
    private ConfigurableApplicationContext context;

    public List<BeanNode> getDependencies(String classPattern) {
        Function<String, String []> dependencyExtractor = context.getBeanFactory()::getDependenciesForBean;
        List<BeanNode> result = getRelatedBeans(classPattern,
                dependencyExtractor);
        return result;
    }

    public List<BeanNode> getDependants(String classPattern) {
        Function<String, String []> dependantsExtractor = context.getBeanFactory()::getDependentBeans;
        List<BeanNode> result = getRelatedBeans(classPattern, dependantsExtractor);
        return result;
    }

    public List<BeanNode> getRelatedBeans(String classPattern, Function<String, String[]> extractor) {
        var bf = context.getBeanFactory();
        var bni = bf.getBeanNamesIterator();
        List<BeanNode> result = streamFromIterator(bni)
                .filter(beanName -> {
                    try {
                        return context.getType(beanName).getCanonicalName() != null &&
                        context.getType(beanName).getCanonicalName().contains(classPattern);
                    }catch(Exception ex) {
                    }
                    return false;
                })
                .map(beanName -> {
                    //BeanNode beanNode = extractor.apply(beanName);
                    BeanNode beanNode = makeTree(beanName, extractor);
                    return beanNode;
                })
                .toList();

        return result;
    }

    public void printBeanNode(BeanNode rootBeanNode, String indent) {
        String name = rootBeanNode.getBeanName();
//        System.out.println(indent + name);
//        indent += "   ";
        for (BeanNode node : rootBeanNode.getRelated()) {
            name = node.getBeanName();
            System.out.println(indent + name + " : " + node.getClazz());
            if (node.getRelated().size() > 0) {
                printBeanNode(node, indent + "   ");
            }
        }
    }

    public BeanNode makeTree(String beanName, Function<String, String[]> extractor) {
        var clazz = context.getType(beanName);
        var beanNode = new BeanNode(beanName, clazz);

        var related = extractor.apply(beanName);

        makeTreeInternal(beanNode, related, extractor);
        return beanNode;
    }

    public void makeTreeInternal(BeanNode rootBeanNode, String[] related,
                                 Function<String, String[]> extractor) {
//        System.out.println("Dependencies for: " + beanName);
        for (String name : related) {
            Class<?> clazz = null;
            try {
                clazz = context.getType(name);
            } catch (NoSuchBeanDefinitionException ex) {
            }
            if (clazz != null) {
                //Create a BeanNode and add it to the parent.
                BeanNode bn = new BeanNode(name, clazz);
                rootBeanNode.addRelated(bn);
                //Possibly recurse and get the relations for this bean.
                var innerDependencies = extractor.apply(name);
                if (innerDependencies.length > 0) {
                    makeTreeInternal(bn, innerDependencies, extractor);
                }
            }
        }
    }


    public <T> Stream<T> streamFromIterator(Iterator<T> iterator) {
        var result = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
        return result;
    }
}
