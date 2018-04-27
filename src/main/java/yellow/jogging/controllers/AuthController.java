package yellow.jogging.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import yellow.jogging.beans.User;
import yellow.jogging.db.dao.UserDao;
import yellow.jogging.security.jwt.JwtUtil;

import java.util.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController extends Controller {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;


    @PostMapping("/login")
    public ResponseEntity login(@RequestParam String username, String password) {
        return businessLogic(answer -> {
            boolean hasError = false;
            UserDetails user =  userDetailsService.loadUserByUsername(username);
            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                answer.put("token", jwtUtil.createAccessToken((User)user));
            } else {
                answer.put("errorMessage", "Check your creditionals and try again");
                hasError = true;
            }
            return hasError;
        }, HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity login(@RequestBody User user) {

        return businessLogic(answer -> {
            boolean hasError = false;
            List<String> errors = validateUser(user);
            if (errors.size() != 0) {
                hasError = true;
                answer.put("errors", errors);
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userDao.createUser(user);
                answer.put("token", jwtUtil.createAccessToken(user));
            }
            return hasError;
        }, HttpStatus.CREATED);
    }

    private List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();
        if (user != null) {

            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                errors.add("Username can't be blank");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                errors.add("Password can't be blank");
            }
            if (user.getConfirmPassword() == null || user.getConfirmPassword().trim().isEmpty()) {
                errors.add("Confirm Password can't be blank");
            }
            if (!user.getConfirmPassword().equals(user.getPassword())) {
                errors.add("Passwords don't match");
            }
        } else {
            errors.add("User can't be empty");
        }
        return errors;
    }

}
