package ttl.larku.trickyness;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

interface Trick {
   public void doTrick();
}

@Component
//@Primary
//@Profile("dev")
@Qualifier("us_west")
class Trick1 implements Trick {

   @Override
   public void doTrick() {
      System.out.println("Handstand");
   }
}

@Component
@Qualifier("us_east")
@Profile("prod")
class Trick2 implements Trick {

   @Override
   public void doTrick() {
      System.out.println("Somersault");
   }
}

@Component
@Qualifier("us_east")
@Profile("prod")
class Trick3 implements Trick {

   @Override
   public void doTrick() {
      System.out.println("Cart Wheel");
   }
}

@Component
class Circus
{
   @Autowired
   @Qualifier("us_west")
   private Trick trick;

   @Autowired
   @Qualifier("us_east")
   private List<Trick> tricks;

   public void startShow() {
//      trick.doTrick();
      tricks.forEach(Trick::doTrick);
   }

   public static void main(String[] args) {
      AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
      context.getEnvironment().setActiveProfiles("prod");
      context.scan("ttl.larku.trickyness");
      context.refresh();

      Circus circus = context.getBean("circus", Circus.class);
      circus.startShow();
   }
}
