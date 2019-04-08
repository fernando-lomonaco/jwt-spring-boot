package academy.lomonaco.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;

import academy.lomonaco.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;

/**
 * classe de seguranca de autenticacao
 *
 * obs.: nao temos @EnableWebSecurity
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

	protected final JwtConfiguration jwtConfiguration;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
		.and()
			.sessionManagement().sessionCreationPolicy(STATELESS)
		.and() //exception de EntryPoint - qualquer expection retorn Unauthorized
			.exceptionHandling().authenticationEntryPoint((req,resp,e) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
		.and() 
		.authorizeRequests()
			.antMatchers(jwtConfiguration.getLoginUrl(), "/**/swagger-ui.html").permitAll()
			.antMatchers(HttpMethod.GET, "/**/swagger-resources/**", "/**/webjars/springfox-swagger-ui/**", "/**/v2/api-docs/**")
				.permitAll()
			.antMatchers("/course/v1/admin/**").hasRole("ADMIN")
			.antMatchers("/auth/user/**").hasAnyRole("USER", "ADMIN")
			.anyRequest().authenticated();
	}

}
