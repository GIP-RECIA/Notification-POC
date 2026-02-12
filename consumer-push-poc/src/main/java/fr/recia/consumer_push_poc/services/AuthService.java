package fr.recia.consumer_push_poc.services;

import fr.recia.consumer_push_poc.configuration.CASProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AuthService {

    private HttpClient httpClient;
    private CASProperties casProperties;

    public AuthService(CASProperties casProperties){
        this.httpClient = HttpClient.newHttpClient();
        this.casProperties = casProperties;
    }

    /**
     * Authentifie un utilisateur en faisant valider un ST au CAS
     * @param ticket le ST à faire valider, qui a été établi avant mais sans aller au bout de la chaîne
     * @return L'UID de l'utilisateur lu depuis la réponse de validation du ticket
     */
    public String authenticate(String ticket) {

        try {
            // Construction de l’URL de validation du ticket
            String url = casProperties.getValidateUrl() +
                    "?ticket=" + URLEncoder.encode(ticket, StandardCharsets.UTF_8) +
                    "&service=" + URLEncoder.encode(casProperties.getServiceUrl(), StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Unable to authenticate user for ticket {}. Response code is {}", ticket, response.statusCode());
                return null;
            }

            String xml = response.body();
            log.trace("Successful response received from CAS : {}", xml);
            return extractUserFromXml(xml);

        } catch (Exception e) {
            log.error("An error has ocurred while authenticating to CAS for ticket {}", ticket, e);
            return null;
        }
    }

    /**
     * Extrait l'UID depuis le XML donné par le CAS lors de la validation du ticket
     */
    private String extractUserFromXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        NodeList nodeList = document.getElementsByTagNameNS("*", "user");
        if (nodeList.getLength() == 0) {
            log.error("User not found in cas answer");
            return null;
        }
        return nodeList.item(0).getTextContent();
    }

}
