package fr.recia.smtp_proxy.registry;

import fr.recia.smtp_proxy.configuration.NotificationClientProperties;
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

    public ProcessorRegistry(NotificationClientProperties notificationClientProperties){
        this.registry = new HashMap<>();
        this.notificationClientProperties = notificationClientProperties;
    }

    public MailProcessor get(String name) {
        return registry.get(name);
    }

    @PostConstruct
    public void init() {
        // TODO : créer un client par processor dynamiquement en fonction de la config (SMTPRoutingRule)
        register("NextcloudProcessor", new NextcloudProcessor(notificationClientProperties.getUrl(), "NEXTCLOUD", "Xif0duNVWzCPvzkk9LIcAZvpdYukD6ws"));
        //register("MoodleProcessor", ...);
        //register("WimsProcessor", ...);

    }

    private void register(String name, MailProcessor processor) {
        registry.put(name, processor);
    }
}