package fr.recia.delayer.services;

import fr.recia.delayer.droitReconnexionConfig.*;
import fr.recia.delayer.services.DroitDeconnexionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DroitDeconnexionServiceTest {
    private DroitDeconnexionService droitDeconnexionService;
    private VacancesProperties vacancesProperties;
    private BornesHoraires bornesHoraires;

    @BeforeEach
    void setUpCalculDelaiHeure() {
        vacancesProperties = new VacancesProperties();

        CalendrierRegion centre = new CalendrierRegion();
        centre.setVacances(new ArrayList<>());
        centre.setJoursFeries(new ArrayList<>());
        vacancesProperties.setCentre(centre);

        CalendrierRegion reunion = new CalendrierRegion();
        reunion.setVacances(new ArrayList<>());
        reunion.setJoursFeries(new ArrayList<>());
        vacancesProperties.setReunion(reunion);

        droitDeconnexionService = new DroitDeconnexionService(vacancesProperties, bornesHoraires);
    }

    @Test
    @DisplayName("On test mardi 12 mai à 10h : on devrait avoir aucun délai")
    void testCalculDelaiMardiMatin() {
        ZonedDateTime date = ZonedDateTime.of(2026, 5, 12, 10, 0, 0, 0, ZoneId.of("Europe/Paris"));
        long timestamp = date.toInstant().toEpochMilli();

        Duration resultat = droitDeconnexionService.calculDelai(timestamp, Region.CENTRE);

        // On devrait avoir un délai de zéro, on test donc si c'est le cas
        assertEquals(Duration.ZERO, resultat);
    }

    @Test
    @DisplayName("On test le mardi 12 mai à 22h : on devrait avoir comme retour la durée entre le 12 mai 22h et le 13 mai 8h")
    void testCalculDelaiMardiSoir() {
        ZonedDateTime date = ZonedDateTime.of(2026, 5, 12, 22, 0, 0, 0, ZoneId.of("Europe/Paris"));
        long timestamp = date.toInstant().toEpochMilli();

        Duration resultat = droitDeconnexionService.calculDelai(timestamp, Region.CENTRE);

        // De 22h à 8h le lendemain, il y a un décalage de 10 heures, donc on test que c'est bien le cas
        assertEquals(Duration.ofHours(10), resultat);
    }

    @Test
    @DisplayName("On test si le décalage horaire est bien traité : la notification devrait être autorisée en métropole (17h) mais interdite à la Réunion (19h)")
    void testDifferenceRegionMemeInstant() {
        // On fixe un point dans le temps : 12 Mai 2026 à 15:00:00 UTC. En France, il est 17h, donc l'envoie de notification est autorisé,
        // mais pas à la Réunion car il est 19 là-bas
        ZonedDateTime instantUtc = ZonedDateTime.of(2026, 5, 12, 15, 0, 0, 0, ZoneOffset.UTC);
        long timestamp = instantUtc.toInstant().toEpochMilli();

        //  Test pour le CENTRE (17h00) -> Doit être envoyé tout de suite
        Duration delaiCentre = droitDeconnexionService.calculDelai(timestamp, Region.CENTRE);
        assertEquals(Duration.ZERO, delaiCentre, "À 17h à Paris, on devrait envoyer immédiatement");

        //  Test pour la REUNION (19h00) -> Doit être décalé au lendemain 8h
        Duration delaiReunion = droitDeconnexionService.calculDelai(timestamp, Region.REUNION);

        // De 19h à 8h le lendemain = 13 heures de délai
        assertFalse(delaiReunion.isZero(), "À 19h à la Réunion, on devrait avoir un délai");
        assertEquals(Duration.ofHours(13), delaiReunion);
    }

    @BeforeEach
    void setUpCalculDelaiVacance() {
        vacancesProperties = new VacancesProperties();


        CalendrierRegion centre = new CalendrierRegion();
        centre.setVacances(new ArrayList<>());
        centre.setJoursFeries(new ArrayList<>());
        vacancesProperties.setCentre(centre);

        CalendrierRegion reunion = new CalendrierRegion();
        reunion.setVacances(new ArrayList<>());
        reunion.setJoursFeries(new ArrayList<>());
        vacancesProperties.setReunion(reunion);

        droitDeconnexionService = new DroitDeconnexionService(vacancesProperties, bornesHoraires);

        // On crée une période : du 20 Octobre au 2 Novembre 2026
        PeriodesVacances toussaint = new PeriodesVacances();
        toussaint.setDebut(LocalDate.of(2026, 10, 20));
        toussaint.setFin(LocalDate.of(2026, 11, 2));

        // On l'ajoute au calendrier du Centre
        vacancesProperties.getCentre().getVacances().add(toussaint);
    }

    @Test
    @DisplayName("Test vacances : l'envoi doit tomber le lundi de la rentrée à 8h")
    void testCalculDelaiPendantVacances() {
        // Setup des vacances
        PeriodesVacances vac = new PeriodesVacances();
        vac.setDebut(LocalDate.of(2026, 5, 9));
        vac.setFin(LocalDate.of(2026, 5, 25)); // Lundi de rentrée
        vacancesProperties.getCentre().getVacances().add(vac);

        //  Date d'envoi : Vendredi 22 mai à 8h00
        ZonedDateTime dateEnvoi = ZonedDateTime.of(2026, 5, 22, 8, 0, 0, 0, ZoneId.of("Europe/Paris"));
        long timestamp = dateEnvoi.toInstant().toEpochMilli();

        //  Calcul manuel de l'attendu :
        // Du 22/5 10h au 05/25 08h = 96h
        ZonedDateTime repriseAttendue = ZonedDateTime.of(2026, 5, 25, 8, 0, 0, 0, ZoneId.of("Europe/Paris"));
        Duration attendu = Duration.between(dateEnvoi, repriseAttendue);

        Duration resultat = droitDeconnexionService.calculDelai(timestamp, Region.CENTRE);

        // Debug pour voir ce que le code trouve (en heures)
        System.out.println("Heures attendues : " + attendu.toHours());
        System.out.println("Heures obtenues : " + resultat.toHours());

        assertEquals(attendu, resultat, "Le délai devrait s'arrêter au lundi de la rentrée 8h");
    }

    @Test
    @DisplayName("Test croisé : Vacances à la Réunion uniquement")
    void testVacancesDifferentes() {
        // On définit des vacances pour la Réunion, ici en mai
        PeriodesVacances vac = new PeriodesVacances();
        vac.setDebut(LocalDate.of(2026, 5, 9));
        vac.setFin(LocalDate.of(2026, 5, 25)); // Lundi de la rentrée
        vacancesProperties.getReunion().getVacances().add(vac);

        //  On choisit une date le 22 mai, l'heure importe peu
        ZonedDateTime date = ZonedDateTime.of(2026, 5, 22, 10, 0, 0, 0, ZoneId.of("UTC"));
        long timestamp = date.toInstant().toEpochMilli();

        // La région centre n'est pas en vacances, donc il ne devrait pas y avoir de délai
        assertEquals(Duration.ZERO, droitDeconnexionService.calculDelai(timestamp, Region.CENTRE));

        // La Réunion est en vacances, donc il doit y avoir un délai
        Duration delaiReunion = droitDeconnexionService.calculDelai(timestamp, Region.REUNION);
        assertFalse(delaiReunion.isZero());
    }

}
