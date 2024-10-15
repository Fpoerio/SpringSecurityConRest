/*package it.dotit.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.dotit.demo.config.JwtService;
import it.dotit.demo.service.UserService;

import jakarta.servlet.http.HttpSession;



@RestController
public class MainController {
	
	@Autowired
	UserService userSer;
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;
	
	@GetMapping("/home")
	public ResponseEntity <String> home(HttpSession session) {
		
		if(jwtService.isTokenValid(null, null)) {
			return new ResponseEntity<>("ok puoi entrare", HttpStatus.OK);
		}else {
			return new ResponseEntity<>("non puoi entrare", HttpStatus.FORBIDDEN);
		}
	}
	
	@GetMapping("/registrazione")
	public String registrazione(Model m, HttpSession session) {
		m.addAttribute("message",session.getAttribute("message"));
		session.removeAttribute("message");
		return "registrazione";
	}
	
	@PostMapping("/registrazione")
	public String postRegistrazione(HttpSession session, @RequestParam String username, @RequestParam String password) {
		session.setAttribute("message", "ciao hai fatto post");
		if(userSer.registrazioneUtente(username, password)) {
			return "redirect:/login";
		}
		return "redirect:/registrazione";
	}

	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	

    @PostMapping("/login")
    public ResponseEntity<String> login(String username, String password,HttpSession session) {
        try {
            // Autenticazione
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Carica i dettagli dell'utente
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Genera il token JWT
            String token = jwtService.generateToken(userDetails);

            session.setAttribute("jwt", token);

            return new ResponseEntity<>("il tuo token: " +token, HttpStatus.OK);

        } catch (Exception e) {
        	return new ResponseEntity<>("accesso negato", HttpStatus.FORBIDDEN);
        }
    }
}
*/