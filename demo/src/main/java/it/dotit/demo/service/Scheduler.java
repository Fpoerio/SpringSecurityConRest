package it.dotit.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import io.jsonwebtoken.io.IOException;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.List;
import java.util.ArrayList;

@Component
public class Scheduler {

	private static final Logger logger = LoggerFactory.getLogger(CsvService.class);

	// (fixedRate = 5000)(cron = "0 0 12 * * ?")
	// @Scheduled(cron = "0 0 12 * * ?")
	// @Async
	// (cron = "0 0 1 * * ?")//una volta al mese

	private final String pathTxt = "C:\\Users\\Fabio\\git\\springSecurityRest\\demo\\src\\main\\resources\\txtCsvEliminati.txt";

	@Scheduled(cron = "0 0 12 * * ?") // Esegue il controllo ogni giorno a mezzogiorno
	private void removeOldRecords() throws FileNotFoundException, java.io.IOException {
		LocalDate thresholdDate = LocalDate.now().minusDays(30); // Calcola la data di soglia
		List<String> linesToKeep = new ArrayList<>(); // Lista per le righe da mantenere
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formato della data

		try (BufferedReader reader = new BufferedReader(new FileReader(pathTxt))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", "); // Suddivide la riga in parti
				String removalDateString = null;

				for (String part : parts) {
					if (part.startsWith("Date eliminato: ")) {
						removalDateString = part.substring("Date eliminato: ".length()); // Estrae la data
						break;
					}
				}

				if (removalDateString != null) {
					try {
						LocalDate removalDate = LocalDate.parse(removalDateString, formatter); // Converte la stringa in
																								// LocalDate
						if (removalDate.isAfter(thresholdDate)) {
							linesToKeep.add(line); // Aggiunge la riga se la data è recente
						}
					} catch (DateTimeParseException e) {
						logger.error("Formato data non valido: {}", removalDateString); // Log di errore per formato non
																						// valido
					}
				}
			}
		} catch (IOException e) {
			logger.error("Errore durante la lettura del file: {}", e.getMessage()); // Log di errore per problemi di
																					// lettura
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTxt))) {
			for (String line : linesToKeep) {
				writer.write(line); // Scrive le righe rimanenti nel file
				writer.newLine();
			}
		} catch (IOException e) {
			logger.error("Errore durante la scrittura nel file: {}", e.getMessage()); // Log di errore per problemi di
																						// scrittura
		}

		logger.info("Operazione completata. Righe vecchie rimosse."); // Log di completamento
	}

}
