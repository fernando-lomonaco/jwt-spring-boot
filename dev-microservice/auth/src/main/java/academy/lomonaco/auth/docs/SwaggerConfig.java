package academy.lomonaco.auth.docs;

import org.springframework.context.annotation.Configuration;

import academy.lomonaco.core.docs.BaseSwaggerConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

	public SwaggerConfig() {
		super("academy.lomonaco.auth.endpoint.controller");
	}

}
