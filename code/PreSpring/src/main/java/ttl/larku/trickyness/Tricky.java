package ttl.larku.trickyness;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

interface Trick {
   public void doTrick();
}

@Component
//@Profile("profileA")
   @Qualifier("easy")
class Trick1 implements Trick {
   @Override
   public void doTrick() {
      System.out.println("Handstand");
   }
}

@Component
//@Profile("profileB")
@Qualifier("hard")
@Order(2)
class Trick2 implements Trick {
   @Override
   public void doTrick() {
      System.out.println("Somersault");
   }
}

@Component
//@Profile("profileB")
@Qualifier("hard")
@Order(1)
class Trick3 implements Trick {
   @Override
   public void doTrick() {
      System.out.println("Cartwheel");
   }
}

@Component
class Circus {

   @Autowired
   private List<Trick> allTricks;

   @Autowired
   @Qualifier("easy")
   private List<Trick> easyTricks;

   @Autowired
   @Qualifier("hard")
   private List<Trick> hardTricks;

//   @Autowired
   private Trick trick;

   public void startShow() {
      easyTricks.forEach(Trick::doTrick);
      hardTricks.forEach(Trick::doTrick);
   }

   public static void main(String[] args) {
      AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//      context.getEnvironment().setActiveProfiles("profileB");
      context.scan("ttl.larku.trickyness");
      context.refresh();

      Circus circus = context.getBean("circus", Circus.class);
      circus.startShow();
   }
}