package academy.lomonaco.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.SignedJWT;

import academy.lomonaco.core.property.JwtConfiguration;
import academy.lomonaco.security.token.converter.TokenConverter;
import academy.lomonaco.security.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * esta classe sera executado em todas as requisicoes garante que sera executado
 * apenas uma vez por request
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {

	private final JwtConfiguration jwtConfiguration;
	private final TokenConverter tokenConverter;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain chain) throws ServletException, IOException {
		String header = request.getHeader(jwtConfiguration.getHeader().getName());

		if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
			chain.doFilter(request, response);// filtro pode estar em request que nao precisa ser autenticada ex: login
			return;
		}

		String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();
		// se o tipo for assinado apenas valida o token
		SecurityContextUtil
				.setSecurityContext(StringUtils.equalsIgnoreCase("signed", jwtConfiguration.getType()) ? validate(token)
						: decryptValidating(token));

		chain.doFilter(request, response);
	}

	// primeiria o token será enviado criptografado e assinado para todos os
	// microservicoes
	@SneakyThrows
	private SignedJWT decryptValidating(String encrpytedToken) {
		String signedToken = tokenConverter.decryptToken(encrpytedToken);
		tokenConverter.validateTokenSignature(signedToken);
		return SignedJWT.parse(signedToken);
	}

	// enviado para services que esteja abaixo do gateway apenas assinado
	@SneakyThrows
	private SignedJWT validate(String signedToken) {
		tokenConverter.validateTokenSignature(signedToken);
		return SignedJWT.parse(signedToken);
	}

}
