package academy.lomonaco.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * classe de configuracao jwt
 *
 */
@Configuration
@ConfigurationProperties(prefix = "jwt.config")
@Getter
@Setter
@ToString
public class JwtConfiguration {

	private String loginUrl = "/login/**";
	@NestedConfigurationProperty
	private Header header = new Header();
	private int expiration = 3600;
	// 32 bits
	private String privateKey = "uSzhHjmnUDgCBPWOBQF6reA8n1area0p";
	private String type = "encrypted";

	@Getter
	@Setter
	public static class Header {
		private String name = "Authorization";
		private String prefix = "Bearer ";
	}

}
