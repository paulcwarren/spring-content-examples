package examples.ecs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.content.s3.config.EnableS3ContentRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;

@Configuration
@EnableJpaRepositories(basePackages="examples")
@EnableS3ContentRepositories(basePackages="examples")
public class ECSConfig {

    @Value("${ecs.url:#{environment.ECS_URL}}")
    private String url;

    @Value("${ecs.accessKey:#{environment.ECS_ACCESS_KEY}}")
    private String accessKey;

    @Value("${ecs.secretKey:#{environment.ECS_SECRET_KEY}}")
    private String secretKey;

    @Value("${ecs.bucket:#{environment.ECS_BUCKET}}")
    private String bucket;

    @Bean
    public String bucket() {
    	return bucket;
    }
    
    @Bean
    public BasicAWSCredentials basicAWSCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }
    
	@Bean
    S3ClientOptions s3ClientOptions() {
        S3ClientOptions opts = new S3ClientOptions();
        opts.setPathStyleAccess(true);
        return opts;
    }

    @Bean
    public AmazonS3Client amazonS3Client(AWSCredentials awsCredentials) {
        AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
        amazonS3Client.setEndpoint(url);
        amazonS3Client.setS3ClientOptions(s3ClientOptions());
        return amazonS3Client;
    }
}
