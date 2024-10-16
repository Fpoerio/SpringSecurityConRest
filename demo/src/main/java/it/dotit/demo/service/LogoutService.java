package it.dotit.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import it.dotit.demo.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler{

	
	private final TokenRepository tokenRepository;
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Estrae l'intestazione di autorizzazione dalla richiesta
        final String authHeader = request.getHeader("Authorization");
        final String jwt; // Variabile per il token JWT
        //Questo consente a richieste non autenticate di raggiungere altre parti dell'applicazione, come pagine pubbliche o endpoint che non richiedono autenticazione.
        // Controlla se l'intestazione non è presente oppure non inizia con "Bearer "
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
       
        jwt = authHeader.substring(7); // Estrae il token JWT dalla stringa dell'intestazione (dopo "Bearer ")
		var storedToken = tokenRepository.findByToken(jwt)
				.orElse(null);
		if(storedToken != null) {
			storedToken.setExpired(true);
			storedToken.setRevoked(true);
			tokenRepository.save(storedToken);
		}
	}

}
