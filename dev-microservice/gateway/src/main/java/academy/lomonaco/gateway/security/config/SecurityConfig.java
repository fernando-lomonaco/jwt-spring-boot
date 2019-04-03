package academy.lomonaco.gateway.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import academy.lomonaco.core.property.JwtConfiguration;
import academy.lomonaco.gateway.security.filter.GatewayJwtTokenAuthorizationFilter;
import academy.lomonaco.security.config.SecurityTokenConfig;
import academy.lomonaco.security.token.converter.TokenConverter;

/**
 * verificar o que é valido - nao valido
 *
 * o gateway e o repsonsavel para enviar para os servicos downstream o token e o
 * gateway vai decidir se o token é apenas assinado ou assinado e criptografado
 */
@EnableWebSecurity
public class SecurityConfig extends SecurityTokenConfig {

	private final TokenConverter tokenConverter;

	public SecurityConfig(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(new GatewayJwtTokenAuthorizationFilter(jwtConfiguration, tokenConverter),
				UsernamePasswordAuthenticationFilter.class);
		super.configure(http);
	}
}
