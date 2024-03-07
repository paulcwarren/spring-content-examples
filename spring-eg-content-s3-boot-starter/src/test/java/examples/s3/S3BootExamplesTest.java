package examples.s3;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.content.commons.repository.Store;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import tests.smoke.ContentStoreTests;

import java.net.URISyntaxException;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = S3BootExamplesTest.Application.class)
public class S3BootExamplesTest extends ContentStoreTests {

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
    public static class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }

        @StoreRestResource
        @CrossOrigin(origins="http://localhost:8080")
        public interface FileStore extends Store<String> {}

        @Configuration
        public static class S3StorageConfig {

            private static final String BUCKET = "aws-test-bucket";

            static {
                System.setProperty("spring.content.s3.bucket", BUCKET);
            }

            @Bean
            public String bucket() {
                return BUCKET;
            }

            @Bean
            public S3Client client() throws URISyntaxException {
                S3Client client = LocalStack.getAmazonS3Client();
                HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                        .bucket(BUCKET)
                        .build();

                try {
                    client.headBucket(headBucketRequest);
                } catch (NoSuchBucketException e) {

                    CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                            .bucket(BUCKET)
                            .build();
                    client.createBucket(bucketRequest);
                }

                return client;
            }
        }
    }
}
