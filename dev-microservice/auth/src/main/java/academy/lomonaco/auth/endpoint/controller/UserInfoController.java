package academy.lomonaco.auth.endpoint.controller;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.lomonaco.core.model.ApplicationUser;

@RestController
@RequestMapping("user")
public class UserInfoController {

	@GetMapping(path = "info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	// principal tem as info do contexto
	public ResponseEntity<ApplicationUser> getserInfo(Principal principal) {

		ApplicationUser applicationUser = (ApplicationUser) ((UsernamePasswordAuthenticationToken) principal)
				.getPrincipal();

		return ResponseEntity.ok(applicationUser);

	}
}
