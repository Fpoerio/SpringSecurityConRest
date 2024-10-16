package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.dotit.demo.auth.AuthenticationRequest; // Classe per la richiesta di autenticazione
import it.dotit.demo.auth.AuthenticationResponse; // Classe per la risposta di autenticazione
import it.dotit.demo.auth.RegisterRequest; // Classe per la richiesta di registrazione
import it.dotit.demo.repository.UserRepository; // Repository per la gestione degli utenti
import it.dotit.demo.service.AuthenticationService; // Servizio per gestire l'autenticazione
import lombok.RequiredArgsConstructor; // Lombok per generare il costruttore

@RestController // Indica che questa classe gestisce le richieste REST
@RequestMapping("/nonAutenticato") // Mappa le richieste a questo controller a partire da questo prefisso
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze
public class AuthenticationController {

	private final AuthenticationService service; // Servizio per gestire l'autenticazione

	private final UserRepository userRepository; // Repository per accedere ai dati degli utenti

	@PostMapping("/registrazione") // Mappa le richieste POST a "/registrazione" a questo metodo
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(service.register(request)); // Registra l'utente e restituisce la risposta, se l'utente è già esistente restituisce ok ma l'authenticationResponse sarà nulla
	}

	@PostMapping("/login") // Mappa le richieste POST a "/login" a questo metodo
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticate(request)); // Autentica l'utente e restituisce la risposta
	}
}