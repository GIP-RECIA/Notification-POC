package fr.recia.consumer_mail_poc.services;

import fr.recia.consumer_mail_poc.configuration.LdapRequestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class LdapMailQueryService {

    private final LdapTemplate ldapTemplate;
    private final LdapRequestProperties ldapRequestProperties;

    public LdapMailQueryService(LdapTemplate ldapTemplate, LdapRequestProperties ldapRequestProperties) {
        this.ldapTemplate = ldapTemplate;
        this.ldapRequestProperties = ldapRequestProperties;
    }

    @Cacheable(cacheNames = "ldap-person-mail", key = "#uid")
    public Optional<String> getPersonMail(String uid) {

        String filter = MessageFormat.format(ldapRequestProperties.getFilter(), uid);
        log.trace("Filtre LDAP utilisé : {}", filter);

        return ldapTemplate.search(ldapRequestProperties.getBranchBase(), filter, this::mapMail)
                .stream()
                .filter(Objects::nonNull)
                .findFirst();
    }

    private String mapMail(Attributes attrs) throws NamingException {
        Attribute mailAttr = attrs.get(ldapRequestProperties.getRetrievedAttribute());
        return mailAttr != null ? mailAttr.get().toString() : null;
    }

}

