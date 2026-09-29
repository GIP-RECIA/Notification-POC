package fr.recia.notifications.consumer_web.configuration;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "notification", name = "storage", havingValue = "mongodb")
@ImportAutoConfiguration({
        MongoAutoConfiguration.class,
        DataMongoAutoConfiguration.class
})
public class MongoStorageConfiguration {
}