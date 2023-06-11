package ttl.larku.dao.jpahibernate;

import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.ScheduledClass;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Transactional
@Profile("production")
public class JPAClassDAO implements BaseDAO<ScheduledClass> {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean update(ScheduledClass updateObject) {
        entityManager.merge(updateObject);
        return true;
    }

    @Override
    public boolean delete(ScheduledClass objToDelete) {
        ScheduledClass managed = entityManager.find(ScheduledClass.class, objToDelete.getId());
        entityManager.remove(managed);
        return true;
    }

    @Override
    public ScheduledClass create(ScheduledClass newObject) {
        entityManager.persist(newObject);
        return newObject;
    }

    @Override
    public ScheduledClass get(int id) {
        return entityManager.find(ScheduledClass.class, id);
    }

    @Override
    public List<ScheduledClass> getAll() {
        TypedQuery<ScheduledClass> query = entityManager.
                createQuery("Select s from ScheduledClass s", ScheduledClass.class);
        List<ScheduledClass> classes = query.getResultList();
        return classes;
    }

    public List<ScheduledClass> getByCourseCode(String courseCode) {
        TypedQuery<ScheduledClass> query = entityManager.
                createNamedQuery("SC.getByCourseCode", ScheduledClass.class);
        query.setParameter("code", courseCode);

        List<ScheduledClass> classes = query.getResultList();
        return classes;
    }
    
    public List<ScheduledClass> getByCourseCodeAndStartDate(String courseCode, LocalDate startDate) {
        TypedQuery<ScheduledClass> query = entityManager.
                createNamedQuery("SC.noStudents", ScheduledClass.class);
        query.setParameter("code", courseCode);
        query.setParameter("startDate", startDate);

        List<ScheduledClass> classes = query.getResultList();
        return classes;
    }

    public List<ScheduledClass> getByCourseCodeAndStartDateForStudents(String courseCode, LocalDate startDate) {
        TypedQuery<ScheduledClass> query = entityManager.
                createNamedQuery("SC.withStudents", ScheduledClass.class);
        query.setParameter("code", courseCode);
        query.setParameter("startDate", startDate);

        List<ScheduledClass> classes = query.getResultList();
        return classes;
    }

    @Override
    public void deleteStore() {
        Query query = entityManager.createQuery("Delete from ScheduledClass");
        query.executeUpdate();
    }

    @Override
    public void createStore() {
    }
}
