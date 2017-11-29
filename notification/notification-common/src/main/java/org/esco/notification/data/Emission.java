package org.esco.notification.data;

import lombok.Data;

@Data
public class Emission {
    private EmissionHeader header;
    private EventContent content;
}
