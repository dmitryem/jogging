package yellow.jogging.db.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
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
        User person = new User();
        person.setUsername(username);
        User user;
        Session session = sessionFactory.openSession();
        Example personExample = Example.create(person);
        Criteria criteria = session.createCriteria(User.class).add(personExample);
        user = (User) criteria.uniqueResult();
        session.close();
        return user;
    }


    @Transactional
    public void createUser(User user){
        Session session = sessionFactory.openSession();
        session.save(user);
        session.close();
    }


}
