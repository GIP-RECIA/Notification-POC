package fr.recia.notifications.model_kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelPreferences {
    private boolean ws;
    private boolean mail;
    private boolean push;
}
