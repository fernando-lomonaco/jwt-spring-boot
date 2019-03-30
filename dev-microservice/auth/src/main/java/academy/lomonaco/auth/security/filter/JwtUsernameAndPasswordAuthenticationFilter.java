package academy.lomonaco.auth.security.filter;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import academy.lomonaco.core.model.ApplicationUser;
import academy.lomonaco.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtConfiguration jwtConfiguration;

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
		SignedJWT signedJWT = createSignedJWT(auth);
		String encryptedToken = encryptToken(signedJWT);
		
		log.info("Token gerado com sucesso, adicionando-o na response(resposta) e ao  header(cabeçalho)");

		// adicionar ,pois o js terá problema de pegar o header
		// habilitando o javascript para pegar os valores
		response.addHeader("Access-Control-Expose-Headers", "XSFR-TOKEN, " + jwtConfiguration.getHeader().getName());

		response.addHeader(jwtConfiguration.getHeader().getName(),
				jwtConfiguration.getHeader().getPrefix() + encryptedToken);
	}
	
	/**
	 * primeiro assina token depois criptografa
	 */
	@SneakyThrows
	private SignedJWT createSignedJWT(Authentication auth) {
		log.info("Inicio da criacao o JWT assinado");
		ApplicationUser applicationUser = (ApplicationUser) auth.getPrincipal();
		JWTClaimsSet jwtClaimsSet = createJWTClaimsSet(auth, applicationUser);
		KeyPair rsaKeys = generateKeyPair();
		log.info("Construindo JWK do RSA Keys");
		//JWK assinar o JSON
		//gera 2 chaves publica e privada
		JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic())
				.keyID(UUID.randomUUID().toString())
				.build();
		
		// passar a chave no header do json já assinado (integridade do token)
		SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
				.jwk(jwk)
				.type(JOSEObjectType.JWT)
				.build(), jwtClaimsSet);
		
		log.info("Assinando o token com a chave RSA privada");
		RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());
		signedJWT.sign(signer);
		log.info("Serializando token '{}'", signedJWT.serialize());
		return signedJWT;
	}
	
	/** 
	 * criando as claims
	 * @param auth
	 * @param applicationUSer
	 * @return
	 */
	private JWTClaimsSet createJWTClaimsSet(Authentication auth, ApplicationUser applicationUSer) {
		log.info("Criando o objeto JwtClaimSet para '{}'", applicationUSer);
		return new JWTClaimsSet.Builder()
				.subject(applicationUSer.getUsername())
				.claim("authorities", auth.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList()))
				.issuer("http://google.com")
				.issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + (jwtConfiguration.getExpiration() * 1000)))
				.build();
				
	}

	@SneakyThrows // joga qualquer execption
	private KeyPair generateKeyPair() {
		log.info("Gerando RSA 2048 bits keys");
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		return generator.genKeyPair();
	}

	private String encryptToken(final SignedJWT signedJWT) throws JOSEException {
		log.info("Iniciando a criptografia do token");

		DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());

		JWEObject jweObject = new JWEObject(
				new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
				.contentType("JWT")
				.build(), new Payload(signedJWT));
		log.info("Encripitando token com chave private do sistema");
		jweObject.encrypt(directEncrypter);
		log.info("Token encripitado");
		return jweObject.serialize();
	}
}
