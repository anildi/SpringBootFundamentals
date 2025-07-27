package ttl.larku.dao.jpahibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.CourseDAO;
import ttl.larku.domain.Course;

@Transactional
@Profile("production")
public class JPACourseDAO implements CourseDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean update(Course updateObject) {
        entityManager.merge(updateObject);
        return true;
    }

    @Override
    public boolean delete(Course objToDelete) {
        Course managed = entityManager.find(Course.class, objToDelete.getId());
        entityManager.remove(managed);
        return true;
    }

    @Override
    public Course insert(Course newObject) {
        entityManager.persist(newObject);
        return newObject;
    }

    @Override
    public Course findById(int id) {
        return entityManager.find(Course.class, id);
    }

    @Override
    public List<Course> findAll() {
        TypedQuery<Course> query = entityManager.createNamedQuery("Course.getAll", Course.class);
        List<Course> courses = cast(query.getResultList(), Course.class);
        return courses;
    }

    @Override
    public Course findByCode(String code) {
        TypedQuery<Course> query = entityManager.createNamedQuery("Course.getByCode", Course.class);
        query.setParameter("code", code);

        try {
            Course course = query.getSingleResult();
            return course;
        }catch(NoResultException e) {
            return null;
        }

    }

    @Override
    public void deleteStore() {
        Query query = entityManager.createQuery("Delete from Course");
        query.executeUpdate();
    }

    @Override
    public void createStore() {
    }

    /**
     * Cast all elements of input List to given class
     *
     * @param list
     * @param cls
     * @param <T>
     * @return
     */
    private <T> List<T> cast(List<?> list, Class<T> cls) {
        List<T> result = new ArrayList<T>();
        for (Object o : list) {
            result.add(cls.cast(o));
        }

        return result;
    }
}
