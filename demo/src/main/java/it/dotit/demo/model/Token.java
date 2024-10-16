package it.dotit.demo.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity 
@AllArgsConstructor // Genera un costruttore con tutti i parametri
@NoArgsConstructor // Genera un costruttore senza parametri
@Builder // Abilita il pattern builder per creare istanze di questa classe
@Data // Genera metodi getter, setter, toString, equals e hashCode
public class Token {
	@Id
	@GeneratedValue
	private Long id;
	
	private String token;
	
	@Enumerated(EnumType.STRING)
	private TokenType tokenType;
	
	
	private boolean expired;
	
	private boolean revoked;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
}
