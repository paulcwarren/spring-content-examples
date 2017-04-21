package examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.content.commons.placement.PlacementStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import internal.org.springframework.content.commons.placement.UUIDPlacementStrategy;

import java.util.UUID;

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
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    public static class ApplicationConfiguration {
    }
}
