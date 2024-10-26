package ttl.larku.reflect.inject;

import java.lang.reflect.InvocationTargetException;

/**
 * @author whynot
 */
public class InjectApp {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        SomeController sc = BeanFactory.getBean(SomeController.class);

        sc.doStuff();
    }
}
