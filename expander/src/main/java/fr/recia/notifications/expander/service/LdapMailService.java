package fr.recia.notifications.expander.service;

import fr.recia.notifications.expander.configuration.LdapMailRequestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public List<String> getUidByMail(String mail) {
        if (mail == null || mail.isEmpty()) {
            return null;
        }
        String filter = MessageFormat.format(ldapMailRequestProperties.getFilter(), mail);

        return ldapTemplate.search(ldapMailRequestProperties.getBranchBase(), filter, this::mapUid)
                .stream()
                .flatMap(Collection::stream)
                .filter(this::isUser)
                .toList();
    }

    private List<String> mapUid(Attributes attrs) throws NamingException {
        List<String> uid =  new ArrayList<>();
        Attribute uidAttr = attrs.get(ldapMailRequestProperties.getRetrievedAttribute());
        if (uidAttr != null && uidAttr.get() != null) {
            NamingEnumeration<?> all = uidAttr.getAll();
            while (all.hasMore())  {
                uid.add(all.next().toString());
            }
        }
        return uid;
    }

    private boolean isUser(String value) {
        return value.length() == 8 && (value.startsWith("F") || value.startsWith("f"));
    }
}