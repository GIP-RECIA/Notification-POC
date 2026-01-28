package fr.recia.soffit_java_client;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class SoffitJwtAuthenticationFilter extends OncePerRequestFilter {

    private final SoffitJwtValidator jwtValidator;

    public SoffitJwtAuthenticationFilter(SoffitJwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.trace("Authentication : soffit filter execution...");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Authorization header is not valid !");
            // Important : on éxécute quand même la suite de chaine de filtres mais sans créer de principal authentifié
            // Sur les endpoints authentifés, on aura un 403 de la part de spring security, mais par contre on pourra bien atteindre les endpoints publics
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = jwtValidator.authenticate(authHeader.substring(7));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.trace("User successfully authenticated {}", authentication);
        } catch (JwtException e) {
            log.error("Error during JWT decoding ! Soffit authentification failure", e);
            // Par contre si on a un champ Authorization qui est présent mais invalide, on peut directement refuser la requête
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

