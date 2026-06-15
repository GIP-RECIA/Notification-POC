package fr.recia.notifications.smtp_proxy.processor;

import jakarta.mail.internet.MimeMessage;

public interface MailProcessor {
    void process(String from, String dest, MimeMessage mimeMessage) throws Exception;
}
