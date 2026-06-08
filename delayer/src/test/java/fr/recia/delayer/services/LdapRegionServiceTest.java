package fr.recia.delayer.services;

import fr.recia.delayer.configuration.LdapRequestDomainesProperties;
import fr.recia.delayer.droitReconnexionConfig.DomainesProperties;
import fr.recia.delayer.droitReconnexionConfig.Region;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LdapRegionServiceTest {
    @Mock
    private LdapTemplate ldapTemplate;

    @Mock
    private LdapRequestDomainesProperties ldapRequestDomainesProperties;

    @Mock
    private DomainesProperties domainesProperties;

    private LdapRegionService ldapRegionService;

    @BeforeEach
    void setUp() {
        ldapRegionService = new LdapRegionService(ldapTemplate, ldapRequestDomainesProperties, domainesProperties);
    }

    @Test
    void shouldReturnCentreWhenUserHasCentreDomaine(){
        String uid = "F1700ivg";

        when(ldapRequestDomainesProperties.getFilter()).thenReturn("uid={0}");
        when(ldapRequestDomainesProperties.getBranchBase()).thenReturn("ou=users,dc=recia,dc=fr");
        when(domainesProperties.getCentre()).thenReturn(List.of("lycees.netocentre.fr", "cfa.netocentre.fr", "recia.netocentre.fr"));

        when(ldapTemplate.search(anyString(), anyString(), any(AttributesMapper.class))).thenReturn(List.of(List.of("lycees.netocentre.fr")));

        Region result = ldapRegionService.getRegionByUid(uid);

        assertEquals(Region.CENTRE, result);
    }

    @Test
    void shouldReturnReunionWhenUSerHasReunionDomaine() {
        String uid = "F1000abc";

        when(ldapRequestDomainesProperties.getFilter()).thenReturn("uid={0}");
        when(ldapRequestDomainesProperties.getBranchBase()).thenReturn("ou=users,dc=recia,dc=fr");
        when(domainesProperties.getCentre()).thenReturn(List.of("lycees.netocentre.fr", "cfa.netocentre.fr", "recia.netocentre.fr"));

        when(ldapTemplate.search(anyString(), anyString(), any(AttributesMapper.class))).thenReturn(List.of(List.of("reunion.fr")));

        Region result = ldapRegionService.getRegionByUid(uid);

        assertEquals(Region.REUNION, result);
    }
}
