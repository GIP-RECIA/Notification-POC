package fr.recia.soffit_java_client;

import lombok.Data;

import java.util.Map;

@Data
public class SoffitPrincipal {

    private final String username;
    private final Map<String, Object> claims;

    public SoffitPrincipal(String username, Map<String, Object> claims) {
        this.username = username;
        this.claims = claims;
    }

    public <T> T getClaim(String name, Class<T> type) {
        return type.cast(claims.get(name));
    }
}

