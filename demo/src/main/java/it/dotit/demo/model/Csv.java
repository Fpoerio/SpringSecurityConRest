package it.dotit.demo.model;

import java.time.LocalDate;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor 
@Builder 
@Data
@Table(name="Csv")
public class Csv {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idMio; 
    
    @CsvBindByName(column = "id")
    private Long id; 
    @CsvBindByName(column = "first_name")
    private String first_name;

    @CsvBindByName(column = "last_name")
    private String last_name;

    @CsvBindByName(column = "gender")
    private String gender;
    
    @CsvBindByName(column = "country")
    private String country;
    
    @CsvBindByName(column = "age")
    private Integer age;
   
    @CsvBindByName(column = "date")
    @CsvCustomBindByName(converter = LocalDateConverter.class, column = "date")
    private LocalDate date;

}
