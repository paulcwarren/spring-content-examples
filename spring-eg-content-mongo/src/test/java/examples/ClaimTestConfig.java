package examples;


import org.springframework.content.mongo.config.EnableMongoContentRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan
@EnableMongoRepositories
@EnableMongoContentRepositories
public class ClaimTestConfig extends AbstractSpringContentMongoConfiguration {

}
