//OK
package it.dotit.demo.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import it.dotit.demo.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component // Indica che questa classe è un componente di Spring e sarà gestita dal
           // contesto dell'applicazione
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze necessarie
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Estende OncePerRequestFilter per garantire che il
                                                                    // filtro venga eseguito una sola volta per ogni
                                                                    // richiesta

    private final JwtService jwtService; // Servizio per gestire le operazioni legate al JSON Web Token (JWT)

    private final UserDetailsService userDetailsService; // Servizio per caricare i dettagli dell'utente

    private final TokenRepository tokenRepository; // Repository per gestire i token di accesso

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Estrae l'intestazione di autorizzazione dalla richiesta HTTP
        final String authHeader = request.getHeader("Authorization");
        final String jwt; // Variabile per memorizzare il token JWT
        final String username; // Variabile per memorizzare il nome utente

        // Se l'intestazione di autorizzazione non è presente o non inizia con "Bearer
        // ", prosegue senza autenticazione
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continua con il filtro successivo
            return; // Esce dal metodo
        }

        // Estrae il token JWT dalla stringa dell'intestazione, rimuovendo il prefisso
        // "Bearer "
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt); // Estrae il nome utente dal token JWT

        // Controlla se il nome utente è stato estratto e se non c'è già
        // un'autenticazione nel contesto di sicurezza
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // Carica i dettagli
                                                                                            // dell'utente dal servizio

            // Verifica se il token è valido controllando se esiste nel repository e se non
            // è scaduto o revocato
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            // Verifica se il token è valido rispetto ai dettagli dell'utente
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                // Crea un'istanza di UsernamePasswordAuthenticationToken per rappresentare
                // l'autenticazione dell'utente
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Aggiunge dettagli
                                                                                                  // sulla richiesta
                                                                                                  // all'oggetto di
                                                                                                  // autenticazione

                // Imposta l'oggetto di autenticazione nel contesto di sicurezza di Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continua con il filtro successivo nella catena
        filterChain.doFilter(request, response);
    }
}