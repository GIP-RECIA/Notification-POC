package fr.recia.soffit_java_client;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class SoffitJwtValidator {

    private final JwtParser jwtParser;

    public SoffitJwtValidator(String signatureKey) {
        this.jwtParser = Jwts.parserBuilder().setSigningKey(signatureKey.getBytes(StandardCharsets.UTF_8)).build();
    }

    public Authentication authenticate(String token) {
        Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        Claims claims = jws.getBody();
        SoffitPrincipal principal = new SoffitPrincipal(claims.getSubject(), new HashMap<>(claims));
        return new UsernamePasswordAuthenticationToken(principal, token, new ArrayList<>());
    }
}

