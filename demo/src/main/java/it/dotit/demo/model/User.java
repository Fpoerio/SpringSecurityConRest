package it.dotit.demo.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority; // Importa l'interfaccia per le autorità concesse
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importa l'implementazione di GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails; // Importa l'interfaccia UserDetails

import jakarta.persistence.ElementCollection; // Importa l'annotazione per collezioni di elementi
import jakarta.persistence.Entity; // Importa l'annotazione per definire un'entità JPA
import jakarta.persistence.EnumType; // Importa l'enum per specificare il tipo di enumerazione
import jakarta.persistence.Enumerated; // Importa l'annotazione per il mapping di enumerazioni
import jakarta.persistence.FetchType; // Importa l'enum per la strategia di caricamento
import jakarta.persistence.GeneratedValue; // Importa l'annotazione per generazione di valore
import jakarta.persistence.GenerationType; // Importa l'enum per le strategie di generazione
import jakarta.persistence.Id; // Importa l'annotazione per la chiave primaria
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor; // Importa l'annotazione per generare un costruttore con tutti i parametri
import lombok.Builder; // Importa l'annotazione per il pattern builder
import lombok.Data; // Importa l'annotazione per generare metodi getter, setter e toString
import lombok.NoArgsConstructor; // Importa l'annotazione per generare un costruttore senza parametri

@Entity
@AllArgsConstructor // Genera un costruttore con tutti i parametri
@NoArgsConstructor // Genera un costruttore senza parametri
@Builder // Abilita il pattern builder per creare istanze di questa classe
@Data // Genera metodi getter, setter, toString, equals e hashCode
public class User implements UserDetails { // Implementa l'interfaccia UserDetails per l'autenticazione

    @Id // Indica che questo campo è la chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera il valore in modo incrementale
    private Long id; // Identificatore univoco dell'utente
    private String username; // Nome utente
    private String password; // Password dell'utente

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @ElementCollection(fetch = FetchType.EAGER) // Raccoglie una collezione di elementi con caricamento eager
    @Enumerated(EnumType.STRING) // Salva il ruolo come stringa nel database
    private Set<Role> roles; // Ruoli dell'utente (es. ADMIN, USER)

    @Override // Sovrascrive il metodo di UserDetails
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte i ruoli in GrantedAuthority per la gestione della sicurezza
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())) // Converti ogni ruolo in un'istanza di
                                                                                // SimpleGrantedAuthority
                .collect(Collectors.toSet()); // Raccoglie e restituisce come set
    }
}
