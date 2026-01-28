package fr.recia.consumer_mail_poc.services;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import java.util.List;

@Service
public class LdapMailQueryService {

    private final LdapTemplate ldapTemplate;

    public LdapMailQueryService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public String getPersonMail(String uid) {
        String base = "ou=people";
        String filter = "(uid=" + uid + ")";
        List<String> results = ldapTemplate.search(
                base,
                filter,
                (AttributesMapper<String>) attrs -> {
                    Attribute mailAttr = attrs.get("mail");
                    return mailAttr != null ? mailAttr.get().toString() : null;
                }
        );
        return results.isEmpty() ? null : results.getFirst();
    }
}


