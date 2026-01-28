package fr.recia.preferences_api_poc.service;

import fr.recia.model_kafka_poc.model.ChannelPreferences;
import fr.recia.model_kafka_poc.model.Priority;
import fr.recia.model_kafka_poc.model.ServicePreferences;
import fr.recia.model_kafka_poc.model.UserPreferences;
import fr.recia.preferences_api_poc.configuration.PreferencesProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PreferencesValidationService {

    private final PreferencesProperties preferencesProperties;

    public PreferencesValidationService(PreferencesProperties preferencesProperties) {
        this.preferencesProperties = preferencesProperties;
    }

    public ServicePreferences createEmptyServicePreferences(){
        ServicePreferences servicePreferences = new ServicePreferences();
        servicePreferences.setEnabled(true);
        Map<Priority, ChannelPreferences> priorities = new HashMap<>();
        for(Priority priority : Priority.values()){
            priorities.put(priority, new ChannelPreferences(false, false, false));
        }
        servicePreferences.setPriorities(priorities);
        return servicePreferences;
    }

    public UserPreferences createEmptyUserPreferences(String userId){
        List<String> serviceList = preferencesProperties.getNotificationServices();
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUserId(userId);
        userPreferences.setGlobal(new ChannelPreferences(preferencesProperties.getDefaultChannels().isWs(),
                preferencesProperties.getDefaultChannels().isMail(), preferencesProperties.getDefaultChannels().isPush()));
        Map<String, ServicePreferences> services = new HashMap<>();
        for(String serviceName : serviceList){
            services.put(serviceName, createEmptyServicePreferences());
        }
        userPreferences.setServices(services);
        return userPreferences;
    }

    public void validateUserPreferences(UserPreferences userPreferences){
        List<String> serviceList = preferencesProperties.getNotificationServices();
        for(String serviceName : serviceList){
            if(!userPreferences.getServices().containsKey(serviceName)){
                log.trace("Adding missing service {} to preferences for user {}", serviceName, userPreferences.getUserId());
                userPreferences.getServices().put(serviceName, createEmptyServicePreferences());
            }
        }
    }


}
