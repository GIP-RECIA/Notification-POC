package fr.recia.delayer_poc.services;

import fr.recia.delayer_poc.configuration.LdapRequestProperties;
import fr.recia.delayer_poc.droitReconnexionConfig.DomainesProperties;
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
public class LdapRegionService {
    private final LdapTemplate ldapTemplate;
    private final LdapRequestProperties ldapRequestProperties;
    private final DomainesProperties domainesProperties;

    public LdapRegionService(LdapTemplate ldapTemplate, LdapRequestProperties ldapRequestProperties, DomainesProperties domainesProperties) {
        this.ldapTemplate = ldapTemplate;
        this.ldapRequestProperties = ldapRequestProperties;
        this.domainesProperties = domainesProperties;
    }

    public String getRegionByUid(String userId) {
        List<String> userDomaines = getListDomaineCentre(userId);

        List<String> referenceCentre = domainesProperties.getCentre();

        boolean isCentre = userDomaines.stream()
                .anyMatch(domaine -> referenceCentre != null && referenceCentre.contains(domaine));

       return isCentre ? "centre" : "reunion";
    }


    public List<String> getListDomaineCentre(String uid) {
        String filter = MessageFormat.format(ldapRequestProperties.getFilter(), uid);

        return ldapTemplate.search(ldapRequestProperties.getBranchBase(), filter, this::mapDomaines)
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    private List<String> mapDomaines(Attributes attrs) throws NamingException {
        List<String> domaines = new ArrayList<>();
        Attribute domainesAttr = attrs.get(ldapRequestProperties.getRetrievedAttribute());
        if (domainesAttr !=null) {
            NamingEnumeration<?> all = domainesAttr.getAll();
            while (all.hasMore()) {
                domaines.add(all.next().toString());
            }
        }
        return domaines;
    }
}
