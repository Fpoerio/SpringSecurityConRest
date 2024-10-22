package it.dotit.demo.model;

// Import delle classi necessarie
import java.time.LocalDate; // Per la gestione delle date


import com.opencsv.bean.CsvBindByName; // Per la mappatura dei campi CSV
import com.opencsv.bean.CsvCustomBindByName; // Per la mappatura dei campi CSV con conversione personalizzata

import jakarta.persistence.Entity; // Per indicare che la classe è un'entità JPA
import jakarta.persistence.Id; // Per indicare la chiave primaria
import jakarta.persistence.Table; // Per specificare il nome della tabella nel database
import lombok.AllArgsConstructor; // Per generare un costruttore con tutti i parametri
import lombok.Builder; // Per il pattern Builder
import lombok.Data; // Per generare metodi comuni come toString, equals, hashCode
import lombok.NoArgsConstructor; // Per generare un costruttore senza parametri

@Entity // Indica che la classe è un'entità JPA
@AllArgsConstructor // Genera un costruttore che accetta tutti i campi come parametri
@NoArgsConstructor // Genera un costruttore senza parametri
@Builder // Abilita il pattern Builder per la creazione dell'oggetto
@Data // Genera automaticamente i metodi getter e setter, toString, equals, e hashCode
@Table(name="Csv") // Specifica il nome della tabella nel database
public class Csv {
    @Id // Indica che questo campo è la chiave primaria
    @CsvBindByName(column = "idMio")
    private Long idMio; // Identificatore unico generato automaticamente per l'entità Csv
    
    @CsvBindByName(column = "id") // Mappa il campo "id" dal CSV
    private Long id; // Identificatore da CSV
    
    @CsvBindByName(column = "first_name") // Mappa il campo "first_name" dal CSV
    private String first_name; // Nome
    
    @CsvBindByName(column = "last_name") // Mappa il campo "last_name" dal CSV
    private String last_name; // Cognome
    
    @CsvBindByName(column = "gender") // Mappa il campo "gender" dal CSV
    private String gender; // Genere
    
    @CsvBindByName(column = "country") // Mappa il campo "country" dal CSV
    private String country; // Paese
    
    @CsvBindByName(column = "age") // Mappa il campo "age" dal CSV
    private Integer age; // Età
    
    @CsvBindByName(column = "date") // Mappa il campo "date" dal CSV
    @CsvCustomBindByName(converter = LocalDateConverter.class, column = "date") // Mappatura personalizzata per la conversione della data
    private LocalDate date; // Data associata


}
