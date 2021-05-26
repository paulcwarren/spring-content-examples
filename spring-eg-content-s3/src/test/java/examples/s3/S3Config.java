package examples.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.services.s3.AmazonS3;

@Configuration
public class S3Config {

    private static final String BUCKET = "aws-test-bucket";

    static {
        System.setProperty("spring.content.s3.bucket", BUCKET);
    }

	@Autowired
	private Environment env;

	@Bean
	public AmazonS3 client(/*AWSCredentials awsCredentials*/) {
        AmazonS3 client = LocalStack.getAmazonS3Client();
        client.createBucket(BUCKET);
        return client;
	}

	@Bean
	public String bucketName() {
	    return BUCKET;
	}
}
