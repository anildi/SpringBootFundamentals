package ttl.larku.db;

import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;
import ttl.larku.domain.Student;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DBOperations {

    //@PersistenceContext(unitName="LarkUPU_SE")
    private EntityManager em;
    private EntityManagerFactory emf;

    public static void main(String[] args) {
        DBOperations dbOperations = new DBOperations();

        //dbOperations.dropData();

//        dbOperations.createData();

//        dbOperations.play();
        dbOperations.printStudents();

        dbOperations.close();

    }

    public DBOperations() {

        emf = Persistence.createEntityManagerFactory("LarkUPU_SE");

        em = emf.createEntityManager();
    }


    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void close() {
        if (emf != null) {
            em.close();
            emf.close();
        }
    }

    public void play() {
        em.getTransaction().begin();
        Student student = new Student("Joseph");
        student.setPhoneNumber("222 383 3838");
        student.setStatus(Student.Status.FULL_TIME);
        em.persist(student);
        em.getTransaction().commit();
    }

    private DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    //public void createData(EntityManager em) {
    public void createData() {

        em.getTransaction().begin();
        Student student = new Student("Manoj", "222 383 3838", Student.Status.FULL_TIME);

        em.persist(student);

        student = new Student("Ana", "333 3839292", Student.Status.FULL_TIME);
        em.persist(student);

        student = new Student("Roberta", "382 4849292", Student.Status.HIBERNATING);
        em.persist(student);

        student = new Student("Madhu", "382 884 9993", Student.Status.PART_TIME);
        em.persist(student);

        Course course1 = new Course("MATH-101", "Introduction to Math");
        course1.setCredits(2f);
        em.persist(course1);

        Course course2 = new Course("MATH-102", "Yet more Math");
        course2.setCredits(3f);
        em.persist(course2);

        Course course3 = new Course("BKTWV-302", "Advanced Basket Weaving");
        course3.setCredits(1.5f);
        em.persist(course3);


        ScheduledClass sc1 = new ScheduledClass(course1, "8/10/2013", "3/27/2014");
        em.persist(sc1);

        ScheduledClass sc2 = new ScheduledClass(course2, "8/10/2013", "3/27/2014");
        em.persist(sc2);

        ScheduledClass sc3 = new ScheduledClass(course3, "8/10/2013", "3/27/2014");
        em.persist(sc3);

        em.getTransaction().commit();
    }

    //public void printStudents(EntityManager em) {
    public void printStudents() {
        TypedQuery<Student> query = em.createQuery("Select s from Student s", Student.class);

        List<Student> students = query.getResultList();

        System.out.println("Students:");
        for (Student s : students) {
            System.out.println(s);
        }
    }

    public void dropData() {

        em.getTransaction().begin();

        Query query = em.createNativeQuery("delete from Student_ScheduledClass");
        query.executeUpdate();

        query = em.createQuery("delete from ScheduledClass");
        query.executeUpdate();

        query = em.createQuery("delete from Course");
        query.executeUpdate();

        query = em.createQuery("delete from Student");
        query.executeUpdate();

        em.getTransaction().commit();

    }
}
