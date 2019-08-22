package examples;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.StringUtils;

import java.util.Arrays;

import static java.lang.String.format;

@Configuration
public class AbstractSpringContentMongoConfiguration extends AbstractMongoConfiguration {
	private static final Log log = LogFactory.getLog(AbstractSpringContentMongoConfiguration.class);
	
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

	@Value("#{environment.MONGODB_URL}")
	private String mongoDbUrl;

	@Bean
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
		return new MongoTemplate(mongoDbFactory);
	}

	@Override
	public MongoDbFactory mongoDbFactory() {

		if (StringUtils.isEmpty(mongoDbUrl) == false) {
 			return new SimpleMongoDbFactory(new MongoClientURI(mongoDbUrl));
		} else {

			return new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost/springdocs"));
		}
	}

	@Override
	protected String getDatabaseName() {
		return "springcontent";
	}

	@Override
	public MongoClient mongoClient() {
		return new MongoClient();
	}
}