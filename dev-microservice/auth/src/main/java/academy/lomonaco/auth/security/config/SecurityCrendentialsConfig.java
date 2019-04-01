package academy.lomonaco.auth.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import academy.lomonaco.auth.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import academy.lomonaco.core.property.JwtConfiguration;
import academy.lomonaco.security.config.SecurityTokenConfig;
import academy.lomonaco.security.token.creator.TokenCreator;

/**
 * classe de seguranca
 *
 */
@EnableWebSecurity
public class SecurityCrendentialsConfig extends SecurityTokenConfig {

	private final UserDetailsService userDetailsService;
	private final TokenCreator tokenCreator;
	
	public SecurityCrendentialsConfig(JwtConfiguration jwtConfiguration, UserDetailsService userDetailsService,
			TokenCreator tokenCreator) {
		super(jwtConfiguration);
		this.userDetailsService = userDetailsService;
		this.tokenCreator = tokenCreator;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		//sera executado todas as vezes que fizer uma requisicao
		.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration, tokenCreator));
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
