package it.dotit.demo.repository; // Pacchetto per la repository degli utenti

import org.springframework.data.jpa.repository.JpaRepository; // Importa l'interfaccia JpaRepository per le operazioni CRUD

import org.springframework.stereotype.Repository; // Importa l'annotazione Repository per indicare che è una classe di accesso ai dati

import it.dotit.demo.model.User; // Importa la classe User dal modello

import java.util.Optional; // Importa la classe Optional per gestire i valori nullabili

// Annotazione per indicare che questa interfaccia è una repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> { // Interfaccia che estende JpaRepository, specificando User come entità e Long come tipo di ID
    // Metodo per trovare un utente per nome utente, restituisce un Optional<User>
    public Optional<User> findByUsername(String username);
    
    // Metodo per verificare se un nome utente esiste già nel sistema, restituisce un booleano
    public boolean existsByUsername(String username);
} 