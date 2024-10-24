//OK
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

@Configuration // Indica che questa classe fornisce configurazioni per i bean di Spring
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze dei campi finali
public class ApplicationConfig {

    private final UserRepository userRepository; // Repository per accedere ai dati degli utenti

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // Crea un'istanza di
                                                                                  // DaoAuthenticationProvider
        authProvider.setUserDetailsService(userDetailsService()); // Imposta il UserDetailsService per recuperare i
                                                                  // dettagli dell'utente
        authProvider.setPasswordEncoder(passwordEncoder()); // Imposta il PasswordEncoder per la codifica delle password
        return authProvider; // Restituisce il provider di autenticazione configurato
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Recupera e restituisce il AuthenticationManager dalla
                                                  // configurazione fornita
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Crea e restituisce un BCryptPasswordEncoder per l'hashing delle password
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username) // Recupera l'utente per nome utente dal repository
                .orElseThrow(() -> new UsernameNotFoundException("utente non trovato")); // Lancia un'eccezione se
                                                                                         // l'utente non viene trovato
    }
}
