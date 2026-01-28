package fr.recia.consumer_kafka_poc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LdapGroupService {

    private final LdapTemplate ldapTemplate;

    public LdapGroupService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public List<String> getGroupMembers(String groupCn) {
        String base = "ou=groups";
        String filter = "(&(objectClass=groupOfNames)(cn=" + groupCn + "))";
        log.trace("Filtre utilisé pour récupérer les utilisateurs du groupe : {}", filter);
        List<List<String>> rawResults = ldapTemplate.search(
                base,
                filter,
                (AttributesMapper<List<String>>) attrs -> {
                    List<String> members = new ArrayList<>();
                    Attribute memberAttr = attrs.get("hasMember");
                    if (memberAttr != null) {
                        NamingEnumeration<?> allMembers = memberAttr.getAll();
                        while (allMembers.hasMore()) {
                            Object obj = allMembers.next();
                            members.add(obj.toString());
                        }
                    }
                    return members;
                }
        );
        List<String> membersFlattened = new ArrayList<>();
        for (List<String> sublist : rawResults) {
            for(String elem : sublist){
                // TODO : ne garder que les users, pas les groupes -> améliorer le test
                if(elem.length()==8 && elem.startsWith("F")){
                    membersFlattened.add(elem);
                }
            }
        }
        return membersFlattened;
    }
}


