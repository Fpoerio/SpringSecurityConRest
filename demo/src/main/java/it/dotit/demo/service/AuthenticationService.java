package it.dotit.demo.service; // Pacchetto per i servizi 

import java.util.HashSet; // Importa la classe HashSet per creare un insieme di ruoli
import java.util.Set; // Importa l'interfaccia Set per gestire collezioni di oggetti unici

import org.springframework.security.authentication.AuthenticationManager; // Importa l'interfaccia per la gestione dell'autenticazione
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Importa la classe per rappresentare una richiesta di autenticazione
import org.springframework.security.crypto.password.PasswordEncoder; // Importa l'interfaccia per la codifica delle password
import org.springframework.stereotype.Service; // Importa l'annotazione Service per indicare che è un servizio

import it.dotit.demo.auth.AuthenticationRequest; // Importa la classe per la richiesta di autenticazione
import it.dotit.demo.auth.AuthenticationResponse; // Importa la classe per la risposta all'autenticazione
import it.dotit.demo.auth.RegisterRequest; // Importa la classe per la richiesta di registrazione
import it.dotit.demo.config.JwtService; // Importa il servizio JWT per la generazione dei token
import it.dotit.demo.model.Role; // Importa la classe Role per gestire i ruoli degli utenti
import it.dotit.demo.model.User; // Importa la classe User per rappresentare gli utenti
import it.dotit.demo.repository.UserRepository; // Importa la repository degli utenti
import lombok.RequiredArgsConstructor; // Importa l'annotazione per la generazione automatica del costruttore

// Annotazione per indicare che questa classe è un servizio
@Service
@RequiredArgsConstructor // Genera un costruttore con parametri per le dipendenze final
public class AuthenticationService {

	private final UserRepository repository; // Repository per accedere ai dati degli utenti
	
	private final PasswordEncoder passwordEncoder; // Codificatore per le password
	
	private final JwtService jwtService; // Servizio per la generazione e validazione dei token JWT
	
	private final AuthenticationManager authenticationManager; // Gestore per le operazioni di autenticazione
	
	// Metodo per registrare un nuovo utente
	public AuthenticationResponse register(RegisterRequest request) {
		if(!repository.existsByUsername(request.getUsername())) {
			Set<Role> roles = new HashSet<>(); // Crea un nuovo insieme di ruoli
		    roles.add(Role.USER); // Aggiunge il ruolo USER all'insieme
			User us = User.builder() // Costruisce un nuovo oggetto User
					.username(request.getUsername()) // Imposta il nome utente
					.password(passwordEncoder.encode(request.getPassword())) // Codifica e imposta la password
					.roles(roles) // Imposta i ruoli dell'utente
					.build(); // Costruisce l'oggetto User
			
			repository.save(us); // Salva l'utente nel repository
			String jwtToken = jwtService.generateToken(us); // Genera un token JWT per l'utente
			return AuthenticationResponse.builder() // Costruisce la risposta di autenticazione
					.token(jwtToken) // Imposta il token nella risposta
					.build(); // Costruisce l'oggetto AuthenticationResponse			
		}else {
			return null;
		}
	}
	
	// Metodo per autenticare un utente esistente
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		
		authenticationManager.authenticate( // Esegue l'autenticazione dell'utente
				new UsernamePasswordAuthenticationToken( // Crea un token di autenticazione
					request.getUsername(), // Nome utente dalla richiesta
					request.getPassword() // Password dalla richiesta
					)
				);
		
		User us = repository.findByUsername(request.getUsername()) // Trova l'utente per nome utente
				.orElseThrow(); // Lancia un'eccezione se l'utente non esiste
		String jwtToken = jwtService.generateToken(us); // Genera un token JWT per l'utente
		return AuthenticationResponse.builder() // Costruisce la risposta di autenticazione
				.token(jwtToken) // Imposta il token nella risposta
				.build(); // Costruisce l'oggetto AuthenticationResponse
	}
}
