package ttl.larku.dao.jpahibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.ScheduledClass;

public class JPAClassDAO implements BaseDAO<ScheduledClass> {

    private Map<Integer, ScheduledClass> classes = new ConcurrentHashMap<Integer, ScheduledClass>();
    private AtomicInteger nextId = new AtomicInteger(1);

    private String from;

    public JPAClassDAO(String from) {
        this.from = from + ": ";
    }

    public JPAClassDAO() {
        this("JPA");
    }

    @Override
    public boolean update(ScheduledClass updateObject) {
        return classes.computeIfPresent(updateObject.getId(), (key, oldValue) -> updateObject) != null;
    }

    @Override
    public boolean delete(int id) {
        return classes.remove(id) != null;
    }

    @Override
    public boolean delete(ScheduledClass input) {
        return delete(input.getId());
    }

    @Override
    public ScheduledClass insert(ScheduledClass newObject) {
        //Create a new Id
        int newId = nextId.getAndIncrement();
        newObject.setId(newId);
        classes.put(newId, newObject);

        return newObject;
    }

    @Override
    public Optional<ScheduledClass> findById(int id) {
        return Optional.ofNullable(classes.get(id));
    }

    @Override
    public List<ScheduledClass> findAll() {
        return new ArrayList<ScheduledClass>(classes.values());
    }

    @Override
    public void deleteStore() {
        classes = null;
    }

    @Override
    public void createStore() {
        classes = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(1);
    }

    public void setClasses(Map<Integer, ScheduledClass> classes) {
        this.classes = classes;
    }
}
