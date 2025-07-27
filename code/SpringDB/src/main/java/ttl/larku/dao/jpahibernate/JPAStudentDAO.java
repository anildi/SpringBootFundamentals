package ttl.larku.dao.jpahibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Student;

@Transactional
@Profile("production")
public class JPAStudentDAO implements BaseDAO<Student> {

    @PersistenceContext
    private EntityManager entityManager;


    public JPAStudentDAO() {
        int i = 0;
    }


    @Override
    public boolean update(Student updateObject) {
        entityManager.merge(updateObject);
        //errors will throw Exception.  See javadoc for merge.
        return true;
    }

    @Override
    public boolean delete(Student objToDelete) {
        Student managed = entityManager.find(Student.class, objToDelete.getId());
//        //errors will throw Exception.  See javadoc for remove.
        entityManager.remove(managed);

        //If you don't make it managed before deleting, then you
        //can get a "Removing a detached instance" Exception if
        //the given student is not managed.
//        entityManager.remove(objToDelete);
        return true;
    }

    @Override
    public Student insert(Student newObject) {
        entityManager.persist(newObject);
        return newObject;
    }

    @Override
    public boolean exists(int id) {
        TypedQuery<Boolean> query = entityManager.createNamedQuery("Student.exists", Boolean.class);
        query.setParameter("id", id);

        boolean result = query.getSingleResult();

        return result;
    }

    @Override
    public Student findById(int id) {
        TypedQuery<Student> query = entityManager.createNamedQuery("Student.bigSelectOne", Student.class);
        query.setParameter("id", id);
        List<Student> tmp = query.getResultList();
        Student student = tmp.size() > 0 ? tmp.get(0) : null;
        return student;
    }

    public List<Student> getAllForLIE() {

//        TypedQuery<Student> query = entityManager.createQuery("Select s from Student s", Student.class);
//        TypedQuery<Student> query = entityManager.createNamedQuery("Student.smallSelect", Student.class);
        TypedQuery<Student> query = entityManager.createNamedQuery("Student.smallSelect", Student.class);
        List<Student> students = query.getResultList();
        return students;
    }

    @Override
    public List<Student> findAll() {

//        TypedQuery<Student> query = entityManager.createQuery("Select s from Student s", Student.class);
//        TypedQuery<Student> query = entityManager.createNamedQuery("Student.smallSelect", Student.class);
        TypedQuery<Student> query = entityManager.createNamedQuery("Student.bigSelect", Student.class);
        List<Student> students = query.getResultList();
        return students;
    }

    public List<Student> getAllWithCourses() {

//        TypedQuery<Student> query = entityManager.createQuery("Select s from Student s", Student.class);
        TypedQuery<Student> query = entityManager.createNamedQuery("Student.bigSelect", Student.class);
//        TypedQuery<Student> query = entityManager.createNamedQuery("Student.bigSelect", Student.class);
        List<Student> students = query.getResultList();
        return students;
    }

    public List<Student> getByName(String name) {
        TypedQuery<Student> query = entityManager.createNamedQuery("Student.getByName", Student.class);
        query.setParameter("name", name);
        List<Student> students = query.getResultList();
        return students;
    }

    @Override
    public void deleteStore() {
        Query query = entityManager.createQuery("Delete from Student");
        query.executeUpdate();
    }

    @Override
    public void createStore() {
    }

}
