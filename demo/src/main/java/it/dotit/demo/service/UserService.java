package it.dotit.demo.service; // Pacchetto per i servizi relativi agli utenti

import org.springframework.security.core.userdetails.UserDetails; // Importa l'interfaccia per i dettagli dell'utente
import org.springframework.security.core.userdetails.UserDetailsService; // Importa l'interfaccia per il servizio dei dettagli dell'utente
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importa l'eccezione per utente non trovato
import org.springframework.security.crypto.password.PasswordEncoder; // Importa l'interfaccia per la codifica delle password
import org.springframework.stereotype.Service; // Importa l'annotazione Service per indicare che è un servizio

import it.dotit.demo.auth.UpdateUserRequest;
import it.dotit.demo.model.User; // Importa la classe User per rappresentare gli utenti
import it.dotit.demo.repository.UserRepository; // Importa la repository degli utenti
import lombok.RequiredArgsConstructor;

// Annotazione per indicare che questa classe è un servizio
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService { // La classe implementa UserDetailsService per fornire dettagli
                                                         // sugli utenti

    private final UserRepository repository; // Repository per accedere ai dati degli utenti

    // Metodo per caricare i dettagli dell'utente dato il nome utente
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username) // Cerca l'utente per nome utente
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username)); // Lancia
                                                                                                      // un'eccezione se
                                                                                                      // non trovato
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()) // Crea un oggetto
                                                                                                   // UserDetails con il
                                                                                                   // nome utente
                .password(user.getPassword()) // Imposta la password dell'utente
                .roles(user.getRoles().toString()) // Imposta i ruoli dell'utente come stringa
                .build(); // Costruisce l'oggetto UserDetails
    }

}