package fr.recia.preferences_api_poc.controller;

import fr.recia.model_kafka_poc.model.UserPreferences;
import fr.recia.preferences_api_poc.service.PreferencesQueryService;
import fr.recia.preferences_api_poc.service.PreferencesValidationService;
import fr.recia.soffit_java_client.SoffitPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prefs")
@Slf4j
public class PreferencesController {

    private final PreferencesQueryService queryService;
    private final PreferencesValidationService validationService;

    public PreferencesController(PreferencesQueryService queryService, PreferencesValidationService validationService) {
        this.queryService = queryService;
        this.validationService = validationService;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> savePreferences(@RequestBody UserPreferences preferences, @AuthenticationPrincipal SoffitPrincipal principal) {
        this.queryService.postPreferences(principal.getUsername(), preferences);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public UserPreferences getPreferences(@AuthenticationPrincipal SoffitPrincipal principal) {
        UserPreferences prefs = queryService.getPreferences(principal.getUsername());
        // Si pas de préférences, on créé des préférences vides avec tous les services
        if(prefs == null){
            log.trace("Creating empty preferences for user {}", principal.getUsername());
            prefs = validationService.createEmptyUserPreferences(principal.getUsername());
        }
        // S'il y a déjà des préférences, on vérifie si on doit ajouter un nouveau service
        else {
            validationService.validateUserPreferences(prefs);
        }
        return prefs;
    }

}
