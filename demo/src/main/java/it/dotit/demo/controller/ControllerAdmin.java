package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity; // Importa la classe per gestire le risposte HTTP
import org.springframework.web.bind.annotation.GetMapping; // Importa l'annotazione per gestire le richieste GET
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // Importa l'annotazione per definire il percorso delle richieste
import org.springframework.web.bind.annotation.RestController; // Importa l'annotazione per creare un controller REST

import it.dotit.demo.auth.AuthenticationResponse;
import it.dotit.demo.auth.RegisterRequest;
import it.dotit.demo.auth.UpdateUserRequest;
import it.dotit.demo.model.Role;
import it.dotit.demo.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController // Indica che questa classe è un controller REST, in grado di gestire richieste HTTP e restituire risposte JSON
@RequestMapping("/admin") // Definisce il prefisso per tutte le rotte gestite da questo controller, in questo caso "/admin"
@RequiredArgsConstructor // Genera un costruttore per facilitare l'iniezione delle dipendenze
public class ControllerAdmin {
	
	private final AuthenticationService service;

	@GetMapping("/sayHello") // Mappa le richieste GET all'endpoint "/admin/sayHello" a questo metodo
	public ResponseEntity<String> sayHello() {
		// Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti autenticati come admin
		return ResponseEntity.ok("hello from secured endpoint from admin"); // Risponde con uno stato HTTP 200 e un messaggio
	}
	
	@PostMapping("/registrazione")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		// Registra un nuovo utente e restituisce la risposta dell'autenticazione
		return ResponseEntity.ok(service.register(request, Role.ADMIN)); // Risponde con il risultato della registrazione
	}
	
	@PostMapping("/updateUser")
	public ResponseEntity<String> updateUser(@RequestBody UpdateUserRequest updateUserRequest ){
		return service.updateUserByAdmin(updateUserRequest);
	}
	
}