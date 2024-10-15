package it.dotit.demo.service;



import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.dotit.demo.auth.AuthenticationRequest;
import it.dotit.demo.auth.AuthenticationResponse;
import it.dotit.demo.auth.RegisterRequest;
import it.dotit.demo.config.JwtService;
import it.dotit.demo.model.Role;
import it.dotit.demo.model.User;
import it.dotit.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository repository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtService jwtService;
	
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(RegisterRequest request) {
		Set<Role> roles = new HashSet<>();
	    roles.add(Role.USER);
		User us = User.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.roles(roles)
				.build();
		
		repository.save(us);
		String jwtToken = jwtService.generateToken(us);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					request.getUsername(),
					request.getPassword()
					)
				);
		
		User us = repository.findByUsername(request.getUsername())
				.orElseThrow();
		String jwtToken = jwtService.generateToken(us);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
}
