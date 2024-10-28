package it.dotit.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
import it.dotit.demo.model.Role;
import it.dotit.demo.service.AuthenticationService;
import it.dotit.demo.service.CrudUserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/utente")
@RequiredArgsConstructor
@Tag(name = "User&AdminController")
public class ControllerUtente {


    private final CrudUserService crudUserService;

    private final AuthenticationService authenticationService;


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(
        description = "endpoint post per modifica utente",
        summary = "Questo è il riassunto per l'endpoint updateUtente",
        responses = {
            @ApiResponse(
                description = "Modifica utente effettuata con successo",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Non sei autorizzato oppure il tuo token non è valido",
                responseCode = "403"
            ),
            @ApiResponse(
                description = "L'username che hai inserito è già esistente, non puoi cambiare lo username con uno già presente",
                responseCode = "409"  
            ),
            @ApiResponse(
                description = "L'utente da modificare non è stato trovato nel database",
                responseCode = "404"
            ),
            @ApiResponse(
                description = "E' obbligatorio oldUsername e almeno uno fra username e password",
                responseCode = "422"
            )
        }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or (principal.username == #request.oldUsername)")
    @PostMapping("/updateUtente")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest request) {

        crudUserService.updateUtente(request);
        return ResponseEntity.ok("utente modificato");

    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Operation(
        description = "endpoint get accessibile solo da admins",
        summary = "Questo è il riassunto per l'endpoint homeAdmin",
        responses = {
            @ApiResponse(
                description = "la richiesta ha avuto successo",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Non sei autorizzato oppure il tuo token non è valido",
                responseCode = "403"
            )
        }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/homeAdmin") // Mappa le richieste GET all'endpoint "/user/sayHello" a questo metodo
    public ResponseEntity<String> sayHelloAdmin() {
        // Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti
        // autenticati
        return ResponseEntity.ok("hello from secured endpoint for admins"); // Risponde con uno stato HTTP 200 e un
                                                                            // messaggio
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    @Operation(
        description = "endpoint get accessibile solo da users",
        summary = "Questo è il riassunto per l'endpoint homeUser",
        responses = {
            @ApiResponse(
                description = "la richiesta ha avuto successo",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Non sei autorizzato oppure il tuo token non è valido",
                responseCode = "403"
            )
        }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/homeUser") // Mappa le richieste GET all'endpoint "/user/sayHello" a questo metodo
    public ResponseEntity<String> sayHelloUser() {
        // Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti
        // autenticati
        return ResponseEntity.ok("hello from secured endpoint for users"); // Risponde con uno stato HTTP 200 e un
                                                                           // messaggio
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(
        description = "endpoint post per registrazione admin",
        summary = "Questo è il riassunto per l'endpoint registrazioneAdmins",
        responses = {
            @ApiResponse(
                description = "Registrazione admin effettuata con successo",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Non sei autorizzato oppure il tuo token non è valido",
                responseCode = "403"
            ),
            @ApiResponse(
                description = "L'username che hai inserito è già esistente",
                responseCode = "409"  
            ),
            @ApiResponse(
                description = "Inserisci tutti i campi richiesti",
                responseCode = "422"
            )
        }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrazioneAdmins")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        // Registra un nuovo admin e restituisce la risposta dell'autenticazione
        return ResponseEntity.ok(authenticationService.register(request, Role.ADMIN));

    }



}
