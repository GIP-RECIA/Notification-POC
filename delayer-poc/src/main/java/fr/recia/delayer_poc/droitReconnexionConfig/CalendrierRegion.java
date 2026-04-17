package fr.recia.delayer_poc.droitReconnexionConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@ToString
@Setter
@Getter
public class CalendrierRegion {
    private List<PeriodesVacances> vacances;
    private List<LocalDate> joursFeries;

}
