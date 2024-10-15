package it.dotit.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.dotit.demo.auth.AuthenticationRequest;
import it.dotit.demo.auth.AuthenticationResponse;
import it.dotit.demo.auth.RegisterRequest;
import it.dotit.demo.repository.UserRepository;
import it.dotit.demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/nonAutenticato")
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final AuthenticationService service;
	
	private final UserRepository userRepository;
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
		if(!userRepository.existsByUsername(request.getUsername())) {
			return ResponseEntity.ok(service.register(request));
		}else {
			return ResponseEntity.ok(null);
		}
		
	}
	
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
		
		return ResponseEntity.ok(service.authenticate(request));
	}
	
}
