package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity; // Importa la classe per gestire le risposte HTTP
import org.springframework.web.bind.annotation.GetMapping; // Importa l'annotazione per gestire le richieste GET
import org.springframework.web.bind.annotation.RequestMapping; // Importa l'annotazione per definire il percorso delle richieste
import org.springframework.web.bind.annotation.RestController; // Importa l'annotazione per creare un controller REST

@RestController // Indica che questa classe è un controller REST, in grado di gestire richieste HTTP e restituire risposte JSON
@RequestMapping("/admin") // Definisce il prefisso per tutte le rotte gestite da questo controller, in questo caso "/admin"
public class ControllerAdmin {

	@GetMapping("/sayHello") // Mappa le richieste GET all'endpoint "/admin/sayHello" a questo metodo
	public ResponseEntity<String> sayHello() {
		// Restituisce una risposta HTTP con un messaggio di benvenuto per gli utenti autenticati come admin
		return ResponseEntity.ok("hello from secured endpoint from admin"); // Risponde con uno stato HTTP 200 e un messaggio
	}
}