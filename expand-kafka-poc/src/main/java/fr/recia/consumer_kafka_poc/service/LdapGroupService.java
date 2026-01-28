package fr.recia.consumer_kafka_poc.service;

import fr.recia.consumer_kafka_poc.configuration.LdapRequestProperties;
import lombok.extern.slf4j.Slf4j;
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
    private final LdapRequestProperties ldapRequestProperties;

    public LdapGroupService(LdapTemplate ldapTemplate, LdapRequestProperties ldapRequestProperties) {
        this.ldapTemplate = ldapTemplate;
        this.ldapRequestProperties = ldapRequestProperties;
    }

    public List<String> getGroupMembers(String groupCn) {

        String filter = MessageFormat.format(ldapRequestProperties.getFilter(), groupCn);
        log.trace("Filtre LDAP utilisé : {}", filter);

        return ldapTemplate.search(ldapRequestProperties.getBranchBase(), filter, this::mapMembers)
                .stream()
                .flatMap(Collection::stream)
                .filter(this::isUser)
                .toList();
    }

    private List<String> mapMembers(Attributes attrs) throws NamingException {
        List<String> members = new ArrayList<>();
        Attribute memberAttr = attrs.get(ldapRequestProperties.getRetrievedAttribute());
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



