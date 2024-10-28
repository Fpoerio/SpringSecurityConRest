package it.dotit.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import it.dotit.demo.auth.AuthenticationRequest;
import it.dotit.demo.auth.AuthenticationResponse;
import it.dotit.demo.auth.RegisterRequest;
import it.dotit.demo.model.Role;
import it.dotit.demo.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController // Indica che la classe gestisce le richieste REST e restituisce risposte JSON
@RequestMapping("/nonAutenticato") // Definisce il prefisso per tutte le rotte gestite da questo controller
@RequiredArgsConstructor // Genera un costruttore per facilitare l'iniezione delle dipendenze
public class AuthenticationController {

	private final AuthenticationService service; // Servizio responsabile della gestione dell'autenticazione




///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(
        description = "endpoint post per registrazione user",
        summary = "Questo è il riassunto per l'endpoint registrazione",
        responses = {
            @ApiResponse(
                description = "Registrazione user effettuata con successo",
                responseCode = "200"
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
	@PostMapping("/registrazione") // Mappa le richieste POST all'endpoint "/registrazione" a questo metodo
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		// Registra un nuovo utente e restituisce la risposta dell'autenticazione
		return ResponseEntity.ok(service.register(request, Role.USER)); // Risponde con il risultato della
																			// registrazione
	}



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



	@Operation(
        description = "endpoint post per accesso utente",
        summary = "Questo è il riassunto per l'endpoint login",
        responses = {
            @ApiResponse(
                description = "accesso effettuato con successo",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "L'username e/o la password sono errati",
                responseCode = "403"  
            ),
            @ApiResponse(
                description = "Inserisci tutti i campi richiesti",
                responseCode = "422"
            )
        }
    )
	@PostMapping("/login") // Mappa le richieste POST all'endpoint "/login" a questo metodo
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		// Autentica l'utente e restituisce la risposta dell'autenticazione
		return ResponseEntity.ok(service.authenticate(request)); // Risponde con il risultato dell'autenticazione
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Operation(
		description = "endpoint post per refresh token",
		summary = "Questo è il riassunto per l'endpoint refresh-token",
		responses = {
			@ApiResponse(
				description = "operazione effettuata con successo",
				responseCode = "200"
			),
			@ApiResponse(
				description = "refresh token non presente o errato",
				responseCode = "403"  
			)
		}
	)
	@PostMapping("/refresh-token") // Mappa le richieste POST all'endpoint "/refresh-token" a questo metodo
	public void refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws StreamWriteException, DatabindException, IOException {
		// Gestisce la logica per il rinnovo del token JWT
		service.refreshToken(request, response); // Chiama il servizio per rinnovare il token
	}
}