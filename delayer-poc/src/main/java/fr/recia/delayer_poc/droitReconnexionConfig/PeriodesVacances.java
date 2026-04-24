package fr.recia.delayer_poc.droitReconnexionConfig;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PeriodesVacances {
    private LocalDate debut;
    private LocalDate fin;


}
