package fr.recia.notifications.smtp_proxy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class SMTPSslConfig {

    @Bean
    public SSLContext smtpSslContext(SmtpProperties smtpProperties) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(smtpProperties.getKeystorePath())) {
            keyStore.load(fis, smtpProperties.getKeystorePassword().toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, smtpProperties.getKeyPassword().toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        return sslContext;
    }
}