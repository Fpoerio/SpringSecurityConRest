package it.dotit.demo.service;


import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import it.dotit.demo.model.Csv;
import it.dotit.demo.repository.CsvRepository;

import java.io.FileReader;
import java.time.LocalDate;


@Service
public class CsvService {

    @Autowired
    private CsvRepository repository;

        @Transactional
    public void importCSV(String filePath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            // Definisci la strategia di mappatura
            HeaderColumnNameMappingStrategy<Csv> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Csv.class);
            
            CsvToBean<Csv> csvToBean = new CsvToBeanBuilder<Csv>(fileReader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Csv> csvList = csvToBean.parse();
            int count=0;
            for (Csv entity : csvList) {
                repository.save(entity);
                System.out.println("Importate " + count + " righe.");
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Gestisci le eccezioni in modo appropriato
        }
    }
}

