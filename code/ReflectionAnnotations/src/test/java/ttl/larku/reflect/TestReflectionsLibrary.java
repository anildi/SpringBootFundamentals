package ttl.larku.reflect;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;
import ttl.larku.service.StudentService;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

/**
 * @author whynot
 */
public class TestReflectionsLibrary {

    private BaseDAO<Student> dao;

    private List<List<String>> multiList;

    private StudentService studentService;

    @Test
    public void testOurFields() throws NoSuchFieldException {
        Reflections reflections = new Reflections("ttl.larku");

        Set<Class<?>> subTypes =
                reflections.get(SubTypes.of(BaseDAO.class).asClass());

        Field f = this.getClass().getDeclaredField("dao");
        Type fieldGenericType = f.getGenericType();
        System.out.println("Field generic type: " + fieldGenericType
                + "typeClass: " + fieldGenericType.getClass().getName());

        Field ssField = this.getClass().getDeclaredField("studentService");
        Type ssFieldGenericType = ssField.getGenericType();
        System.out.println("ssField generic type: " + ssFieldGenericType
                + "typeClass: " + ssFieldGenericType.getClass().getName());

        Field multiField = this.getClass().getDeclaredField("multiList");
        Type multiFieldGenericType = multiField.getGenericType();
        System.out.println("multiField generic type: " + multiFieldGenericType
                + "typeClass: " + multiFieldGenericType.getClass().getName());

    }

    @Test
    public void testGetSubTypes() throws NoSuchFieldException {
        Reflections reflections = new Reflections("ttl.larku");

        Set<Class<?>> subTypes =
                reflections.get(SubTypes.of(BaseDAO.class).asClass());

        Field f = this.getClass().getDeclaredField("dao");
        Type fieldGenericType = f.getGenericType();
        System.out.println("Field generic type: " + fieldGenericType
                + "typeClass: " + fieldGenericType.getClass().getName());

        Field ssField = this.getClass().getDeclaredField("studentService");
        Type ssFieldGenericType = ssField.getGenericType();
        System.out.println("ssField generic type: " + ssFieldGenericType
                + "typeClass: " + ssFieldGenericType.getClass().getName());

        for(Class<?> cls : subTypes) {
            Type [] types = cls.getGenericInterfaces();
            //System.out.println("cls: " + cls + ", type: ");
            for(Type type : types) {
//                System.out.println("type: " + type);

                if(type.equals(fieldGenericType)) {
                    System.out.println("Found it!!!: cls: " + cls + ", type: " + type);
                }
            }
        }
    }
}
