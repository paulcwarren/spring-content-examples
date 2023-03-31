package api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.rest.config.RestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tests.smoke.JpaConfig;

@SpringBootApplication
@Import(RestConfiguration.class)
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
	@Configuration
	@Import(JpaConfig.class)
	@EnableFilesystemStores(basePackages="examples")
	public static class ApplicationConfig {
	}
}
