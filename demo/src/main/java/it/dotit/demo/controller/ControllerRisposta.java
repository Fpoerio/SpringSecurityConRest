package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autenticato")
public class ControllerRisposta {

	@GetMapping("/sayHello")
	public ResponseEntity<String> sayHello(){
		return ResponseEntity.ok("hello from secured endpoint for users");
	}
}
