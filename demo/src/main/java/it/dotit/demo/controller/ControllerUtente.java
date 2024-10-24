package it.dotit.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.internal.build.AllowNonPortable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.dotit.demo.auth.AuthenticationResponse;
import it.dotit.demo.auth.RegisterRequest;
import it.dotit.demo.auth.UpdateUserRequest;
import it.dotit.demo.exceptions.myExceptions.UserNotFoundException;
import it.dotit.demo.model.Role;
import it.dotit.demo.service.AuthenticationService;
import it.dotit.demo.service.CrudUserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/utente")
@RequiredArgsConstructor
//@Tag(name = "User&AdminController")
public class ControllerUtente {


    private final CrudUserService crudUserService;

    private final AuthenticationService authenticationService;


    @Operation(
            description = "Get endpoint for manager",
            summary = "This is summary for updateUtente endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or (principal.username == #request.oldUsername)")
    @PostMapping("/updateUtente")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest request) {

        crudUserService.updateUtente(request);
        return ResponseEntity.ok("utente modificato");

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/homeAdmin") // Mappa le richieste GET all'endpoint "/user/sayHello" a questo metodo
    public ResponseEntity<String> sayHelloAdmin() {
        // Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti
        // autenticati
        return ResponseEntity.ok("hello from secured endpoint for admins"); // Risponde con uno stato HTTP 200 e un
                                                                            // messaggio
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/homeUser") // Mappa le richieste GET all'endpoint "/user/sayHello" a questo metodo
    public ResponseEntity<String> sayHelloUser() {
        // Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti
        // autenticati
        return ResponseEntity.ok("hello from secured endpoint for users"); // Risponde con uno stato HTTP 200 e un
                                                                           // messaggio
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrazioneAdmins")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        // Registra un nuovo admin e restituisce la risposta dell'autenticazione
        return ResponseEntity.ok(authenticationService.register(request, Role.ADMIN));

    }

}
