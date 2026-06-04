package fr.recia.smtp_proxy.registry;

import fr.recia.smtp_proxy.configuration.NotificationClientProperties;
import fr.recia.smtp_proxy.configuration.SMTPRoutingProperties;
import fr.recia.smtp_proxy.configuration.SMTPRoutingRule;
import fr.recia.smtp_proxy.processor.MailProcessor;
import fr.recia.smtp_proxy.processor.impl.NextcloudProcessor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ProcessorRegistry {

    private final Map<String, MailProcessor> registry;
    private final NotificationClientProperties notificationClientProperties;
    private final SMTPRoutingProperties smtpRoutingProperties;

    public ProcessorRegistry(NotificationClientProperties notificationClientProperties, SMTPRoutingProperties smtpRoutingProperties){
        this.smtpRoutingProperties = smtpRoutingProperties;
        this.registry = new HashMap<>();
        this.notificationClientProperties = notificationClientProperties;
    }

    public MailProcessor get(String name) {
        return registry.get(name);
    }

    @PostConstruct
    public void init() {

        String url = notificationClientProperties.getUrl();

        for (SMTPRoutingRule rule : smtpRoutingProperties.getRules()) {
            String processorName = rule.getProcessor();
            String service = rule.getService();
            String apiKey = rule.getApiKey();

            MailProcessor processor = null;;

            switch (processorName) {
                case "NextcloudProcessor" :
                    processor = new NextcloudProcessor(url, service, apiKey);
            }

            if (processor != null) {
                register(processorName, processor);
            }
        }


    }

    private void register(String name, MailProcessor processor) {
        registry.put(name, processor);
    }
}