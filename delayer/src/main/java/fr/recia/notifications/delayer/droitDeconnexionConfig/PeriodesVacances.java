package fr.recia.notifications.delayer.droitDeconnexionConfig;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PeriodesVacances {
    private LocalDate debut;
    private LocalDate fin;


}
