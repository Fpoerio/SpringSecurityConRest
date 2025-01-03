//OK
package it.dotit.demo.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


//classe per richiesta di aggiornamento credenziali utente
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

	private String oldUsername;

	private String username;

	private String password;

}
