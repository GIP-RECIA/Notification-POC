package fr.recia.delayer.services;

import fr.recia.delayer.configuration.LdapRequestBypassProperties;
import fr.recia.delayer.droitReconnexionConfig.BypassDroitDeconnexionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
public class LdapBypassDroitDeconnexionService {
    private final LdapRequestBypassProperties ldapRequestBypassProperties;
    private final LdapTemplate ldapTemplate;
    private final BypassDroitDeconnexionConfig bypassDroitDeconnexionConfig;


    public LdapBypassDroitDeconnexionService(LdapRequestBypassProperties ldapRequestBypassProperties, LdapTemplate ldapTemplate, BypassDroitDeconnexionConfig bypassDroitDeconnexionConfig) {
        this.ldapRequestBypassProperties = ldapRequestBypassProperties;
        this.ldapTemplate = ldapTemplate;
        this.bypassDroitDeconnexionConfig = bypassDroitDeconnexionConfig;
    }

    public boolean canBypass(String userId) {
        String userProfil = getProfile(userId);

        log.debug("Le profil de l'utilisateur est : {}", userProfil);


        List<String> profil = bypassDroitDeconnexionConfig.getProfil();

        log.debug("La liste des profils qui peuvent bypass est : {}", profil);

        if(profil.contains(userProfil)){
            return true;
        }

        return false;
    }


    public String getProfile(String uid) {
        String filter = MessageFormat.format(ldapRequestBypassProperties.getFilter(), uid);

        return ldapTemplate.search(ldapRequestBypassProperties.getBranchBase(), filter, this::mapProfil)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private String mapProfil(Attributes attrs) throws NamingException {
        var profileAttr = attrs.get(ldapRequestBypassProperties.getRetrievedAttribute());
        if (profileAttr != null && profileAttr.get() != null) {
            return profileAttr.get().toString();
        }
        return null;
    }
}
