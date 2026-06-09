package fr.recia.delayer.services;

import fr.recia.delayer.configuration.LdapRequestDomainesProperties;
import fr.recia.delayer.droitReconnexionConfig.DomainesProperties;
import fr.recia.delayer.droitReconnexionConfig.Region;
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
    private final LdapRequestDomainesProperties ldapRequestDomainesProperties;
    private final DomainesProperties domainesProperties;

    public LdapRegionService(LdapTemplate ldapTemplate, LdapRequestDomainesProperties ldapRequestDomainesProperties, DomainesProperties domainesProperties) {
        this.ldapTemplate = ldapTemplate;
        this.ldapRequestDomainesProperties = ldapRequestDomainesProperties;
        this.domainesProperties = domainesProperties;
    }

    public Region getRegionByUid(String userId) {
        List<String> userDomaines = getListDomaineCentre(userId);

        List<String> referenceCentre = domainesProperties.getCentre();

        boolean isCentre = userDomaines.stream()
                .anyMatch(domaine -> referenceCentre != null && referenceCentre.contains(domaine));

       return isCentre ? Region.CENTRE : Region.REUNION;
    }


    public List<String> getListDomaineCentre(String uid) {
        String filter = MessageFormat.format(ldapRequestDomainesProperties.getFilter(), uid);

        return ldapTemplate.search(ldapRequestDomainesProperties.getBranchBase(), filter, this::mapDomaines)
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    private List<String> mapDomaines(Attributes attrs) throws NamingException {
        List<String> domaines = new ArrayList<>();
        Attribute domainesAttr = attrs.get(ldapRequestDomainesProperties.getRetrievedAttribute());
        if (domainesAttr !=null) {
            NamingEnumeration<?> all = domainesAttr.getAll();
            while (all.hasMore()) {
                domaines.add(all.next().toString());
            }
        }
        return domaines;
    }
}
