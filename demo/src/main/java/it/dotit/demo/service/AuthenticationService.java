package it.dotit.demo.service; // Pacchetto per i servizi

import java.io.IOException; // Importa IOException per gestire eccezioni I/O
import java.util.HashSet; // Importa HashSet per collezioni di oggetti unici
import java.util.Set; // Importa Set per gestire collezioni di oggetti unici

import org.springframework.http.HttpHeaders; // Importa HttpHeaders per gestire le intestazioni delle richieste
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // Importa per gestire l'autenticazione
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Rappresenta una richiesta di autenticazione
import org.springframework.security.crypto.password.PasswordEncoder; // Per la codifica delle password
import org.springframework.stereotype.Service; // Indica che questa classe è un servizio

import com.fasterxml.jackson.core.exc.StreamWriteException; // Eccezione per scrittura su flussi
import com.fasterxml.jackson.databind.DatabindException; // Eccezione per problemi di binding
import com.fasterxml.jackson.databind.ObjectMapper; // Per la serializzazione/deserializzazione di oggetti

import it.dotit.demo.auth.AuthenticationRequest; // Classe per la richiesta di autenticazione
import it.dotit.demo.auth.AuthenticationResponse; // Classe per la risposta di autenticazione
import it.dotit.demo.auth.RegisterRequest; // Classe per la richiesta di registrazione
import it.dotit.demo.config.JwtService; // Servizio per generare e validare token JWT
import it.dotit.demo.exceptions.myExceptions.InvalidCredentialsException;
import it.dotit.demo.exceptions.myExceptions.MissingFieldsException;
import it.dotit.demo.exceptions.myExceptions.UsernameAlreadyExistsException;
import it.dotit.demo.model.Role; // Classe per gestire i ruoli degli utenti
import it.dotit.demo.model.Token; // Classe per rappresentare i token
import it.dotit.demo.model.TokenType; // Tipo di token (es. BEARER)
import it.dotit.demo.model.User; // Classe per rappresentare gli utenti
import it.dotit.demo.repository.TokenRepository; // Repository per gestire i token
import it.dotit.demo.repository.UserRepository; // Repository per gestire gli utenti
import jakarta.servlet.http.HttpServletRequest; // Per gestire le richieste HTTP
import jakarta.servlet.http.HttpServletResponse; // Per gestire le risposte HTTP
import lombok.RequiredArgsConstructor; // Genera un costruttore per le dipendenze

@Service // Indica che questa classe è un servizio e sarà gestita da Spring
@RequiredArgsConstructor // Crea un costruttore con parametri per le dipendenze final
public class AuthenticationService {

	private final UserRepository repository; // Repository per accedere ai dati degli utenti

	private final PasswordEncoder passwordEncoder; // Codificatore per le password

	private final TokenRepository tokenRepository; // Repository per gestire i token

	private final JwtService jwtService; // Servizio per gestire token JWT

	private final AuthenticationManager authenticationManager; // Gestore per le operazioni di autenticazione

	// Metodo per registrare un nuovo utente
	public AuthenticationResponse register(RegisterRequest request, Role role) {
		if(request.getPassword()!=null && !request.getPassword().isEmpty() && request.getUsername()!=null && !request.getUsername().isEmpty()){
			if (!repository.existsByUsername(request.getUsername())) { // Verifica se l'utente esiste già
				Set<Role> roles = new HashSet<>(); // Crea un insieme per i ruoli
				roles.add(role); // Aggiunge il ruolo USER
				User us = User.builder() // Costruisce un nuovo oggetto User
						.username(request.getUsername()) // Imposta il nome utente
						.password(passwordEncoder.encode(request.getPassword())) // Imposta e codifica la password
						.roles(roles) // Imposta i ruoli dell'utente
						.build(); // Costruisce l'oggetto User

				var savedUser = repository.save(us); // Salva l'utente nel database
				String jwtToken = jwtService.generateToken(us); // Genera un token JWT
				saveUserToken(savedUser, jwtToken); // Salva il token dell'utente
				String refreshToken = jwtService.generateRefreshToken(us); // Genera un refresh token
				return AuthenticationResponse.builder() // Costruisce la risposta di autenticazione
						.accessToken(jwtToken) // Imposta il token nella risposta
						.refreshToken(refreshToken) // Imposta il refresh token nella risposta
						.build(); // Costruisce l'oggetto AuthenticationResponse
			} else {
				throw new UsernameAlreadyExistsException("username già esistente");
			}
		}else{
			throw new MissingFieldsException("compila tutti i campi");
		}

	}

