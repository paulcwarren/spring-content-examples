package examples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.MongoClient;

@Configuration
public class AbstractSpringContentMongoConfiguration extends AbstractMongoClientConfiguration {

	private static final Log log = LogFactory.getLog(AbstractSpringContentMongoConfiguration.class);

	@Override
	protected String getDatabaseName() {
        return MongoTestContainer.getTestDbName();
	}

	@Override
    @Bean
	public MongoClient mongoClient() {
        return MongoTestContainer.getMongoClient();
	}

	@Bean
	public GridFsTemplate gridFsTemplate(MappingMongoConverter mongoConverter) {
		return new GridFsTemplate(mongoDbFactory(), mongoConverter);
	}

	@Override
    @Bean
	public MongoDatabaseFactory mongoDbFactory() {
		return new SimpleMongoClientDatabaseFactory(mongoClient(), getDatabaseName());
	}
}