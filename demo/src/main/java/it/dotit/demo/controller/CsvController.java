package it.dotit.demo.controller;



import it.dotit.demo.service.CsvService;



import java.io.File;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/nonAutenticato")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvservice;

    @Value("${application.pathTxt}")
    private String pathTxt;


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Operation(
        description = "endpoint post per import di un csv",
        summary = "Questo è il riassunto per l'endpoint import",
        responses = {
            @ApiResponse(
                description = "import effettuato con successo",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Devi inserire un file zip con un csv ben strutturato",
                responseCode = "400"  
            )
        }
    )
    @PostMapping("/import")
    public ResponseEntity<String> importCSV(@RequestParam String filePath){

        csvservice.importCSV(filePath); // Assicurati che questo sia il metodo corretto

        return ResponseEntity.ok("Importazione completata!");
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    

    @Operation(
        description = "endpoint post per il download dei csv eliminati",
        summary = "Questo è il riassunto per l'endpoint downloadCsvEliminati",
        responses = {
            @ApiResponse(
                description = "download effettuato con successo",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "file non trovato",
                responseCode = "404"  
            )
        }
    )
    @GetMapping("/downloadCsvEliminati")
    public ResponseEntity<Resource> fileTxt() {
        File fileUtentiEliminati = new File(pathTxt);
        if (!fileUtentiEliminati.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(fileUtentiEliminati);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .header("Content-Type", "text/plain") // Imposta il tipo di contenuto
                .body(resource);
    }

}
