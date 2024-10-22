package it.dotit.demo.repository; 

import org.springframework.data.jpa.repository.JpaRepository; 

import org.springframework.stereotype.Repository; 

import it.dotit.demo.model.User; 

import java.util.Optional; 


// Annotazione per indicare che questa interfaccia è una repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> { // Interfaccia che estende JpaRepository, specificando User come entità e Long come tipo di ID
    // Metodo per trovare un utente per nome utente, restituisce un Optional<User>
    public Optional<User> findByUsername(String username);
    
    // Metodo per verificare se un nome utente esiste già nel sistema, restituisce un booleano
    public boolean existsByUsername(String username);
    
    public User searchByUsername(String username);
} 