# Soffit Java Client

Librairie de sécurité par soffit pour spring-security compatible java 21.

## Mise en place

1- Ajouter la dépendance dans le projet. Exemple avec maven :

```xml
<dependency>
    <groupId>fr.recia</groupId>
    <artifactId>soffit-java-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

2- Déclarer les beans nécéssaires au fonctionnement du filtre

```java
@Bean
SoffitJwtValidator soffitJwtValidator() {
    return new SoffitJwtValidator(SIGNATURE_KEY);
}

@Bean
SoffitJwtAuthenticationFilter soffitJwtAuthenticationFilter(SoffitJwtValidator validator) {
    return new SoffitJwtAuthenticationFilter(validator);
}
```

Bonus : Créer une classe de properties pour charger la clé de signature depuis un fichier de config (à récupérer ensuite à l'endroit de la création des beans via un autowired)
```java
@Component
@ConfigurationProperties(prefix = "soffit.jwt")
@Data
public class SoffitJwtProperties {
    private String signatureKey;
}
```

3- Ajouter le filtre dans la chaine de filtres. Exemple pour protéger toutes les routes sauf `/health-check` :
```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http, SoffitJwtAuthenticationFilter filter) {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/health-check/**").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```

## Utilisation

On peut ensuite récupérer les claims/username via le principal spring-security :
- Soit directement avec `@AuthenticationPrincipal` en paramètre de méthode sur les controleurs
- Soit via le SecurityContextHolder avec `SecurityContextHolder.getContext().getAuthentication().getPrincipal()`

Un objet dedié de type `SoffitPrincipal` permet d'accéder facilement à toutes les propriétés utiles du principal (voir les méthodes de la classe).


