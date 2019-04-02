package academy.lomonaco.auth.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import academy.lomonaco.auth.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import academy.lomonaco.core.property.JwtConfiguration;
import academy.lomonaco.security.config.SecurityTokenConfig;
import academy.lomonaco.security.filter.JwtTokenAuthorizationFilter;
import academy.lomonaco.security.token.converter.TokenConverter;
import academy.lomonaco.security.token.creator.TokenCreator;

/**
 * classe de seguranca
 *
 */
@EnableWebSecurity
public class SecurityCrendentialsConfig extends SecurityTokenConfig {

	private final UserDetailsService userDetailsService;
	private final TokenCreator tokenCreator;
	private final TokenConverter tokenConverter;
	
	public SecurityCrendentialsConfig(JwtConfiguration jwtConfiguration,
			@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, TokenCreator tokenCreator,
			TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.userDetailsService = userDetailsService;
		this.tokenCreator = tokenCreator;
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		//sera executado todas as vezes que fizer uma requisicao
		.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration, tokenCreator))
		.addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfiguration, tokenConverter), UsernamePasswordAuthenticationFilter.class);
		super.configure(http);
	}

	// autentica chamando o metodo finByUsername
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	// password hashing function
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
