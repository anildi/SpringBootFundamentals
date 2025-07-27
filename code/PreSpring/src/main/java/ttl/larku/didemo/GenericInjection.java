package ttl.larku.didemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

public class GenericInjection {
}

@Configuration
class MyConfig
{
    @Bean
    public List<String> letters() {
        return List.of("a", "b", "c");
    }

    @Bean
    public List<String> symbols() {
        return List.of("#", "%", "$");
    }

    @Bean
    public List<Integer> listOfInts() {
        return List.of(0, 39, 30, 3838054);
    }

}

@Component
class MyApp
{
    @Autowired
    List<List<String>> strings;

    @Autowired
    Map<String, List<String>> mapOfLists;

    @Autowired
    List<Integer> ints;



    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("ttl.larku.didemo");
        context.refresh();

        MyApp app = context.getBean("myApp", MyApp.class);

        app.strings.forEach(System.out::println);

        app.ints.forEach(System.out::println);

        System.out.println("Map: " + app.mapOfLists);
    }
}


