package yellow.jogging.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import yellow.jogging.beans.User;
import yellow.jogging.db.dao.UserDao;
import yellow.jogging.security.JwtUtil;

import java.util.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private  UserDao userDao;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;


    @GetMapping("/login")
    public ResponseEntity login(@RequestParam String username,String password) {
        Map<String,Object>  answer = new HashMap<>();
        boolean hasError = false;
        User user = (User) userDetailsService.loadUserByUsername(username);
        if(passwordEncoder.matches(password,user.getPassword())){
            answer.put("token",jwtUtil.createAccessToken(user));
        }else{
            answer.put("errorMessage","Check your conditionals and try again");
            hasError = true;
        }
        ResponseEntity response;
        if (hasError) {
            response = ResponseEntity.badRequest().body(answer);
        } else {
            response = ResponseEntity.ok(answer);
        }
        return response;
    }

    @PostMapping("/register")
    public ResponseEntity login(@RequestBody User user) {
        Map<String,Object>  answer = new HashMap<>();
        boolean hasError = false;
        List<String> errors = validateUser(user);
        if(errors.size() != 0){
            hasError = true;
            answer.put("errors",errors);
        }else{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.createUser(user);
            answer.put("token",jwtUtil.createAccessToken(user));
        }
        ResponseEntity response;
        if (hasError) {
            response = ResponseEntity.badRequest().body(answer);
        } else {
            response = ResponseEntity.ok(answer);
        }
        return response;
    }

    private List<String> validateUser(User user){
        List<String> errors = new ArrayList<>();
        if(user.getUsername() == null || user.getUsername().trim().isEmpty()){
            errors.add("Username can't be blank");
        }
        if(user.getPassword()== null || user.getPassword().trim().isEmpty()){
            errors.add("Password can't be blank");
        }
        if(user.getConfirmPassword()== null || user.getConfirmPassword().trim().isEmpty()){
            errors.add("Confirm Password can't be blank");
        }
        if(!user.getConfirmPassword().equals(user.getPassword())){
            errors.add("Passwords don't match");
        }
        return errors;
    }

}
