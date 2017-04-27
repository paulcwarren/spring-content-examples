package examples;


import org.springframework.content.mongo.config.EnableMongoStores;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan
@EnableMongoRepositories
@EnableMongoStores
public class MongoConfig extends AbstractSpringContentMongoConfiguration {

}
