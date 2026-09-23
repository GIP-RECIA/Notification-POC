package fr.recia.notifications.e2e_tests.clients;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;
import java.util.Properties;

public class InputSmtpApiClient {

    private final int smtpPort;
    private final String smtpHost;
    private final String username;
    private final String password;

    public InputSmtpApiClient(String smtpHost, int smtpPort, String username, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
    }

    public void sendEmail(String from, String to, String subject, String textBody, Map<String, String> headers) {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(textBody);

            // Ajout des headers cruciaux pour passer les Regex du SMTPRoutingRule
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    message.addHeader(entry.getKey(), entry.getValue());
                }
            }

            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to SMTP proxy", e);
        }
    }
}