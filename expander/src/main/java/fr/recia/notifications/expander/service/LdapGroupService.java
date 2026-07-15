package fr.recia.notifications.expander.service;

import fr.recia.notifications.expander.configuration.LdapGroupRequestProperties;
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
public class LdapGroupService {

    private final LdapTemplate ldapTemplate;
    private final LdapGroupRequestProperties ldapGroupRequestProperties;

    public LdapGroupService(LdapTemplate ldapTemplate, LdapGroupRequestProperties ldapGroupRequestProperties) {
        this.ldapTemplate = ldapTemplate;
        this.ldapGroupRequestProperties = ldapGroupRequestProperties;
    }

    @Cacheable(value = "ldapGroupMembers", key = "#groupCn")
    public List<String> getGroupMembers(String groupCn) {


        String filter = MessageFormat.format(ldapGroupRequestProperties.getFilter(), groupCn);
        log.trace("LDAP filter used : {}", filter);

        return ldapTemplate.search(ldapGroupRequestProperties.getBranchBase(), filter, this::mapMembers)
                .stream()
                .flatMap(Collection::stream)
                .filter(this::isUser)
                .toList();
    }

    private List<String> mapMembers(Attributes attrs) throws NamingException {
        List<String> members = new ArrayList<>();
        Attribute memberAttr = attrs.get(ldapGroupRequestProperties.getRetrievedAttribute());
        if (memberAttr != null) {
            NamingEnumeration<?> all = memberAttr.getAll();
            while (all.hasMore()) {
                members.add(all.next().toString());
            }
        }
        return members;
    }

    private boolean isUser(String value) {
        return value.length() == 8 && (value.startsWith("F") || value.startsWith("f"));
    }
}



