package it.dotit.demo.service;

import it.dotit.demo.model.Csv;
import it.dotit.demo.repository.CsvRepository;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);


    private final CsvRepository repository;

   @Value("${application.pathTxt}")
   private String pathTxt;

    @Transactional
    public void importCSV(String filePath) throws CsvBadConverterException, IllegalStateException, IOException {
        File zipFile = new File(filePath);

        // Verifica se il file esiste ed è un file ZIP valido
        if (!zipFile.exists() || !zipFile.getName().endsWith(".zip")) {
            logger.error("Il file deve essere un file ZIP valido.");
            throw new IllegalArgumentException("The file must be a valid ZIP file.");
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;

            // Processa ogni voce nel file ZIP
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Controlla se l'entry è un file CSV
                if (entry.getName().endsWith(".csv")) {
                    logger.info("Inizio della lettura del file CSV: {}", entry.getName());

                    try (InputStreamReader inputStreamReader = new InputStreamReader(zipInputStream);
                         BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                        // Leggi la prima riga per ottenere gli headers
                        String headerLine = bufferedReader.readLine();
                        // Verifica la validità degli headers
                        if (!checkHeaders(headerLine)) {
                            logger.error("I nomi delle colonne non sono validi.");
                            throw new IllegalArgumentException("Invalid CSV headers.");
                        }
                    } catch (Exception e) {
                        logger.error("Errore durante il controllo del CSV: {}", e.getMessage());

                    }
                }
                zipInputStream.closeEntry(); // Chiudi l'entry corrente
            }
        } catch (IOException e) {

            logger.error("Errore durante la lettura del file ZIP: {}", e.getMessage());
        }

        // Riprocessa il file ZIP per analizzare i dati CSV
        try (ZipInputStream zipInputStream1 = new ZipInputStream(new FileInputStream(zipFile))) {
            @SuppressWarnings("unused")
            ZipEntry entry1;

            while ((entry1 = zipInputStream1.getNextEntry()) != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(zipInputStream1);
                HeaderColumnNameMappingStrategy<Csv> strategy = new HeaderColumnNameMappingStrategy<>();
                strategy.setType(Csv.class);

                // Configura il parser per il CSV
                CsvToBean<Csv> csvToBean = new CsvToBeanBuilder<Csv>(inputStreamReader)
                        .withMappingStrategy(strategy)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // Parsing dei dati CSV in una lista di oggetti Csv
                List<Csv> newList = csvToBean.parse();
                logger.info("Lettura completata. Numero di record letti: {}", newList.size());

                // Sincronizza i dati letti con il database
                syncCsvData(newList);
            }
        } catch (IOException e) {

            logger.error("Errore durante la lettura del file ZIP: {}", e.getMessage());
        }
    }

    @Transactional
    public void syncCsvData(List<Csv> newList) {
        logger.info("Inizio della sincronizzazione dei dati CSV.");

        // Recupera i record esistenti dal database
        List<Csv> existingList = repository.findAll();
        logger.info("Trovati {} record esistenti nel database.", existingList.size());

        // Crea una mappa per accesso rapido ai record esistenti tramite ID
        Map<Long, Csv> existingMap = existingList.stream()
                .collect(Collectors.toMap(Csv::getIdMio, csv -> csv));

        // Aggiorna o inserisce i record dalla nuova lista
        for (Csv newEntity : newList) {
            Csv existingEntity = existingMap.get(newEntity.getIdMio());
            if (existingEntity != null) {
                boolean updated = false;

                // Controlla se i campi sono cambiati
                if (!existingEntity.getFirst_name().equals(newEntity.getFirst_name())
                        || !existingEntity.getLast_name().equals(newEntity.getLast_name())
                        || !existingEntity.getGender().equals(newEntity.getGender())
                        || !existingEntity.getCountry().equals(newEntity.getCountry())
                        || !existingEntity.getAge().equals(newEntity.getAge())
                        || !existingEntity.getDate().equals(newEntity.getDate())) {
                    updated = true;
                }

                // Se ci sono aggiornamenti, salva l'entità aggiornata
                if (updated) {
                    repository.save(newEntity);
                    logger.info("Record aggiornato: {}", existingEntity.getIdMio());
                }
                existingMap.remove(newEntity.getIdMio()); // Rimuovi l'ID dalla mappa
            } else {
                // Se il record non esiste, aggiungilo
                repository.save(newEntity);
                logger.info("Aggiunta nuovo record: {}", newEntity.getIdMio());
            }
        }

        // Rimuove i record non più presenti nella nuova lista
        for (Csv entityToRemove : existingMap.values()) {
            logger.info("Rimozione record non presente: {}", entityToRemove.getIdMio());
            insertIntoTxtRemovedEntity(entityToRemove);
            repository.delete(entityToRemove); // Elimina il record
        }

        logger.info("Sincronizzazione completata.");
    }

    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "idMio", "first_name", "last_name", "gender", "country", "age", "date", "id");

    private boolean checkHeaders(String headerLine) {
        // Suddivide gli headers in base alla virgola
        String[] headers = headerLine.split(",");
        Set<String> headerSet = Set.of(headers);

        // Verifica se gli headers sono quelli richiesti
        return headerSet.equals(REQUIRED_HEADERS);
    }

    private void insertIntoTxtRemovedEntity(Csv removedEntity) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathTxt, true))) {
            writer.write("Record rimosso: Id_Mio: " + removedEntity.getIdMio() +
                    ", First_Name: " + removedEntity.getFirst_name() +
                    ", Last_Name: " + removedEntity.getLast_name() +
                    ", Gender: " + removedEntity.getGender() +
                    ", Country: " + removedEntity.getCountry() +
                    ", Age: " + removedEntity.getAge() +
                    ", Date: " + removedEntity.getDate() +
                    ", Date eliminato: " + LocalDate.now());
            logger.info(LocalDate.now().toString());
            writer.newLine();
            logger.info("Aggiunta del record rimosso nel file txt");
        } catch (IOException e) {
            logger.error("Errore durante la scrittura nel file di log: {}", e.getMessage());
        }
    }

}