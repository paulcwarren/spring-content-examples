package examples;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages={"examples"})
public class ExamplesConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
