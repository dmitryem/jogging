package yellow.jogging.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import yellow.jogging.beans.User;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static yellow.jogging.security.SecurityConstants.SECRET;


@Component
public class JwtUtil {

    public User parseToken(String token) {
        User user = new User();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            user.setId(Integer.parseInt(claims.getId()));
            user.setUsername(String.valueOf(claims.get("username")));
            if (claims.getExpiration().before(new Date())) {
                user = null;
            }
        }catch (Exception e){
            user = null;
        }
        return user;

    }

    public String createAccessToken(User user) {
        final Date now = new Date();

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("userId", String.valueOf(user.getId()));
        claims.put("username", user.getUsername());
        return Jwts.builder()
                .setClaims(claims)
                .setId(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(10L)))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET.getBytes())
                .compact();
    }

}
