package examples.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.content.commons.repository.Store;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@ComponentScan(excludeFilters={
		@Filter(type = FilterType.REGEX,
				pattern = {
						".*MongoConfiguration", 
		})
})
@EnableJpaRepositories(basePackages="examples.repositories")
@EntityScan(basePackages = "examples.models")
@EnableS3Stores(basePackages = "examples.stores")


public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @StoreRestResource
	@CrossOrigin(origins="http://localhost:8080")
    public interface FileStore extends Store<String> {}

	@Configuration
	public static class S3StorageConfig {

		@Value("#{environment.AWS_ACCESS_KEY_ID}")
		private String accessKey;

		@Value("#{environment.AWS_SECRET_KEY}")
		private String secretKey;

		@Value("#{environment.AWS_REGION}")
		private String region;

		@Value("${aws.bucket:#{environment.AWS_BUCKET}}")
		private String bucket;

		@Bean
		public String bucket() {
			return bucket;
		}

		@Bean
		public AmazonS3 client() {
			return AmazonS3ClientBuilder.standard()
						.withCredentials(new AWSStaticCredentialsProvider(
											new BasicAWSCredentials(accessKey,secretKey)))
						.withRegion(Regions.fromName(region))
						.build();
		}
	}
}
