package it.dotit.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.dotit.demo.model.Csv;



@Repository
public interface CsvRepository extends JpaRepository<Csv,Long> {

}
