package ttl.larku.dao.jpahibernate;


import org.springframework.transaction.annotation.Transactional;
import ttl.larku.dao.BaseDAO;
import ttl.larku.domain.Track;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class JPATrackDAO implements BaseDAO<Track> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean update(Track updateObject) {
        em.merge(updateObject);
        return true;
    }

    @Override
    public boolean delete(Track deleteObject) {
        em.remove(deleteObject);
        return true;
    }

    @Override
    public Track create(Track newObject) {
        em.persist(newObject);
        return newObject;
    }

    @Override
    public Track get(int id) {
        return em.find(Track.class, id);
    }

    @Override
    public List<Track> getAll() {
        TypedQuery<Track> query = em.createQuery("Select t from Track t", Track.class);
        List<Track> tracks = query.getResultList();
        return tracks;
    }

    @Override
    public void deleteStore() {
        Query query = em.createQuery("Delete from Track");
        query.executeUpdate();
    }

    @Override
    public void createStore() {
    }

    @Override
    public List<Track> getTracksByTitle(String title) {
        TypedQuery<Track> query = em.createNamedQuery("Track.getByTitle", Track.class);
        query.setParameter("title", "%" + title + "%");
        List<Track> result = query.getResultList();
        return result;
    }

    /**
     * SQL Injectable.
     * This should only let you select by title.
     * To make it break, try it with <"h2-Leave It to Me' or title like '%_%">
     * which should give you everything where title == anything.
     *
     * (See the test)
     * @param title The title to search for.
     * @return List of Tracks matching title.
     */
    public List<Track> getTracksByTitleInjectable(String title) {
        //String jpaql = "select t from Track t where t.title like '%" + title + "%'";
        String jpaql = "select t from Track t where t.title = '" + title + "'";
        System.out.println("getTracksByTitleBad jpaql: " + jpaql);
        TypedQuery<Track> query = em.createQuery(jpaql, Track.class);
        List<Track> result = query.getResultList();
        return result;
    }

    /**
     * Use the Criteria API to create a query dynamically.
     * Which is still ugly, but better.
     *
     * @param example
     * @return
     */
    @Override
    public List<Track> getByCriteriaWithLike(Track example) {

        //Get the builder
        CriteriaBuilder builder = em.getCriteriaBuilder();
        //Create a query that will return Tracks
        CriteriaQuery<Track> cq = builder.createQuery(Track.class);

        //Tracks is also going to be the (only) root entity we will
        //be searching from.  This need not always be the same as the
        //type returned from the query.  This is the 'From' clause.
        Root<Track> queryRoot = cq.from(Track.class);

        //We are going to be selecting Tracks.
        cq.select(queryRoot).distinct(true);

        //Build up a List of javax.persistence.criteria.Predicate objects,
        //based on what is not null in the example Student.
        List<Predicate> preds = new ArrayList<>();
        if (example.getTitle() != null) {
            //we are doing a 'like' comparison, with the lower case entity title to lower case example title.
            preds.add(builder.like(builder.lower(queryRoot.get("title")), "%" + example.getTitle().toLowerCase() + "%"));
        }
        if (example.getArtist() != null) {
            preds.add(builder.like(builder.lower(queryRoot.get("artist")), "%" + example.getArtist().toLowerCase() + "%"));
        }
        if (example.getAlbum() != null) {
            preds.add(builder.like(builder.lower(queryRoot.get("album")), "%" + example.getAlbum().toLowerCase() + "%"));
        }

        //Now 'or' them together.
        Predicate finalPred = builder.or(preds.toArray(new javax.persistence.criteria.Predicate[0]));

        //And set them as the where clause of our query.
        cq.where(finalPred);

        TypedQuery<Track> query = em.createQuery(cq);

        List<Track> result = query.getResultList();

//        String sql = getCriteriaQuerySql(query);
//
//        System.out.println("Criteria query: " + sql);

        return result;
    }

    public List<Track> getByCriteriaWithEquals(Track example) {

        //Get the builder
        CriteriaBuilder builder = em.getCriteriaBuilder();
        //Create a query that will return Tracks
        CriteriaQuery<Track> cq = builder.createQuery(Track.class);

        //Tracks is also going to be the (only) root entity we will
        //be searching from.  This need not always be the same as the
        //type returned from the query.  This is the 'From' clause.
        Root<Track> queryRoot = cq.from(Track.class);

        //We are going to be selecting Tracks.
        cq.select(queryRoot).distinct(true);

        //Build up a List of javax.persistence.criteria.Predicate objects,
        //based on what is not null in the example Student.
        List<Predicate> preds = new ArrayList<>();
        if (example.getTitle() != null) {
            //we are doing a 'like' comparison, with the lower case entity title to lower case example title.
            preds.add(builder.equal(builder.lower(queryRoot.get("title")),
                    example.getTitle().toLowerCase()));
        }
        if (example.getArtist() != null) {
            preds.add(builder.equal(builder.lower(queryRoot.get("artist")),
                    example.getArtist().toLowerCase()));
        }
        if (example.getAlbum() != null) {
            preds.add(builder.equal(builder.lower(queryRoot.get("album")),
                    example.getAlbum().toLowerCase()));
        }

        //Now 'or' them together.
        Predicate finalPred = builder.or(preds.toArray(new javax.persistence.criteria.Predicate[0]));

        //And set them as the where clause of our query.
        cq.where(finalPred);

        TypedQuery<Track> query = em.createQuery(cq);

        List<Track> result = query.getResultList();

        //Hibernate specific way to dump out sql.
        //Logging with debug level will also give you the sql.
        String sql = query.unwrap(org.hibernate.query.Query.class).getQueryString();
        System.out.println("Unwrapped Criteria Query: " + sql);

        return result;
    }

    /**
     * Bad alternative way to do this, because we are creating our
     * sql query by concatenating strings, which can leave us
     * open to sql injection attacks.
     * @param example
     * @return
     */
    public List<Track> getByExampleBadWithLike(Track example) {
        String rootQuery = "select t from Track t where ";
        StringBuilder builder = new StringBuilder(rootQuery);
        StringBuilder qb = new StringBuilder();
        if (example.getTitle() != null) {
            //we are doing a 'like' comparison, with the lower case entity title to lower case example title.
            qb.append(qb.length() == 0 ? " " : " or ");
            qb.append(" LOWER(t.title) like '%").append(example.getTitle().toLowerCase()).append("%'");
        }
        if (example.getArtist() != null) {
            qb.append(qb.length() == 0 ? " " : " or ");
            qb.append(" LOWER(t.artist) like '%").append(example.getArtist().toLowerCase()).append("%'");
        }
        if (example.getAlbum() != null) {
            qb.append(qb.length() == 0 ? " " : " or ");
            qb.append(" LOWER(t.album) like '%").append(example.getAlbum().toLowerCase()).append("%'");
        }
        String finalQuery = builder.append(qb).toString();
        System.out.println("finalQuery: " + finalQuery);

        TypedQuery<Track> query = em.createQuery(finalQuery, Track.class);
        List<Track> result = query.getResultList();

        return result;
    }

    public List<Track> getByExampleBadWithEqual(Track example) {
        String rootQuery = "select t from Track t where ";
        StringBuilder builder = new StringBuilder(rootQuery);
        StringBuilder qb = new StringBuilder();
        if (example.getTitle() != null) {
            //we are doing a 'like' comparison, with the lower case entity title to lower case example title.
            qb.append(qb.length() == 0 ? " " : " or ");
            qb.append(" LOWER(t.title) = '").append(example.getTitle().toLowerCase()).append("'");
        }
        if (example.getArtist() != null) {
            qb.append(qb.length() == 0 ? " " : " or ");
            qb.append(" LOWER(t.artist) = '").append(example.getArtist().toLowerCase()).append("'");
        }
        if (example.getAlbum() != null) {
            qb.append(qb.length() == 0 ? " " : " or ");
            qb.append(" LOWER(t.album) = '").append(example.getAlbum().toLowerCase()).append("'");
        }
        String finalQuery = builder.append(qb).toString();
        System.out.println("finalQuery: " + finalQuery);

        TypedQuery<Track> query = em.createQuery(finalQuery, Track.class);
        List<Track> result = query.getResultList();

        return result;
    }
}
