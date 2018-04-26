package yellow.jogging.db.dao;

import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import yellow.jogging.beans.User;
import yellow.jogging.db.dao.exceptions.SessionCreationException;
import yellow.jogging.db.dao.exceptions.UnatharizedAccessException;
import yellow.jogging.db.dao.interfaces.DaoCommand;

public abstract class AuthorizedDao {

    @Autowired
    private SessionFactory sessionFactory;


    protected void workWithSession(DaoCommand command) throws UnatharizedAccessException, SessionCreationException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            Session session = sessionFactory.openSession();
            if (command != null) {
                command.call(user, session);
                session.close();
            } else {
                throw new SessionCreationException("Can't initialize session");
            }
        } else {
            throw new UnatharizedAccessException("Can't find stored user");
        }
    }


}
