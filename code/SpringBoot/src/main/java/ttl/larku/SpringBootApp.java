package ttl.larku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import ttl.larku.domain.StudentCreatedEvent;
import ttl.larku.service.StudentService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@EnableAsync
@SpringBootApplication

//Can Customize the ComponentScan if you want.
//Here, we are going to exclude all beans under
//the "ttl.larku.app.tricky" package.
//@ComponentScan(excludeFilters = {
//        @ComponentScan.Filter(
//                type = FilterType.REGEX,
//                pattern =
//                        "ttl.larku.app.tricky.*"
//        )
//})
public class SpringBootApp {
    public static void defaultmain(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);

    }

    public static void main(String [] args) {
        SpringApplication app = new SpringApplication(SpringBootApp.class);
        app.addInitializers(new MyInitializer());
        ConfigurableApplicationContext context = app.run(args);
    }

    public static void mainWithBuilder(String [] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringBootApp.class)
            .initializers(new MyInitializer());
         builder.run(args);
    }

    /**
     * An example of main that you could use to run a Spring boot
     * application as a JavaSE application.  No Web Environment.
     * A CommandLineRunner is useful in this context to actually
     * get some work done.
     *
     * @param args
     */
    public static void cmdLineMain(String [] args) {
        ConfigurableApplicationContext ctx =
                new SpringApplicationBuilder(SpringBootApp.class)
                        .web(WebApplicationType.NONE)
                        .run(args);

        ctx.close();
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("Bean Runner Called");
        };
    }
}

/**
 * You can use this if you are writing a command line application and you want
 * to parse args.  Though it only offers certain types of help.
 * <p>
 * NoOptionArgs are any arguments that start without a '--'
 * Option args start with a '--'.  They can have names and
 * values.
 * e.g. run the program with the following args:
 * aNoOptionVar --a=b --a=c --otherOpt=otherVal
 * <p>
 * aNoOptionVar is a NoOptionArg (duh)
 * --a is an Option arg with a list of values[b, c]
 * --otherOpt is an Option arg with a list of 1 value [otherVal]
 */
@Component
class ApplicationCommandLineRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("ApplicationCommandLineRunner called:");
        List<String> noOptionArgs = args.getNonOptionArgs();
        System.out.println("NoOptionArgs");
        noOptionArgs.forEach(System.out::println);

        Set<String> optionNames = args.getOptionNames();
        System.out.println("Option Args");
        optionNames.forEach(optName -> {
            System.out.println("Name: " + optName + ", Values ...");
            args.getOptionValues(optName).forEach((value) -> System.out.println("\t" + value));
        });
    }

    public void addIndent(String s) {
        System.out.println("    " + s);
    }
}

/**
 * You can register an ApplicationContextInitializer, which will
 * get called *BEFORE* any beans are added to the context.
 */
@Component
class MyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("ApplicationContextInitializer called with: " + applicationContext);
        Arrays.stream(applicationContext.getBeanDefinitionNames())
                .forEach(System.out::println);

        System.out.println("Bean count At Initialization: " + applicationContext.getBeanDefinitionCount());
    }
}

/**
 * A SpringEvent Listener.  Can have separate Listener
 * methods which listen for specific events, based on the
 * argument of the method.
 */
@Component
class GeneralEventListener {
    private Logger logger = LoggerFactory.getLogger(getClass());


    //Listen for StudentCreatedEvent
    @EventListener
//    @Async
    public void handleStudentCreatedEvent(StudentCreatedEvent event) {
        logger.info("Handle Student Created: " + event + " in " + Thread.currentThread());
    }

    //Listen for ServletRequestHandledEvent, sent after a Controller method has
    //been finished processing a request.
    @EventListener
    public void handleServletRequestEvent(ServletRequestHandledEvent event) {
        logger.info("ServletRequest: " + event);
    }
}

/**
 * Or can create a class just to listen to a particular event.
 * E.g. in this case we are listening for only the ContextRefreshedEvent.
 */
@Component
class ContextRefreshedHandler implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("Context refreshed Event.  Beans:");
//        Arrays.stream(event.getApplicationContext().getBeanDefinitionNames())
//                        .forEach(System.out::println);

        System.out.println("Bean count: " + event.getApplicationContext().getBeanDefinitionCount());
    }
}

/**
 * A method with an argument of type ApplicationEvent would listen to all
 * events that Spring generates.
 */
//@Component
//class AllEventListener {
//    @EventListener
//    public void listenToAllEvents(ApplicationEvent event) {
//        System.out.println("Event happened: " + event);
//        if(event instanceof SpringApplicationEvent) {
//            System.out.println("SpringBoot event");
////            SpringApplicationEvent sae = (SpringApplicationEvent)event;
////            for(String arg: sae.getArgs()) {
////                System.out.println("arg: " + arg);
////            }
//            }
//        //Will only work with java 17.  Else you need an
          //old-fashioned caste and the @SuppressWarnings
//        if(event instanceof AvailabilityChangeEvent ace) {
////            @SuppressWarnings({"rawtypes", "unchecked"})
////            AvailabilityChangeEvent<AvailabilityState> ace = (AvailabilityChangeEvent) event;
//            System.out.println("AvailabilityChanged new State: " + ace.getState());
//        }
//
//    }
//}
