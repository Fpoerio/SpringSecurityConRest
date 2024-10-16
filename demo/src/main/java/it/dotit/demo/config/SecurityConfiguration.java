package it.dotit.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration // Indica che questa classe contiene configurazioni di bean
@EnableWebSecurity // Abilita la sicurezza web per l'applicazione
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter; // Filtro per l'autenticazione JWT

    private final AuthenticationProvider authenticationProvider; // Provider di autenticazione

    @Bean // Indica che questo metodo è un bean gestito da spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disabilita la protezione CSRF
            .authorizeHttpRequests(auth -> auth // Configura l'autorizzazione delle richieste HTTP
                .requestMatchers("/nonAutenticato/**").permitAll() // Permette l'accesso a tutte le richieste a questo endpoint
                .requestMatchers("/user/**").hasAuthority("USER") // Richiede l'autorità "USER" per accedere a questo endpoint
                .requestMatchers("/admin/**").hasAuthority("ADMIN") // Richiede l'autorità "ADMIN" per accedere a questo endpoint
            )
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Imposta la politica di sessione su stateless (senza stato)
            )
            .authenticationProvider(authenticationProvider) // Imposta il provider di autenticazione
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Aggiunge il filtro JWT prima del filtro di autenticazione standard

        return http.build(); // Costruisce e restituisce la catena di filtri di sicurezza
    }
}