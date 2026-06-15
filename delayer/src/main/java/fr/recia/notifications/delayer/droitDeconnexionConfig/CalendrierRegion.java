package fr.recia.notifications.delayer.droitDeconnexionConfig;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CalendrierRegion {
    private List<PeriodesVacances> vacances;
    private List<LocalDate> joursFeries;

}
