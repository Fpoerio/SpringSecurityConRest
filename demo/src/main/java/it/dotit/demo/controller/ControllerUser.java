package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity; // Importa la classe per gestire le risposte HTTP
import org.springframework.web.bind.annotation.GetMapping; // Importa l'annotazione per gestire le richieste GET
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // Importa l'annotazione per definire il percorso delle richieste
import org.springframework.web.bind.annotation.RestController; // Importa l'annotazione per creare un controller REST

import it.dotit.demo.auth.UpdateUserRequest;
import it.dotit.demo.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController // Indica che questa classe è un controller REST, che gestisce le richieste e restituisce risposte in formato JSON
@RequestMapping("/user") // Definisce il prefisso per tutte le rotte gestite da questo controller, in questo caso "/user"
@RequiredArgsConstructor
public class ControllerUser {
	
	private final AuthenticationService service;

	@GetMapping("/sayHello") // Mappa le richieste GET all'endpoint "/user/sayHello" a questo metodo
	public ResponseEntity<String> sayHello() {
		// Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti autenticati
		return ResponseEntity.ok("hello from secured endpoint for users"); // Risponde con uno stato HTTP 200 e un messaggio
	}
	
	@PostMapping("/updateUser")
	public ResponseEntity<String> updateUser(HttpServletRequest request,@RequestBody UpdateUserRequest updateUserRequest ){
		return service.updateSelf(updateUserRequest, request);
	}
}
