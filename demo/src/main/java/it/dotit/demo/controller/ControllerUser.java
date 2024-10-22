package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RestController; 

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
