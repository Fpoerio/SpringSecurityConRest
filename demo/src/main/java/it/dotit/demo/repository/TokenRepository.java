package it.dotit.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.dotit.demo.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>{

	@Query("SELECT t "
			+ "FROM Token t "
			+ "JOIN User u on t.user.id = u.id "
			+ "WHERE u.id = :id AND (t.expired = false OR t.revoked = false)")
	public List<Token> findAllValidTokensByUser(Long id);
	
	Optional<Token> findByToken(String token);
}
