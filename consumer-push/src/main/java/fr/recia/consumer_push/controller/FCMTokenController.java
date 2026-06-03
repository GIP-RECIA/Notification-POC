package fr.recia.consumer_push.controller;

import fr.recia.consumer_push.model.TokenDto;
import fr.recia.consumer_push.services.AuthService;
import fr.recia.consumer_push.services.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@Slf4j
public class FCMTokenController {

    private final TokenService tokenService;
    private final AuthService authService;

    public FCMTokenController(TokenService tokenService, AuthService authService){
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody TokenDto tokenDto) {
        log.debug("Got a new token registration {}", tokenDto);
        String uid = authService.authenticate(tokenDto.getTicket());
        if(uid != null){
            log.debug("Authenticated user {} from ticket {}", uid, tokenDto.getTicket());
            tokenService.saveToken(uid, tokenDto.getToken());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }

    }

}
