package it.dotit.demo.service; // Pacchetto per i servizi

import org.springframework.security.core.Authentication; // Importa l'interfaccia Authentication per gestire l'autenticazione
import org.springframework.security.web.authentication.logout.LogoutHandler; // Importa l'interfaccia LogoutHandler per gestire il logout
import org.springframework.stereotype.Service; // Importa l'annotazione Service per indicare che è un servizio

import it.dotit.demo.repository.TokenRepository; // Importa il repository per gestire i token
import jakarta.servlet.http.HttpServletRequest; // Importa per gestire le richieste HTTP
import jakarta.servlet.http.HttpServletResponse; // Importa per gestire le risposte HTTP
import lombok.RequiredArgsConstructor; // Importa l'annotazione per generare un costruttore

@Service // Indica che questa classe è un servizio e sarà gestita da Spring
@RequiredArgsConstructor // Crea un costruttore per le dipendenze final
public class LogoutService implements LogoutHandler { // Implementa l'interfaccia LogoutHandler

	private final TokenRepository tokenRepository; // Repository per gestire i token

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Estrae l'intestazione di autorizzazione dalla richiesta
        final String authHeader = request.getHeader("Authorization"); // Recupera l'intestazione Authorization
        final String jwt; // Variabile per il token JWT
        
        // Controlla se l'intestazione non è presente oppure non inizia con "Bearer "
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // Esce dal metodo se l'intestazione non è valida
        }
       
        jwt = authHeader.substring(7); // Estrae il token JWT dalla stringa dell'intestazione (dopo "Bearer ")
		var storedToken = tokenRepository.findByToken(jwt) // Cerca il token nel repository
				.orElse(null); // Restituisce null se il token non esiste
		if(storedToken != null) { // Se il token esiste
			storedToken.setExpired(true); // Marca il token come scaduto
			storedToken.setRevoked(true); // Marca il token come revocato
			tokenRepository.save(storedToken); // Salva le modifiche nel repository
		}
	}
}
