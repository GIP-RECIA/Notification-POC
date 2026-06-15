package fr.recia.notifications.delayer.droitDeconnexionConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Region {
    CENTRE("centre"),
    REUNION("reunion");

    private final String label;

    @Override
    public String toString() {
        return this.label;
    }
}
