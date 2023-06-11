package ttl.larku.didemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

interface TrickInterface {
    public void doTrick();
}

@Component
@Qualifier("us-east")
//@Profile("us-east")
@Order(2)
class Tricky1 implements TrickInterface
{
    @Override
    public void doTrick() {
        System.out.println("Handstand");
    }
}

@Component
//@Primary
@Qualifier("us-west")
//@Profile("us-west")
@Order(200000)
class Tricky2 implements TrickInterface
{
    @Override
    public void doTrick() {
        System.out.println("Juggling");
    }
}

@Component
//@Primary
@Qualifier("us-west")
//@Profile("us-west")
@Order(1)
class Tricky3 implements TrickInterface
{
    @Override
    public void doTrick() {
        System.out.println("Card Trick");
    }
}

@Component
class Circus
{
    @Autowired
    @Qualifier("us-east")
//    @Resource(name = "tricky2")
    private TrickInterface tricky2;

    @Autowired
//    @Qualifier("us-west")
//    private TrickInterface[] westTricks;
    private List<TrickInterface> westTricks;

    public void startShow() {
        for(TrickInterface trick : westTricks) {
            trick.doTrick();
        }
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("us-east");
        context.scan("ttl.larku.didemo");
        context.refresh();

        Circus circus = context.getBean("circus", Circus.class);

        circus.startShow();
    }
}
