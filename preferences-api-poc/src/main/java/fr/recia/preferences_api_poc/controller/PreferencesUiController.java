package fr.recia.preferences_api_poc.controller;

import fr.recia.model_kafka_poc.model.UserPreferences;
import fr.recia.preferences_api_poc.service.PreferencesQueryService;
import fr.recia.preferences_api_poc.service.PreferencesValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ui/prefs")
@Slf4j
// TODO : Controlleur de test doublonné à supprimer lorsqu'on aura un vrai front pour requêter l'API
public class PreferencesUiController {

    private final PreferencesQueryService preferencesQueryService;
    private final PreferencesValidationService validationService;

    public PreferencesUiController(PreferencesQueryService preferencesQueryService, PreferencesValidationService validationService) {
        this.preferencesQueryService = preferencesQueryService;
        this.validationService = validationService;
    }

    @GetMapping
    public String load(@RequestParam String userId, Model model) {
        UserPreferences prefs = preferencesQueryService.getPreferences(userId);
        // Si pas de préférences, on créé des préférences vides avec tous les services
        if(prefs == null){
            log.trace("Creating empty preferences for user {}", userId);
            prefs = validationService.createEmptyUserPreferences(userId);
        }
        // S'il y a déjà des préférences, on vérifie si on doit ajouter un nouveau service
        else {
            validationService.validateUserPreferences(prefs);
        }
        model.addAttribute("userId", userId);
        model.addAttribute("prefs", prefs);
        return "preferences";
    }

    @PostMapping
    public String save(@RequestParam String userId, @ModelAttribute UserPreferences prefs, Model model) {
        preferencesQueryService.postPreferences(userId, prefs);
        model.addAttribute("userId", userId);
        model.addAttribute("prefs", prefs);
        return "preferences";
    }
}

