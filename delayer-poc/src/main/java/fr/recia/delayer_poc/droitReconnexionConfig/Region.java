package fr.recia.delayer_poc.droitReconnexionConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
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
