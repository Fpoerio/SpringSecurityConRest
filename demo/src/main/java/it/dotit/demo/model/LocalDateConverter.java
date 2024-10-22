package it.dotit.demo.model;

// Import delle classi necessarie
import java.time.LocalDate; // Per la gestione delle date
import java.time.format.DateTimeFormatter; // Per formattare le date
import java.time.format.DateTimeParseException; // Per gestire le eccezioni di parsing delle date

import com.opencsv.bean.AbstractBeanField; // Classe base per la creazione di campi personalizzati in OpenCSV

// Classe che gestisce la conversione di stringhe in oggetti LocalDate
@SuppressWarnings("rawtypes")
public class LocalDateConverter extends AbstractBeanField {

    // Definizione del formato della data
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Metodo sovrascritto per la conversione del valore da stringa a LocalDate
    @Override
    public Object convert(String value) {
        // Controlla se il valore è null o vuoto
        if (value == null || value.isEmpty()) {
            return null; // Restituisce null per valori non validi
        }
        try {
            // Tenta di analizzare la stringa in un oggetto LocalDate utilizzando il formato specificato
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            // Se il formato non è valido, lancia un'eccezione con un messaggio informativo
            throw new IllegalArgumentException("Invalid date format: " + value, e);
        }
    }
}
