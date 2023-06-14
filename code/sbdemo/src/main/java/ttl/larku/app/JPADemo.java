package ttl.larku.app;

import ttl.larku.domain.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author whynot
 */
public class JPADemo {

    private EntityManagerFactory factory;

    public static void main(String[] args) {
        JPADemo jpaDemo = new JPADemo();
//        jpaDemo.addStudent();
        jpaDemo.updateStudent();
        jpaDemo.dumpStudents();

    }

    public JPADemo() {
        factory = Persistence.createEntityManagerFactory("LarkUPU_SE");
    }

    public void dumpStudents() {
        EntityManager manager = factory.createEntityManager();

        TypedQuery<Student> query = manager.createQuery("Select s from Student s", Student.class);

        List<Student> students = query.getResultList();

        students.forEach(System.out::println);
    }

    public void addStudent() {
        Student student = new Student("Furdy", "383 9393 9393", Student.Status.HIBERNATING);

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        manager.persist(student);

        manager.getTransaction().commit();
    }

    public void updateStudent() {
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        Student oldFurdy = manager.find(Student.class, 27);

        oldFurdy.setName("Other Furdy");

        manager.getTransaction().commit();

    }

}
