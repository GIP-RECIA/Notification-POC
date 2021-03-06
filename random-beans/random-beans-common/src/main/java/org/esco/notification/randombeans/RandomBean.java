package org.esco.notification.randombeans;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class RandomBean {
    private String uuid = UUID.randomUUID().toString();
    private Date creationDate;
    private Date lastUpdateDate = creationDate;

    private String description;
    private String title;
    private Integer random;

    @Override
    public String toString() {
        return "RandomBean{" +
                "uuid='" + uuid + '\'' +
                '}';
    }
}
