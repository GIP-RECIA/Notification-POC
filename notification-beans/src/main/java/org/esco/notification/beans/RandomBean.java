package org.esco.notification.beans;

import java.util.Date;
import java.util.UUID;

public class RandomBean {
    private String uuid = UUID.randomUUID().toString();
    private Date creationDate;
    private Date lastUpdateDate = creationDate;

    private String description;
    private String title;
    private Integer random;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRandom() {
        return random;
    }

    public void setRandom(Integer random) {
        this.random = random;
    }

    @Override
    public String toString() {
        return "RandomBean{" +
                "uuid='" + uuid + '\'' +
                '}';
    }
}
