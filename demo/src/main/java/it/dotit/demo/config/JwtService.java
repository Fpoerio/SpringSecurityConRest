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

@Service // Indica che questa classe è un servizio gestito da Spring, utile per la logica di business
@RequiredArgsConstructor // Genera un costruttore per l'iniezione delle dipendenze necessarie
public class JwtService {

    // La chiave segreta per firmare il token JWT, letta dalle proprietà dell'applicazione
	@Value("${application.security.jwt.secret-key}")
	private String secretKey;
	
	@Value("${application.security.jwt.expiration}") // Tempo di scadenza del token
	private Long jwtExpiration;
	
	@Value("${application.security.jwt.refresh-token.expiration}") // Tempo di scadenza del refresh token
	private Long refreshExpiration;
	
    // Estrae il nome utente dal token JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Utilizza un metodo per estrarre claim specifici
    }
    
    // Estrae un claim specifico dal token utilizzando una funzione di risoluzione
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Estrae tutti i claim dal token
        return claimsResolver.apply(claims); // Applica la funzione per ottenere il claim desiderato
    }

    // Genera un token JWT per un utente specifico, usando una mappa vuota per eventuali claim extra
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Verifica se un token è valido per un dato utente
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Estrae il nome utente dal token
        // Controlla che il nome utente corrisponda e che il token non sia scaduto
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Controlla se il token JWT è scaduto
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Confronta la data di scadenza con la data attuale
    }

    // Estrae la data di scadenza dal token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // Estrae il claim relativo alla scadenza
    }

    // Genera un token JWT con claim extra per un utente specifico
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    
    // Genera un refresh token per un utente
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }
    
    // Costruisce e firma un token JWT, includendo i claim extra e l'utente
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expiration) {
        // Aggiunge i ruoli dell'utente ai claim extra
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList());
        return Jwts
                .builder() // Inizia la costruzione del token JWT
                .setClaims(extraClaims) // Imposta i claim extra
                .setSubject(userDetails.getUsername()) // Imposta il soggetto (username) del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Imposta la data di emissione del token
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Imposta la data di scadenza
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma il token con la chiave segreta
                .compact(); // Genera e restituisce il token compatto
    }

    // Estrae tutti i claim dal token JWT
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // Inizia la costruzione del parser JWT
                .setSigningKey(getSignInKey()) // Imposta la chiave di firma per la validazione
                .build()
                .parseClaimsJws(token) // Analizza il token JWT
                .getBody(); // Restituisce il corpo dei claim
    }

    // Ottiene la chiave di firma decodificando la chiave segreta da Base64
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decodifica la chiave segreta da Base64
        return Keys.hmacShaKeyFor(keyBytes); // Restituisce la chiave per la firma HMAC
    }
}
