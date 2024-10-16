package it.dotit.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.dotit.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Configuration // Indica che questa classe contiene configurazioni di bean
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze
public class ApplicationConfig {

    private final UserRepository userRepository; // Repository per accedere ai dati degli utenti

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // Imposta il servizio per il recupero dei dettagli utente
        authProvider.setPasswordEncoder(passwordEncoder()); // Imposta l'encoder per la codifica delle password
        return authProvider; // Restituisce il provider di autenticazione
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Restituisce il gestore di autenticazione configurato
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Restituisce un encoder per le password basato su BCrypt
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username) // Cerca l'utente nel repository
                .orElseThrow(() -> new UsernameNotFoundException("user not found")); // Lancia un'eccezione se non trovato
    }
}
