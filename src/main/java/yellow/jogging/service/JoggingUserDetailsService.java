package yellow.jogging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yellow.jogging.db.dao.UserDao;

@Service("userDetailsService")
public class JoggingUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public JoggingUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userDao.getUser(s);
    }
}
