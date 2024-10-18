package it.dotit.demo.controller;

import org.springframework.web.bind.annotation.*;

import it.dotit.demo.service.CsvService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/nonAutenticato")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvservice;

    @PostMapping("/import")
    public String importCSV(@RequestParam String filePath) {
        csvservice.importCSV(filePath); // Assicurati che questo sia il metodo corretto
        return "Importazione completata!";
    }
}
