package academy.lomonaco.security.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import academy.lomonaco.core.model.ApplicationUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityContextUtil {

	private SecurityContextUtil() {

	}

	public static void setSecurityContext(SignedJWT signedJWT) {
		try {
			JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
			String username = claims.getSubject();
			if (username == null) {
				throw new JOSEException("Username nao encontrado do JWT");
			}
			
			List<String> authorities = claims.getStringListClaim("authorities");
			ApplicationUser applicationUser = ApplicationUser
					.builder()
					.id(claims.getLongClaim("userId"))
					.username(username)
					.role(String.join(",", authorities))
					.build();
			
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(applicationUser, null, createAuthorities(authorities));
			auth.setDetails(signedJWT.serialize());
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (Exception e) {
			log.error("Error security context", e);
			SecurityContextHolder.clearContext();// limpa o que esta no context
		}
	}
	
	private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
		return authorities.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

}
