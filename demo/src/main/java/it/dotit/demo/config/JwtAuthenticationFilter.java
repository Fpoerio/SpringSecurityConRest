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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component // Indica che questa classe è un componente Spring 
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Estende OncePerRequestFilter per eseguire il filtro una sola volta per richiesta

    private final JwtService jwtService; // Servizio per gestire le operazioni relative al JWT

    private final UserDetailsService userDetailsService; 

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // Estrae l'intestazione di autorizzazione dalla richiesta
        final String authHeader = request.getHeader("Authorization");
        final String jwt; // Variabile per il token JWT
        final String username; // Variabile per il nome utente
        //Questo consente a richieste non autenticate di raggiungere altre parti dell'applicazione, come pagine pubbliche o endpoint che non richiedono autenticazione.
        // Controlla se l'intestazione non è presente oppure non inizia con "Bearer "
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); //prosegue con la catena di filtri
            return;
        }
       
        jwt = authHeader.substring(7); // Estrae il token JWT dalla stringa dell'intestazione (dopo "Bearer ")
        username = jwtService.extractUsername(jwt); // Estrae il nome utente dal token JWT

        // Controlla se il nome utente è non nullo e se non c'è già autenticazione nel contesto di sicurezza
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // Carica i dettagli dell'utente

            // Verifica se il token è valido per l'utente caricato
            if(jwtService.isTokenValid(jwt, userDetails)) {
                // Crea un'istanza di UsernamePasswordAuthenticationToken per l'autenticazione
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Aggiunge dettagli sulla richiesta all'oggetto di autenticazione

                // Imposta l'oggetto di autenticazione nel contesto di sicurezza
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // Prosegue con il filtro successivo nella catena
    }
}