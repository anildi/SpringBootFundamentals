package ttl.larku.reflect.inject;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

public class BeanFactoryComplex {
    /**
     * Create an instance of the given type and do dependency injection
     * on it.  In this case, we also recurse check and maybe do
     * depencency injection on the newly created object(s).
     *
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T getBean(Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T result = clazz.getConstructor().newInstance();

        doInjection(result);

        return result;
    }

    static final private Class<? extends Annotation> myInject = MyInject.class;

    public static void doInjection(Object target) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = target.getClass();

        Field[] fields = clazz.getDeclaredFields();

        //Look for our @MyInject annotation on all declared fields.
        for (Field field : fields) {
            if (field.isAnnotationPresent(myInject)) {
                //Get the class
                Class<?> targetType = field.getType();
                //If it is an interface, get an implementing
                //class.
                if (targetType.isInterface()) {
                    var listOfImpls = findImplementations(field, "ttl");
                    if(listOfImpls.size() > 0) {
                        targetType = listOfImpls.get(0);
                    } else {
                        throw new InstantiationException("No Beans of type: " + targetType + " found to inject into object of class: " + clazz);
                    }
                }
                //Create the new instance
                Object newInstance = targetType.getConstructor().newInstance();

                //and now do the actual "injection"
                field.setAccessible(true);
                field.set(target, newInstance);

                //Now look through your new created object and
                //possibly do injection on it.
                //This needs severe testing.
                doInjection(newInstance);
            }
        }
    }

    /**
     * Find all implementations for a given interface.
     * We are using the fabulous Reflections library for this.
     * We search either in the list of scanPackages you give us,
     * or the package of the Type of the given field.
     * <p>
     * We return a possibly empty List of implementing classes.
     * <p>
     * https://github.com/ronmamo/reflections
     *
     * @param field        the interface field we want to find an implementation for.
     * @param scanPackages optional list of packages to scan for.
     * @return List of Implementing classes.
     */
    public static List<Class<?>> findImplementations(Field field, String... scanPackages) {
        List<Class<?>> result = new ArrayList<>();
        Class<?> inputClass = field.getType();
        if (inputClass.isInterface()) {

//            Reflections reflections = new Reflections("ttl.larku");
            Reflections reflections;
            //If you give us packages to scan, we use those
            if(scanPackages.length > 0) {
                reflections = new Reflections(
                        new ConfigurationBuilder()
                                .forPackages(scanPackages)
                );
            }else {  //else we use the package that this class is from
                String packName = inputClass.getPackageName();
                System.out.println("class: " + inputClass + ", packName: " + packName);
                reflections = new Reflections(packName);
            }

            Set<Class<?>> subTypes =
                    reflections.get(SubTypes.of(inputClass).asClass());

            //Get the generic field Type to compare with in the for loop below.
            Type fieldGenericType = field.getGenericType();

            for (Class<?> cls : subTypes) {
                //The types of Interfaces implemented by the class
                Type[] types = cls.getGenericInterfaces();
//                System.out.println("cls: " + cls);
                for (Type type : types) {
//                    System.out.println("\ttype: " + type);
                    if (type.equals(fieldGenericType)) {
                        result.add(cls);
                        System.out.println("Found it!!!: cls: " + cls + ", type: " + type);
                    }
                }
            }
        }

        return result;
    }
}
