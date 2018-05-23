package examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;

/**
 * exclude={MongoRepositoriesAutoConfiguration.class} is only required because we re-use
 * tests from spring-eg-content-commons that puts spring-data-mongodb on the classpath and causes
 * autoconfiguration to @EnableMongoRepositories, rather than JPA
 * 
 * @author paulcwarren
 *
 */

@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoRepositoriesAutoConfiguration.class})
@ComponentScan(excludeFilters={
		@Filter(type = FilterType.REGEX,
				pattern = {
						".*MongoConfiguration", 
		})
})
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
