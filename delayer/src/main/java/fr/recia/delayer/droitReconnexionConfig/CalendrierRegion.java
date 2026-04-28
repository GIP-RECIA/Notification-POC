package fr.recia.delayer.droitReconnexionConfig;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CalendrierRegion {
    private List<PeriodesVacances> vacances;
    private List<LocalDate> joursFeries;

}
