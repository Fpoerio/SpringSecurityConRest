package it.dotit.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;

@Configuration // Indica che questa classe fornisce configurazioni di bean per l'applicazione
@EnableWebSecurity // Abilita la configurazione di sicurezza per l'applicazione web
@RequiredArgsConstructor // Genera un costruttore per facilitare l'iniezione delle dipendenze
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter; // Filtro per gestire l'autenticazione tramite JWT

    private final AuthenticationProvider authenticationProvider; // Componente per la gestione dell'autenticazione dell'utente
    
    private final LogoutHandler logoutHandler; // Gestore per le operazioni di logout

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configurazione della sicurezza delle richieste HTTP
        http
            .csrf(csrf -> csrf.disable()) // Disabilita la protezione CSRF, utile per le API REST
            .authorizeHttpRequests(auth -> auth // Configura l'autorizzazione per le richieste HTTP
                .requestMatchers("/nonAutenticato/**").permitAll() // Consente l'accesso non autenticato a questo percorso
                .requestMatchers("/user/**").hasAuthority("USER") // Richiede l'autorità "USER" per accedere a questo percorso
                .requestMatchers("/admin/**").hasAuthority("ADMIN") // Richiede l'autorità "ADMIN" per accedere a questo percorso
                .requestMatchers("/shared/**").authenticated()
                .anyRequest().authenticated() // Richiede autenticazione per tutte le altre richieste
            )
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Imposta la gestione delle sessioni come stateless (senza stato)
            )
            .authenticationProvider(authenticationProvider) // Imposta il provider di autenticazione da utilizzare
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Aggiunge il filtro JWT prima del filtro standard per l'autenticazione

        // Configurazione per il logout
        http.logout(logout -> logout
            .logoutUrl("/nonAutenticato/logout") // Specifica l'URL per la richiesta di logout
            .addLogoutHandler(logoutHandler) // Aggiunge un gestore per gestire le operazioni di logout
            .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()) // Pulisce il contesto di sicurezza al termine del logout
        );

        return http.build(); // Costruisce e restituisce la catena di filtri di sicurezza configurata
    }
    
}