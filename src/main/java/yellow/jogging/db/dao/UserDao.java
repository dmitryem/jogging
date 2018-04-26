package yellow.jogging.db.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yellow.jogging.beans.User;


@Repository
public class UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public User getUser(String username) {
        User user = null;
        if( username != null && !username.trim().isEmpty()) {
            Session session = sessionFactory.openSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("username", username));
            user = (User) criteria.uniqueResult();
            session.close();
        }
        return user;
    }


    @Transactional
    public void createUser(User user){
        if(user != null) {
            Session session = sessionFactory.openSession();
            session.save(user);
            session.close();
        }
    }
}
