package examples.solrboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * exclude={MongoRepositoriesAutoConfiguration.class} is only required because we re-use
 * tests from spring-eg-content-commons that puts spring-data-mongodb on the classpath and causes
 * autoconfiguration to @EnableMongoRepositories, rather than JPA
 * 
 * @author paulcwarren
 *
 */

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude={MongoRepositoriesAutoConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
