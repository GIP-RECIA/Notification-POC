package fr.recia.notifications.consumer_push.model;

import lombok.Data;

@Data
public class TokenDto {
    private String ticket;
    private String token;
}
