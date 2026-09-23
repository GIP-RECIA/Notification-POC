package fr.recia.notifications.smtp_proxy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class SMTPSslConfig {

    @Bean
    public SSLContext smtpSslContext(SmtpProperties smtpProperties, ResourceLoader resourceLoader) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        Resource resource = resourceLoader.getResource(smtpProperties.getKeystorePath());

        try (InputStream fis = resource.getInputStream()) {
            keyStore.load(fis, smtpProperties.getKeyPassword().toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, smtpProperties.getKeyPassword().toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        return sslContext;
    }
}