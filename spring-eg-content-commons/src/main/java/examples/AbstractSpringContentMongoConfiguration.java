package examples;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class AbstractSpringContentMongoConfiguration extends AbstractMongoClientConfiguration {

	private static final Log log = LogFactory.getLog(AbstractSpringContentMongoConfiguration.class);

	@Value("#{environment.MONGODB_URL}")
	private String mongoDbUrl = "mongodb://localhost:27017";

	@Override
	protected String getDatabaseName() {
		return "spring-content";
	}

	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create(mongoDbUrl);
	}

	@Bean
	public GridFsTemplate gridFsTemplate(MappingMongoConverter mongoConverter) {
		return new GridFsTemplate(mongoDbFactory(), mongoConverter);
	}

	@Bean
	public MongoDatabaseFactory mongoDbFactory() {
		return new SimpleMongoClientDatabaseFactory(mongoClient(), getDatabaseName());
	}
}