package academy.lomonaco.course.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import academy.lomonaco.core.property.JwtConfiguration;
import academy.lomonaco.security.config.SecurityTokenConfig;
import academy.lomonaco.security.filter.JwtTokenAuthorizationFilter;
import academy.lomonaco.security.token.converter.TokenConverter;


/**
 * classe de seguranca
 *
 */
@EnableWebSecurity
public class SecurityCrendentialsConfig extends SecurityTokenConfig {

	private final TokenConverter tokenConverter;

	public SecurityCrendentialsConfig(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfiguration, tokenConverter),
				UsernamePasswordAuthenticationFilter.class);
		super.configure(http);
	}

}
