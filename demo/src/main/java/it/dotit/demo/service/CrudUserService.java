package it.dotit.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.dotit.demo.auth.UpdateUserRequest;
import it.dotit.demo.exceptions.myExceptions.MissingFieldsException;
import it.dotit.demo.exceptions.myExceptions.UserNotFoundException;
import it.dotit.demo.exceptions.myExceptions.UsernameAlreadyExistsException;
import it.dotit.demo.model.User;
import it.dotit.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

// Annotazione per indicare che questa classe è un servizio
@Service
@RequiredArgsConstructor
public class CrudUserService {

    private final PasswordEncoder passwordEncoder; // Codificatore per le password

    private final UserRepository repository; // Repository per accedere ai dati degli utenti

    private final AuthenticationService authenticationService;

    public void updateUtente(UpdateUserRequest request) {

        if(!(request.getOldUsername()==null||
            request.getOldUsername().isEmpty()||
                (
                    (
                        request.getPassword()==null||
                        request.getPassword().isEmpty()
                    )
                    &&
                    (
                        request.getUsername()==null ||
                        request.getUsername().isEmpty()
                    )
                )
        )){

            User userToUpdate = repository.searchByUsername(request.getOldUsername());


            if (userToUpdate != null) {
                // Revoca tutti i token esistenti per l'utente da aggiornare
                authenticationService.revokeAllUserTokens(userToUpdate); // Revoca i token
                // esistenti
    
                // Aggiorna username se fornito
                if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                    if (request.getUsername().equals(userToUpdate.getUsername())
                            || !repository.existsByUsername(request.getUsername())) {
                        userToUpdate.setUsername(request.getUsername());
                    } else {
                        throw new UsernameAlreadyExistsException("username già esistente");
                    }
                }
    
    
                // Aggiorna password se fornita
                if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                    userToUpdate.setPassword(passwordEncoder.encode(request.getPassword())); // Codifica la nuova password
                }
    
                repository.save(userToUpdate); // Salva l'utente aggiornato nel database
    
            } else {
                throw new UserNotFoundException("utente da modificare non trovato");
            }
            
        }else{
            throw new MissingFieldsException("old username deve essere obbligatoriamente fornito, inoltre deve essere presente almeno uno fra username e password");
            
        }
    }
}
