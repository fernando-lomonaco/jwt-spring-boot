package academy.lomonaco.security.token.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import academy.lomonaco.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenConverter {

	private final JwtConfiguration jwtConfiguration;

	@SneakyThrows
	public String decryptToken(String encryptedToken) {
		log.info("Descriptografando token");

		JWEObject jweObject = JWEObject.parse(encryptedToken);
		DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
		jweObject.decrypt(directDecrypter);
		log.info("Token descriptografado, retornando token assinado...");
		return jweObject.getPayload().toSignedJWT().serialize();
	}

	// toda requisicao dever ser executada
	@SneakyThrows
	public void validateTokenSignature(String signedToken) {
		log.info("Iniciando metodo para validar assinatura do token");
		SignedJWT signedJWT = SignedJWT.parse(signedToken);
		log.info("Token Parsed. Recuperando chave publica do token assinado");
		RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
		log.info("Chave publica recuperada, validando assinatura");

		if (!signedJWT.verify(new RSASSAVerifier(publicKey))) {
			throw new AccessDeniedException("Assinatura do token invalida");
		}

		log.info("O token tem uma assinatura válida");
	}

}
