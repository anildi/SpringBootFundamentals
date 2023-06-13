package ttl.larku.tricky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author whynot
 */
interface Trick {
    public void doTrick();
}

@Component
//@Primary
//@Profile("us-east")
@Qualifier("asiapacific")
class Trick1 implements Trick {
    @Override
    public void doTrick() {
        System.out.println("Handstand");
    }
}

@Component
//@Profile("us-west")
@Qualifier("europe")
class Trick2 implements Trick {
    @Override
    public void doTrick() {
        System.out.println("Cartwheel");
    }
}

@Component
//@Profile("us-west")
@Qualifier("europe")
class Trick3 implements Trick {
    @Override
    public void doTrick() {
        System.out.println("Cartwheel");
    }
}


@Configuration
@ComponentScan("ttl.larku.tricky")
class MyConfig
{
    @Bean
    public List<Trick> myTrick() {
        return List.of(new Trick1());
    }
}

@Component
class Circus
{
    @Autowired
//    @Qualifier("trick2")
    private Trick trick2;

    @Autowired
    @Qualifier("europe")
    private List<Trick> europeTricks;

    @Autowired
    @Qualifier("asiapacific")
    private List<Trick> apacTricks;

    public void startShow() {
        apacTricks.forEach(Trick::doTrick);
//        trick2.doTrick();
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
//        context.getEnvironment().setActiveProfiles("us-west");
//        context.scan("ttl.larku.tricky");

//        context.refresh();

        Circus circus = context.getBean("circus", Circus.class);

        circus.startShow();
    }
}