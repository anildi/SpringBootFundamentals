package ttl.larku.didemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public interface TrickInterface {
    public void doTrick();
}

@Component
@Qualifier("us-east")
class Tricky1 implements TrickInterface
{
    @Override
    public void doTrick() {
        System.out.println("Handstand");
    }
}

@Component
//@Qualifier("us-west")
class Tricky2 implements TrickInterface
{
    @Override
    public void doTrick() {
        System.out.println("Juggling");
    }
}

@Service
class Circus
{
//    @Resource(name = "tricky2")

    @Autowired
    //@Qualifier("us-west")
    private TrickInterface tricky;

    public void startShow() {
        tricky.doTrick();
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("ttl.larku.didemo");
        context.refresh();

        Circus c = context.getBean("circus", Circus.class);
        c.startShow();
    }
}
