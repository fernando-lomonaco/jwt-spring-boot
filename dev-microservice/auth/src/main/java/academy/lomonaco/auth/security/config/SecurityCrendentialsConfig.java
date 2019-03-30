package academy.lomonaco.auth.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import academy.lomonaco.auth.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import academy.lomonaco.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;

/**
 * classe de seguranca
 *
 */
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityCrendentialsConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final JwtConfiguration jwtConfiguration;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
		.and()
			.sessionManagement().sessionCreationPolicy(STATELESS)
		.and() //exception de EntryPoint - qualquer expection retorn Unauthorized
			.exceptionHandling().authenticationEntryPoint((req,resp,e) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
		.and() //sera executado todas as vezes que fizer uma requisicao
			.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration))
		.authorizeRequests()
			.antMatchers(jwtConfiguration.getLoginUrl()).permitAll()
			.antMatchers("/course/admin/**").hasRole("ADMIN")
			.anyRequest().authenticated();
			
	}

	// autentica chamando o metdo finByUsername
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
