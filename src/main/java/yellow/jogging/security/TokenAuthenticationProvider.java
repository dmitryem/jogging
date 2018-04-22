package yellow.jogging.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import yellow.jogging.beans.User;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        UsernamePasswordAuthenticationToken authenticationToken = null;
        if (token != null) {
            User user = jwtUtil.parseToken(token);
            if (user != null) {
                authenticationToken = new UsernamePasswordAuthenticationToken(user, token);
            }else{
                throw new AuthenticationCredentialsNotFoundException("Token expired");
            }
        }

        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
