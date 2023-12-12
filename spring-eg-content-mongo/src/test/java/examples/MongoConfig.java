package examples;


import examples.mongo.AbstractSpringContentMongoConfiguration;
import org.springframework.content.mongo.config.EnableMongoStores;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import tests.smoke.JpaConfig;

@Configuration
@ComponentScan(excludeFilters={
        @Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = {
                    JpaConfig.class,
                })
})
@EnableMongoRepositories
@EnableMongoStores
public class MongoConfig extends AbstractSpringContentMongoConfiguration {

}
