package ttl.larku;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SpringDBApp implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApp = new SpringApplication(SpringDBApp.class);

        springApp.run(args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Init completed");
    }
}

//@Component
//class SpringRestConfiguration implements RepositoryRestConfigurer {
//    @Override
//    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//        config.setRepositoryDetectionStrategy(RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED);
//    }
//}

