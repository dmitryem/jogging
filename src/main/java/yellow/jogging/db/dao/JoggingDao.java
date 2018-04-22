package yellow.jogging.db.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yellow.jogging.beans.Jogging;
import yellow.jogging.beans.User;

import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.util.Date;
import java.util.List;

@Repository
public class JoggingDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public List<Jogging> getJoggingList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Jogging.class);
        criteria.createAlias("user", "user");
        criteria.add(Restrictions.eq("user.id", user.getId()));
        List<Jogging> list = (List<Jogging>) criteria.list();
        session.close();
        return list;
    }

    @Transactional
    public List<Jogging> getJoggingListForPeriod(Date dateFrom, Date dateTo) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Jogging.class);
        criteria.createAlias("user", "user");
        criteria.add(Restrictions.eq("user.id", user.getId()));
        criteria.add(Restrictions.between("date",dateFrom,dateTo));
        List<Jogging> list = (List<Jogging>) criteria.list();
        session.close();
        return list;
    }

    @Transactional
    public Jogging getJogging(int id) {
        Jogging result;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Jogging.class);
        criteria.createAlias("user", "user");
        criteria.add(Restrictions.eq("user.id", user.getId()));
        criteria.add(Restrictions.eq("id", id));
        result = (Jogging) criteria.uniqueResult();
        session.close();
        return result;

    }

    @Transactional
    public void createJogging(@NotNull Jogging jogging) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        jogging.setUser(user);
        Session session = sessionFactory.openSession();
        session.save(jogging);
        session.close();
    }

    @Transactional
    public boolean updateJogging(@NotNull Jogging jogging) {
        boolean updated;
        Jogging dbJogging = getJogging(jogging.getId());
        if (dbJogging != null) {
            dbJogging.setDate(jogging.getDate());
            dbJogging.setDuration(jogging.getDuration());
            dbJogging.setDistance(jogging.getDistance());
            Session session = sessionFactory.openSession();
            session.saveOrUpdate(dbJogging);
            session.flush();
            session.close();
            updated = true;
        } else {
            updated = false;
        }
        return updated;
    }

    @Transactional
    public boolean deleteJogging(int id) {
        boolean deleted;
        Jogging dbJogging = getJogging(id);
        if (dbJogging != null) {
            Session session = sessionFactory.openSession();
            session.delete(dbJogging);
            session.flush();
            session.close();
            deleted = true;
        }else{
            deleted = false;
        }
        return deleted;
    }
}
