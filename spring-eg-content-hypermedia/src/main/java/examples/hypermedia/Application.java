package examples.hypermedia;

import examples.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(excludeFilters={
		@Filter(type = FilterType.REGEX,
				pattern = {
						".*MongoConfiguration", 
		})
})
@EnableAutoConfiguration(exclude = {
		SecurityAutoConfiguration.class
})
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Configuration
	@Import(JpaConfig.class)
	@EnableFilesystemStores(basePackages="examples.stores")
	public static class AppConfig {
		//
	}
}
