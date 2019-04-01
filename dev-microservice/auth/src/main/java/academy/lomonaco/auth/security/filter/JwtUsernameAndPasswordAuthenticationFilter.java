package academy.lomonaco.auth.security.filter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;

import academy.lomonaco.core.model.ApplicationUser;
import academy.lomonaco.core.property.JwtConfiguration;
import academy.lomonaco.security.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtConfiguration jwtConfiguration;	
	private final TokenCreator tokenCreator;

	/*
	 * tenta fazer a autenticacao do usuario delegando responsabilidade para o
	 * UserDetailService
	 */
	@Override
	@SneakyThrows // encapsular o requestInputStream no tipo de exception runtime
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		log.info("Tentando a autenticacao...");
		ApplicationUser applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
		if (applicationUser == null) {
			throw new UsernameNotFoundException("Nao foi possivel recuperar o usuario ou a senha");
		}
		log.info("Criano o objeto de autenticacao para o usuario '{}' e chama UserDetailServiceImpl loadUserByUsername",
				applicationUser.getUsername());

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				applicationUser.getUsername(), applicationUser.getPassword(), Collections.emptyList());

		usernamePasswordAuthenticationToken.setDetails(applicationUser);

		return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
	}

	@Override
	@SneakyThrows
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		log.info("Autenticao com sucesso para o usuario '{}', gerando o JWE token", auth.getName());
		SignedJWT signedJWT = tokenCreator.createSignedJWT(auth);
		String encryptedToken = tokenCreator.encryptToken(signedJWT);
		
		log.info("Token gerado com sucesso, adicionando-o na response(resposta) e ao  header(cabeçalho)");

		// adicionar ,pois o js terá problema de pegar o header
		// habilitando o javascript para pegar os valores
		response.addHeader("Access-Control-Expose-Headers", "XSFR-TOKEN, " + jwtConfiguration.getHeader().getName());

		response.addHeader(jwtConfiguration.getHeader().getName(),
				jwtConfiguration.getHeader().getPrefix() + encryptedToken);
	}
	
	
}
