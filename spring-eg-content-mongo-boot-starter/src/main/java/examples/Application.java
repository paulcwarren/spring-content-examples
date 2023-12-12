package examples;

import examples.mongo.AbstractSpringContentMongoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters={
		@Filter(type = FilterType.REGEX,
				pattern = {
						".*JpaConfig.*", 
		})
})
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Configuration
	public static class MongoConfig extends AbstractSpringContentMongoConfiguration {
	}

}
