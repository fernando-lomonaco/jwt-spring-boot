package academy.lomonaco.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import academy.lomonaco.core.property.JwtConfiguration;

@SpringBootApplication
@EntityScan({ "academy.lomonaco.core.model" })
@EnableJpaRepositories({ "academy.lomonaco.core.repository" })
@EnableConfigurationProperties(value = JwtConfiguration.class )
@ComponentScan("academy.lomonaco")
public class CourseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseApplication.class, args);
	}

}
