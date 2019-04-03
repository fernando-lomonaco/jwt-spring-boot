package academy.lomonaco.gateway.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

import com.netflix.zuul.context.RequestContext;
import com.nimbusds.jwt.SignedJWT;

import academy.lomonaco.core.property.JwtConfiguration;
import academy.lomonaco.security.filter.JwtTokenAuthorizationFilter;
import academy.lomonaco.security.token.converter.TokenConverter;
import academy.lomonaco.security.util.SecurityContextUtil;
import lombok.SneakyThrows;

public class GatewayJwtTokenAuthorizationFilter extends JwtTokenAuthorizationFilter {

	public GatewayJwtTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
		super(jwtConfiguration, tokenConverter);
	}

	@Override
	@SneakyThrows
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain chain) throws ServletException, IOException {
		String header = request.getHeader(jwtConfiguration.getHeader().getName());

		if (header == null || !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
			chain.doFilter(request, response);// filtro pode estar em request que nao precisa ser autenticada ex: login
			return;
		}

		// token criptogradado
		String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

		// requisicao vem do Front-end (criptografado) - faz o decrypt (sem perguntar se
		// é criptografado ou nao
		String signedToken = tokenConverter.decryptToken(token);
		tokenConverter.validateTokenSignature(signedToken);

		// ele que valida os roles
		SecurityContextUtil.setSecurityContext(SignedJWT.parse(signedToken));
		
		// se tiver proprieda assinada sera feito o replace no authorization header
		if (jwtConfiguration.getType().equalsIgnoreCase("signed")) {
			RequestContext.getCurrentContext().addZuulRequestHeader("Authorization",
					jwtConfiguration.getHeader().getPrefix() + signedToken);
		}

		chain.doFilter(request, response);
	}

}
