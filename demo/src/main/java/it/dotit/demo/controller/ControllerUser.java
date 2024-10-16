package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity; // Importa la classe ResponseEntity per gestire le risposte HTTP
import org.springframework.web.bind.annotation.GetMapping; // Importa l'annotazione per mappare le richieste GET
import org.springframework.web.bind.annotation.RequestMapping; // Importa l'annotazione per mappare i percorsi
import org.springframework.web.bind.annotation.RestController; // Importa l'annotazione per definire un controller REST

@RestController // Indica che questa classe gestisce le richieste REST
@RequestMapping("/user") // Mappa le richieste a questo controller a partire dal prefisso "/autenticato"
public class ControllerUser {

	@GetMapping("/sayHello") // Mappa le richieste GET a "/user/sayHello" a questo metodo
	public ResponseEntity<String> sayHello() {
		// Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti autenticati
		return ResponseEntity.ok("hello from secured endpoint for users");
	}
}
