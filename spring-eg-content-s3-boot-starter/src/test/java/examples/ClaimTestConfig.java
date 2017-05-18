package examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.content.s3.config.AbstractS3StoreConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
public class ClaimTestConfig extends AbstractS3StoreConfiguration {

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
    
    @Override
	public SimpleStorageResourceLoader simpleStorageResourceLoader() {
		return new SimpleStorageResourceLoader(client(basicAWSCredentials()));
	}
}
