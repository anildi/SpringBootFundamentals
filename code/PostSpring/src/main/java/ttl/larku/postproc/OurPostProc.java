package ttl.larku.postproc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class OurPostProc implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext context;

    /**
     * Called by the framework to give us a reference to the ApplicationContext
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;

    }

    /**
     * Called before after the instance has been created but before any
     * initialization methods have been called (with afterProperties set, or init-method)
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //System.out.println("OurPostProc.postProcessBeforeInitialization");
        return bean;
    }

    /**
     * Called after initialization methods have been called.  We use it to put into action
     * our @OurInject annotation.
     * <p>
     * Here we make an attempt to take Generic Type information into account.  But it is a
     * very weak attempt
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //System.out.println("OurPostProc.postProcessAfterInitialization");

        Class<?> clazz = bean.getClass();

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(OurInject.class) && method.getName().startsWith("set")
                    && method.getParameterCount() == 1) {
                Class<?> argType = method.getParameterTypes()[0];
                //Get Generic Type Information for this class
                Type[] genericTypes = method.getGenericParameterTypes();
                dumpTypeInfo(genericTypes);
                //Get the parameterized type which will be something like
                //ttl.larku.dao.BaseDAO<ttl.larku.domain.Student>
                ParameterizedType typeParam = getGenericParameterType(genericTypes);

                //Get Beans of type of the argument, which will
                //be *all* beans that implement BaseDAO.  We have
                //to then do further processing below to get only
                //those that implement BaseDAO<Student>
                String[] names = context.getBeanNamesForType(argType);

                List<Object> candidates = new ArrayList<>();
                //Now we need to examine the interfaces implemented by these beans.
                for (String name : names) {
                    Object foundBean = context.getBean(name, argType);
                    Class<?> foundClass = foundBean.getClass();
                    //Generic Interfaces implemented by the Class.
                    Type[] genericInterfaces = foundClass.getGenericInterfaces();
                    Type foundTypeParam = getGenericParameterType(genericInterfaces);

                    if (foundTypeParam.equals(typeParam)) {
                        System.out.println("Found one: " + name);
                        candidates.add(foundBean);
                    } else {
                        System.out.println("not this one: " + name);
                    }
                }

                if (candidates.size() > 0) {
                    System.out.println("Warning, " + candidates.size() + " beans found, expected 1. Using first one: " + candidates.get(0));
                }
                Object arg = candidates.get(0);
                try {
                    method.invoke(bean, arg);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    /**
     * This one does not take into account Generics.  It is searching only by type of the
     * field, which is BaseDAO.  So getBeansForType will return all beans implementing
     * BaseDAO, which includes the InMemoryCourseDAO.  Not good.  So it works just because
     * the first DAO in the list happens to be an InMemoryStudentDAO.
     * <p>
     * What we want to look for are classes that implement BaseDAO<Student>.  This requires
     * more work, as shown in the function above.
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessAfterInitializationSimple(Object bean, String beanName) throws BeansException {
        //System.out.println("OurPostProc.postProcessAfterInitialization");

        Class<?> clazz = bean.getClass();

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(OurInject.class) && method.getName().startsWith("set")
                    && method.getParameterCount() == 1) {
                Class<?> argType = method.getParameterTypes()[0];
                String[] names = context.getBeanNamesForType(argType);
//               for(String name : names) {
//                   System.out.println("name is " + name);
//               }
                Object arg = context.getBean(names[0], argType);
                try {
                    method.invoke(bean, arg);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }


    /**
     * Extract the GenericParameterType from an array of types.
     * Very elementary, we just return the first ParameterizedType we find,
     * or null.
     *
     * @param types
     * @return
     */
    private ParameterizedType getGenericParameterType(Type[] types) {
        for (Type t : types) {
            if (t instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) t;
                return pt;
            }
        }
        return null;
    }

    /**
     * Example of how to dump out Generic Type Information using
     * Reflection.
     *
     * @param types
     */
    private static void dumpTypeInfo(Type[] types) {

        for (Type t : types) {
            System.out.println("\tGen Param: " + t);
            if (t instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) t;
                Type[] actualTypes = pt.getActualTypeArguments();
                for (Type actualType : actualTypes) {
                    System.out.println("\t\tActual Param: " + actualType);
                    if (actualType instanceof TypeVariable) {
                        TypeVariable<?> wt = (TypeVariable<?>) actualType;
                        System.out.println("\t\t\tTypeVariable: " + wt);
                        Type[] bounds = wt.getBounds();
                        for (Type tt : bounds) {
                            System.out.println("\t\t\t\tBounds: " + tt);
                        }
                    }
                    if (actualType instanceof WildcardType) {
                        WildcardType wt = (WildcardType) actualType;
                        Type[] upperBounds = wt.getUpperBounds();
                        for (Type tt : upperBounds) {
                            System.out.println("\t\t\tUpper Bounds: " + tt);
                        }
                        Type[] lowerBounds = wt.getLowerBounds();
                        for (Type tt : lowerBounds) {
                            System.out.println("\t\t\tLower Bounds: " + tt);
                        }
                    }
                }
            }
        }
    }
}
