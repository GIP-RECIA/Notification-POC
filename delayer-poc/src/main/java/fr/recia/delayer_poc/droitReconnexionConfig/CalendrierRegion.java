package fr.recia.delayer_poc.droitReconnexionConfig;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CalendrierRegion {
    private List<PeriodesVacances> vacances;
    private List<LocalDate> joursFeries;

}
