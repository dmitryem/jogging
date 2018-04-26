package yellow.jogging.db.dao.interfaces;

import org.hibernate.Session;
import yellow.jogging.beans.User;

public interface DaoCommand {
    void call(User user, Session session);
}
