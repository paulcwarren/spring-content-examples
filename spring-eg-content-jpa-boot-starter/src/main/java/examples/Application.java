package examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

/**
 * excludeFilters is only required because we re-use tests from spring-eg-content-commons that 
 * puts spring-data-jpa and spring-data-mongodb on the classpath together
 * 
 * @author paulcwarren
 *
 */

@SpringBootApplication
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
