package it.dotit.demo.controller;



import it.dotit.demo.service.CsvService;



import java.io.File;
import java.io.IOException;



import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import com.opencsv.exceptions.CsvBadConverterException;



import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/nonAutenticato")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvservice;

    @PostMapping("/import")
    public String importCSV(@RequestParam String filePath) throws CsvBadConverterException, IllegalStateException, IOException{
        csvservice.importCSV(filePath); // Assicurati che questo sia il metodo corretto
        return "Importazione completata!";
    }

    @PostMapping("/downloadCsvEliminati")
    public ResponseEntity<Resource> fileTxt() {
        File fileUtentiEliminati = new File("C:\\Users\\Fabio\\git\\springSecurityRest\\demo\\src\\main\\resources\\txtCsvEliminati.txt");
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
