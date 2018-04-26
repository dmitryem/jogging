package yellow.jogging.db.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yellow.jogging.beans.Jogging;
import yellow.jogging.beans.Statistic;
import yellow.jogging.beans.User;
import yellow.jogging.db.dao.exceptions.SessionCreationException;
import yellow.jogging.db.dao.exceptions.UnatharizedAccessException;

import org.hibernate.type.Type;

import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class JoggingDao extends AuthorizedDao {


    @Transactional
    public List<Jogging> getJoggingList(int offset,int perPage) throws UnatharizedAccessException, SessionCreationException {
        AtomicReference<List<Jogging>> list = new AtomicReference<>();
        workWithSession((user, session) -> {
            Criteria criteria = session.createCriteria(Jogging.class);
            criteria.createAlias("user", "user");
            criteria.add(Restrictions.eq("user.id", user.getId()));
            criteria.setMaxResults(perPage);
            criteria.setFirstResult(offset);
            list.set(criteria.list());
        });
        return list.get();
    }

    @Transactional
    public long getJoggingCount() throws UnatharizedAccessException, SessionCreationException {
        AtomicLong count = new AtomicLong();
        workWithSession((user, session) -> {
            Criteria criteria = session.createCriteria(Jogging.class);
            criteria.createAlias("user", "user");
            criteria.add(Restrictions.eq("user.id", user.getId()));
            criteria.setProjection(Projections.rowCount());
            count.set((long) criteria.uniqueResult());
        });
        return count.get();
    }


    @Transactional
    public List<Statistic> getJoggingListForPeriod(int offset, int perPage) throws UnatharizedAccessException, SessionCreationException {
        AtomicReference<List> list = new AtomicReference<>();
        workWithSession((user, session) -> {
            Criteria criteria = session.createCriteria(Jogging.class);
            ProjectionList projectionList = Projections.projectionList()
                    .add(Projections.sqlGroupProjection("week(jogging_date) as week",
                            "week",
                            new String[]{"week"},
                            new Type[]{IntegerType.INSTANCE}))
                    .add(Projections.sum("distance"))
                    .add(Projections.sum("duration"))
                    .add(Projections.avg("duration"));
            criteria.setProjection(projectionList);
            criteria.createAlias("user", "user");
            criteria.add(Restrictions.eq("user.id", user.getId()));
            criteria.setFirstResult(offset);
            criteria.setMaxResults(perPage);
            list.set(criteria.list());

        });
        List<Statistic> result = new ArrayList<>();
        for (Object[] item : (List<Object[]>) list.get()) {
            Statistic statistic = new Statistic();
            statistic.setWeekNumber((Integer) item[0]);
            statistic.setTotalDistance((Long) item[1]);
            statistic.setAvSpeed(Double.valueOf((Long) item[1]) / ((Long) item[2]));
            statistic.setAvTime((Double) item[3]);
            result.add(statistic);
        }
        return result;
    }

    public long getWeeksCount() throws UnatharizedAccessException, SessionCreationException {
        AtomicLong count = new AtomicLong();
        workWithSession((user, session) -> {
            Criteria criteria = session.createCriteria(Jogging.class);

            ProjectionList projectionList = Projections.projectionList()
                    .add(Projections.sqlProjection("count(distinct week(jogging_date)) as count",
                            new String[]{"count"},
                            new Type[]{IntegerType.INSTANCE}));
            criteria.setProjection(projectionList);
            criteria.createAlias("user", "user");
            criteria.add(Restrictions.eq("user.id", user.getId()));
            count.set((int)criteria.uniqueResult());
        });
        return count.get();
    }


    @Transactional
    public Jogging getJogging(int id) throws UnatharizedAccessException, SessionCreationException {
        AtomicReference<Jogging> result = new AtomicReference<>();
        workWithSession((user, session) -> {
            Criteria criteria = session.createCriteria(Jogging.class);
            criteria.createAlias("user", "user");
            criteria.add(Restrictions.eq("user.id", user.getId()));
            criteria.add(Restrictions.eq("id", id));
            result.set((Jogging) criteria.uniqueResult());
        });
        return result.get();

    }

    @Transactional
    public void createJogging(@NotNull Jogging jogging) throws UnatharizedAccessException, SessionCreationException {
        if (jogging != null) {
            workWithSession((user, session) -> {
                jogging.setUser(user);
                session.save(jogging);
            });
        }
    }

    @Transactional
    public boolean updateJogging(@NotNull Jogging jogging) throws UnatharizedAccessException, SessionCreationException {
        boolean updated = false;
        if (jogging != null) {
            Jogging dbJogging = getJogging(jogging.getId());
            if (dbJogging != null) {
                dbJogging.setDate(jogging.getDate());
                dbJogging.setDuration(jogging.getDuration());
                dbJogging.setDistance(jogging.getDistance());
                workWithSession((user, session) -> {
                    session.saveOrUpdate(dbJogging);
                    session.flush();
                });
                updated = true;
            } else {
                updated = false;
            }
        }
        return updated;
    }

    @Transactional
    public boolean deleteJogging(int id) throws UnatharizedAccessException, SessionCreationException {
        boolean deleted;
        Jogging dbJogging = getJogging(id);
        if (dbJogging != null) {
            workWithSession((user, session) -> {
                session.delete(dbJogging);
                session.flush();
            });
            deleted = true;
        } else {
            deleted = false;
        }
        return deleted;
    }
}
