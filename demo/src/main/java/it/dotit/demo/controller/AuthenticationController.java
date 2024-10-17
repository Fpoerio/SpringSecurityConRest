package it.dotit.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import it.dotit.demo.auth.AuthenticationRequest; // Classe che rappresenta la richiesta di autenticazione
import it.dotit.demo.auth.AuthenticationResponse; // Classe che rappresenta la risposta dopo l'autenticazione
import it.dotit.demo.auth.RegisterRequest; // Classe che rappresenta la richiesta di registrazione
import it.dotit.demo.service.AuthenticationService; // Servizio che gestisce la logica di autenticazione
import jakarta.servlet.http.HttpServletRequest; // Import per gestire le richieste HTTP
import jakarta.servlet.http.HttpServletResponse; // Import per gestire le risposte HTTP
import lombok.RequiredArgsConstructor; // Libreria per generare automaticamente il costruttore

@RestController // Indica che la classe gestisce le richieste REST e restituisce risposte JSON
@RequestMapping("/nonAutenticato") // Definisce il prefisso per tutte le rotte gestite da questo controller
@RequiredArgsConstructor // Genera un costruttore per facilitare l'iniezione delle dipendenze
public class AuthenticationController {

	private final AuthenticationService service; // Servizio responsabile della gestione dell'autenticazione

	@PostMapping("/registrazione") // Mappa le richieste POST all'endpoint "/registrazione" a questo metodo
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		// Registra un nuovo utente e restituisce la risposta dell'autenticazione
		return ResponseEntity.ok(service.register(request)); // Risponde con il risultato della registrazione
	}

	@PostMapping("/login") // Mappa le richieste POST all'endpoint "/login" a questo metodo
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		// Autentica l'utente e restituisce la risposta dell'autenticazione
		return ResponseEntity.ok(service.authenticate(request)); // Risponde con il risultato dell'autenticazione
	}
	
	@PostMapping("/refresh-token") // Mappa le richieste POST all'endpoint "/refresh-token" a questo metodo
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
		// Gestisce la logica per il rinnovo del token JWT
		service.refreshToken(request, response); // Chiama il servizio per rinnovare il token
	}
}