	// Metodo per autenticare un utente esistente
	public AuthenticationResponse authenticate(AuthenticationRequest request) {

		if(request.getUsername()!=null&&!request.getUsername().isEmpty()&&request.getPassword()!=null&&!request.getPassword().isEmpty()){
			try {
				authenticationManager.authenticate( // Esegue l'autenticazione dell'utente
						new UsernamePasswordAuthenticationToken( // Crea un token di autenticazione
								request.getUsername(), // Nome utente dalla richiesta
								request.getPassword() // Password dalla richiesta
						));
			} catch (BadCredentialsException e) {
				throw new InvalidCredentialsException("Nome utente o password non validi."); // Lancia un'eccezione
																								// personalizzata
			}

			User us = repository.findByUsername(request.getUsername()) // Trova l'utente per nome utente
					.orElseThrow(); // Lancia un'eccezione se l'utente non esiste
			String jwtToken = jwtService.generateToken(us); // Genera un token JWT per l'utente
			revokeAllUserTokens(us); // Revoca i token esistenti per l'utente
			saveUserToken(us, jwtToken); // Salva il nuovo token per l'utente
			String refreshToken = jwtService.generateRefreshToken(us); // Genera un refresh token
			return AuthenticationResponse.builder() // Costruisce la risposta di autenticazione
					.accessToken(jwtToken) // Imposta il token nella risposta
					.refreshToken(refreshToken) // Imposta il refresh token nella risposta
					.build(); // Costruisce l'oggetto AuthenticationResponse			
		}else{
			throw new MissingFieldsException("compila tutti i campi");
		}

	}

	// Salva il token dell'utente nel database
	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder() // Costruisce un nuovo oggetto Token
				.user(user) // Imposta l'utente associato al token
				.token(jwtToken) // Imposta il token JWT
				.tokenType(TokenType.BEARER) // Imposta il tipo di token
				.expired(false) // Indica che il token non è scaduto
				.revoked(false) // Indica che il token non è stato revocato
				.build(); // Costruisce l'oggetto Token
		tokenRepository.save(token); // Salva il token nel repository
	}

	// Revoca tutti i token validi per un utente
	public void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId()); // Trova i token validi per
																						// l'utente
		if (validUserTokens.isEmpty()) // Se non ci sono token validi, esci dal metodo
			return;
		validUserTokens.forEach(token -> { // Per ogni token valido
			token.setExpired(true); // Marca il token come scaduto
			token.setRevoked(true); // Marca il token come revocato
		});
		tokenRepository.saveAll(validUserTokens); // Salva le modifiche ai token nel repository
	}

	// Metodo per gestire il refresh del token
	public void refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws StreamWriteException, DatabindException, IOException {

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // Estrae l'intestazione di
																				// autorizzazione
		final String refreshToken; // Variabile per il token JWT refresh
		final String username; // Variabile per il nome utente

		// Controlla se l'intestazione non è presente oppure non inizia con "Bearer "
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return; // Se non valido, esci dal metodo
		}

		refreshToken = authHeader.substring(7); // Estrae il token JWT dall'intestazione (dopo "Bearer ")
		username = jwtService.extractUsername(refreshToken); // Estrae il nome utente dal token JWT

		// Se il nome utente è non nullo e l'utente non è già autenticato
		if (username != null) {
			var user = this.repository.findByUsername(username).orElseThrow(); // Carica i dettagli dell'utente

			// Verifica se il token è valido per l'utente caricato
			if (jwtService.isTokenValid(refreshToken, user)) {

				var accessToken = jwtService.generateToken(user); // Genera un nuovo token JWT

				revokeAllUserTokens(user); // Revoca tutti i token validi per l'utente
				saveUserToken(user, accessToken); // Salva il nuovo token per l'utente

				var authResponse = AuthenticationResponse.builder() // Costruisce la risposta di autenticazione
						.accessToken(accessToken) // Imposta il nuovo token nella risposta
						.refreshToken(refreshToken) // Mantiene il refresh token nella risposta
						.build(); // Costruisce l'oggetto AuthenticationResponse
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse); // Scrive la risposta nel
																							// flusso di output
			}
		}
	}
}
