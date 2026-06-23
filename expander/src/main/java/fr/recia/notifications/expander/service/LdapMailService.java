package fr.recia.notifications.expander.service;

import fr.recia.notifications.expander.configuration.LdapMailRequestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.text.MessageFormat;

@Service
@Slf4j
public class LdapMailService {
    private final LdapTemplate ldapTemplate;
    private final LdapMailRequestProperties ldapMailRequestProperties;

    public LdapMailService(LdapTemplate ldapTemplate, LdapMailRequestProperties ldapMailRequestProperties) {
        this.ldapTemplate = ldapTemplate;
        this.ldapMailRequestProperties = ldapMailRequestProperties;
    }

    @Cacheable(value = "ldapUidByMail", key = "#mail")
    public String getUidByMail(String mail) {
        if (mail == null || mail.isEmpty()) {
            return null;
        }

        String filter = MessageFormat.format(ldapMailRequestProperties.getFilter(), mail);

        return ldapTemplate.search(ldapMailRequestProperties.getBranchBase(), filter, this::mapUid)
                .stream()
                .findFirst()
                .orElse(null);

    }


    private String mapUid(Attributes attrs) throws NamingException {
        var uidAttr = attrs.get(ldapMailRequestProperties.getRetrievedAttribute());
        if (uidAttr != null && uidAttr.get() != null) {
            return uidAttr.get().toString();
        }
        return null;
    }
}