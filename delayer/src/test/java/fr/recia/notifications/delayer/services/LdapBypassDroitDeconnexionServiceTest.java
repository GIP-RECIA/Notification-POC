package fr.recia.notifications.delayer.services;

import fr.recia.notifications.delayer.configuration.LdapRequestBypassProperties;
import fr.recia.notifications.delayer.droitDeconnexionConfig.BypassDroitDeconnexionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LdapBypassDroitDeconnexionServiceTest {

    @Mock
    private LdapRequestBypassProperties ldapRequestBypassProperties;

    @Mock
    private LdapTemplate ldapTemplate;

    @Mock
    private BypassDroitDeconnexionConfig bypassDroitDeconnexionConfig;

    private LdapBypassDroitDeconnexionService service;

    @BeforeEach
    void setUp() {
        service = new LdapBypassDroitDeconnexionService(
                ldapRequestBypassProperties,
                ldapTemplate,
                bypassDroitDeconnexionConfig
        );
    }

    @Test
    @DisplayName("canBypass : doit retourner true si le profil de l'utilisateur est dans la liste des profils autorisés")
    void shouldReturnTrueWhenUserProfileIsAllowed() {
        String uid = "userBypass";
        String expectedProfile = "DIRECTION";

        // Mock de la configuration LDAP
        when(ldapRequestBypassProperties.getFilter()).thenReturn("uid={0}");
        when(ldapRequestBypassProperties.getBranchBase()).thenReturn("ou=people,dc=recia,dc=fr");

        // Mock du retour LDAP (on simule que le mapper a trouvé le profil "DIRECTION")
        when(ldapTemplate.search(anyString(), eq("uid=" + uid), any(AttributesMapper.class)))
                .thenReturn(List.of(expectedProfile));

        // Mock de la configuration métier (liste des profils autorisés)
        when(bypassDroitDeconnexionConfig.getProfil()).thenReturn(List.of("ADMIN", "DIRECTION"));

        boolean result = service.canBypass(uid);

        assertTrue(result, "L'utilisateur devrait pouvoir bypasser le droit à la déconnexion");
    }

    @Test
    @DisplayName("canBypass : doit retourner false si le profil de l'utilisateur n'est pas dans la liste")
    void shouldReturnFalseWhenUserProfileIsNotAllowed() {
        String uid = "userNormal";
        String userProfile = "ELEVE";

        when(ldapRequestBypassProperties.getFilter()).thenReturn("uid={0}");
        when(ldapRequestBypassProperties.getBranchBase()).thenReturn("ou=people,dc=recia,dc=fr");

        // L'utilisateur a bien un profil, mais c'est "ELEVE"
        when(ldapTemplate.search(anyString(), eq("uid=" + uid), any(AttributesMapper.class)))
                .thenReturn(List.of(userProfile));

        // Les profils autorisés ne contiennent pas "ELEVE"
        when(bypassDroitDeconnexionConfig.getProfil()).thenReturn(List.of("ADMIN", "DIRECTION"));

        boolean result = service.canBypass(uid);

        assertFalse(result, "L'utilisateur ne devrait pas pouvoir bypasser le droit à la déconnexion");
    }

    @Test
    @DisplayName("canBypass : doit retourner false si l'utilisateur n'est pas trouvé dans le LDAP (profil null)")
    void shouldReturnFalseWhenUserNotFoundInLdap() {
        String uid = "userGhost";

        when(ldapRequestBypassProperties.getFilter()).thenReturn("uid={0}");
        when(ldapRequestBypassProperties.getBranchBase()).thenReturn("ou=people,dc=recia,dc=fr");

        // On simule un LDAP qui ne trouve personne (liste vide)
        when(ldapTemplate.search(anyString(), eq("uid=" + uid), any(AttributesMapper.class)))
                .thenReturn(Collections.emptyList());

        boolean result = service.canBypass(uid);

        assertFalse(result, "Un utilisateur non trouvé ne devrait pas bypasser le droit à la déconnexion");
    }
}