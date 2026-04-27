package fr.recia.delayer_poc.services;

import fr.recia.delayer_poc.droitReconnexionConfig.PeriodesVacances;
import fr.recia.delayer_poc.droitReconnexionConfig.Region;
import fr.recia.delayer_poc.droitReconnexionConfig.VacancesProperties;
import fr.recia.delayer_poc.droitReconnexionConfig.CalendrierRegion;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DroitDeconnexionService {
    private final VacancesProperties vacancesProperties;


    private ZoneId getZoneId(Region region) {
        return (region == Region.REUNION)
                ? ZoneId.of("Indian/Reunion") // UTC +4
                : ZoneId.of("Europe/Paris"); // UTC +1 ou UTC +2 selon la période
    }

    public Duration calculDelai(long timestamp, Region region) {
        ZonedDateTime maintenant = Instant.ofEpochMilli(timestamp).atZone(getZoneId(region));
        ZonedDateTime prochainEnvoi = prochainMomentAutorise(maintenant, region);

        Duration delai = Duration.between(maintenant, prochainEnvoi);

        return delai.isNegative() ? Duration.ZERO : delai;
    }

    private LocalDate getFinVacances(LocalDate date, Region region){
        CalendrierRegion prop = getCalendrierRegion(region);

        for (PeriodesVacances period : prop.getVacances()) {
            if (!date.isBefore(period.getDebut()) && !date.isAfter(period.getFin().minusDays(1))){
                return period.getFin();
            }
        }
        return date;
    }

    private ZonedDateTime prochainMomentAutorise(ZonedDateTime dateTime, Region region) {
        LocalDate date = dateTime.toLocalDate();


        // Si vacances, on sélectionne le lundi de la rentrée, et on appelle la méthode avec pour savoir si c'est un jour où l'envoie est possible.
        if (estVacances(date, region)) {
            LocalDate finVacances = getFinVacances(date, region);
            ZonedDateTime repriseVacances = finVacances.plusDays(1).atTime(8, 0).atZone(getZoneId(region));

            return prochainMomentAutorise(repriseVacances, region);
        }

        // Si c'est un jour férié, on appelle la méthode avec le lendemain pour savoir si c'est un jour où l'envoie est possible ou pas.
        if (estFerie(date, region)){
            ZonedDateTime lendemain = date.plusDays(1).atTime(8,0).atZone(getZoneId(region));

            return prochainMomentAutorise(lendemain, region);
        }

        // Si week-end, on cherche le prochain jour où l'on pourra envoyer la notif, donc le prochain lundi.
        if (estWeekend(dateTime)) {
                LocalDate lundi = date.with(DayOfWeek.MONDAY);
                return lundi.atTime(8, 0).atZone(getZoneId(region));
        }

        //Si l'heure est en dehors des horaires autorisés, et que toutes les conditions précédentes n'ont pas été validées, on check si on est minuit passé,
        // si oui, on donne à dateTime la valeur du jour même à 8h, sinon, on donne la valeur du lendemain 8h.
        if (!estHeureAutorisee(dateTime)) {
            if (dateTime.getHour() < 8) {
                return date.atTime(8, 0).atZone(getZoneId(region));
            }else {
                ZonedDateTime demain = date.plusDays(1).atTime(8,0).atZone(getZoneId(region));
                return prochainMomentAutorise(demain, region);
            }
        }
        return dateTime;
    }

    private CalendrierRegion getCalendrierRegion(Region region) {
        return ( region == Region.REUNION)
                ? vacancesProperties.getReunion()
                : vacancesProperties.getCentre();
    }

    // Check si on peut envoyer la notif ou non.
    public boolean peutRecevoirNotif(String userId, long timestamp, Region region) {
        log.debug("le calendrier {}", vacancesProperties);
        ZonedDateTime dateTime = Instant.ofEpochMilli(timestamp).atZone(getZoneId(region));
        LocalDate date = dateTime.toLocalDate();

        if (estWeekend(dateTime)){
            log.debug("userId= {} en week-end", userId);
                    return false;
        }

        if (!estHeureAutorisee(dateTime)) {
            log.debug("userId = {} en horaire interdit", userId);
                return false;

        }

        if (estVacances(date, region)){
            log.debug("userId {} en vacances ou en jour férié", userId);
            return false;
        }

        if (estFerie(date, region)) {
            log.debug("userId {} en jour férié", userId);
            return false;
        }

        return true;
    }



    private boolean estWeekend(ZonedDateTime dateTime) {
        DayOfWeek jour = dateTime.getDayOfWeek();
        return jour == DayOfWeek.SATURDAY || jour == DayOfWeek.SUNDAY;
    }


    private boolean estHeureAutorisee(ZonedDateTime dateTime) {
        int heure = dateTime.getHour();
        return heure >= 8 && heure <= 18;
    }

    private boolean estVacances(LocalDate date, Region region){
        CalendrierRegion prop = getCalendrierRegion(region);


        for (PeriodesVacances periode : prop.getVacances()) {
            log.trace("date début : {} , date de fin {} , date actuelle {}", periode.getDebut(), periode.getFin(), date);
            if (date.isAfter(periode.getDebut()) && date.isBefore(periode.getFin().minusDays(1))) {
                return true;
            }
        }
        return false;
    }

    private boolean estFerie(LocalDate date, Region region){
        CalendrierRegion prop = getCalendrierRegion(region);

        return prop.getJoursFeries().contains(date) && !estVacances(date, region);
    }

}
