package examples.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import examples.config.JpaConfig;

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
	
	@Configuration
	@Import(JpaConfig.class)
	@EnableS3Stores(basePackages="examples")
	public static class AppConfig {
	
		@Autowired
		private Environment env;
		
	    public Region region() {
	        return Region.getRegion(Regions.fromName(System.getenv("AWS_REGION")));
	    }
	
		@Bean
		public BasicAWSCredentials basicAWSCredentials() {
			return new BasicAWSCredentials(env.getProperty("AWS_ACCESS_KEY_ID"), env.getProperty("AWS_SECRET_KEY"));
		}
		
		@Bean
		public AmazonS3 client(AWSCredentials awsCredentials) {
			AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
			amazonS3Client.setRegion(region());
			return amazonS3Client;
		}
	}
}
