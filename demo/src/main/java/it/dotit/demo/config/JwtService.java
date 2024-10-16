package it.dotit.demo.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service // Indica che questa classe è un servizio gestito da Spring
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze
public class JwtService {

    //private static final String SECRET_KEY = "97aabf4b92e2e29c2f13ff54a26d71c6f1d84e98a02b1cd2151e834d7b0fe817"; // Chiave segreta per la firma del JWT
	
	@Value("${application.security.jwt.secret-key}")
	private String secretKey;
	
	@Value("${application.security.jwt.expiration}")
	private Long jwtExpiration;
	
	@Value("${application.security.jwt.refresh-token.expiration}")
	private Long refreshExpiration;
	
    // Estrae il nome utente dal token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Utilizza il metodo di estrazione dei claim
    }
    
    //In sostanza, i claims aiutano a descrivere chi è un utente e quali permessi ha, permettendo a sistemi e applicazioni di gestire l'accesso in modo efficace.
    // Estrae un claim specifico dal token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Estrae tutti i claim dal token
        return claimsResolver.apply(claims); // Applica la funzione fornita per risolvere il claim
    }

    // Genera un token JWT per un utente specifico
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails); // Passa una mappa vuota per i claim
    }

    // Verifica se un token è valido per un utente specifico
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Estrae il nome utente dal token
        // Verifica che il nome utente corrisponda e che il token non sia scaduto
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Controlla se il token è scaduto
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Confronta la data di scadenza con la data attuale
    }

    // Estrae la data di scadenza dal token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // Estrae il claim di scadenza
    }

    // Genera un token JWT con claim extra per un utente specifico
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {

        return buildToken(new HashMap<>(), userDetails, jwtExpiration);
    }
    
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expiration) {
        // Aggiunge i ruoli dell'utente ai claim extra
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList());
        return Jwts
                .builder() // Inizia la costruzione del token
                .setClaims(extraClaims) // Imposta i claim extra
                .setSubject(userDetails.getUsername()) // Imposta il soggetto (username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Imposta la data di emissione
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Imposta la data di scadenza
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma il token con la chiave segreta e l'algoritmo
                .compact(); // Genera e restituisce il token compatto
    }

    // Estrae tutti i claim dal token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // Inizia la costruzione del parser
                .setSigningKey(getSignInKey()) // Imposta la chiave di firma
                .build()
                .parseClaimsJws(token) // Analizza il token JWT
                .getBody(); // Restituisce il corpo dei claim
    }

    // Ottiene la chiave di firma a partire dalla chiave segreta
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decodifica la chiave segreta da Base64
        return Keys.hmacShaKeyFor(keyBytes); // Restituisce la chiave per la firma HMAC
    }
}
