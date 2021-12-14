package examples.rest;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static examples.utils.Env.setEnv;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.amazonaws.regions.Regions;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;

import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentContentStore;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = S3RestExamplesTest.Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
public class S3RestExamplesTest {

    static {
        Map<String,String> props = new HashMap<>();
        props.put("AWS_REGION", Regions.US_WEST_1.getName());
        try {
            setEnv(props);
        } catch (Exception e) {
            fail("Failed to set environment for test: " + e.getMessage());
            e.printStackTrace();
        }
    }

	@Autowired
	private DocumentRepository repo;

	@Autowired
	private DocumentContentStore store;

    @LocalServerPort
    int port;

    private Document document;

    {
    	Describe("Spring Content REST", () -> {
    		BeforeEach(() -> {
    			RestAssured.port = port;
    		});
    		Context("given a claim", () -> {
    			BeforeEach(() -> {
    		    	document = new Document();
    		    	repo.save(document);
    			});
    			It("should be POSTable with new content with 201 Created", () -> {
    				// assert content does not exist
					when()
						.get("/documents/" + document.getId())
					.then()
						.assertThat()
						.statusCode(HttpStatus.SC_NOT_FOUND);

					String newContent = "This is some new content";

					// POST the new content
					given()
    					.contentType("plain/text")
    					.content(newContent.getBytes())
					.when()
    					.post("/documents/" + document.getId())
					.then()
    					.statusCode(HttpStatus.SC_CREATED);

					// assert that it now exists
					given()
    					.header("accept", "plain/text")
    					.get("/documents/" + document.getId())
					.then()
    					.statusCode(HttpStatus.SC_OK)
    					.assertThat()
    					.contentType(Matchers.startsWith("plain/text"))
    					.body(Matchers.equalTo(newContent));
    			});
    			Context("given that claim has existing content", () -> {
        			BeforeEach(() -> {
        		    	document.setMimeType("plain/text");
        		    	document = store.setContent(document, new ByteArrayInputStream("This is plain text content!".getBytes()));
        		    	repo.save(document);
        			});
    				It("should return the content with 200 OK", () -> {
    					given()
	    					.header("accept", "plain/text")
	    					.get("/documents/" + document.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo("This is plain text content!"));
    				});
    				It("should be POSTable with new content with 200 OK", () -> {
    					String newContent = "This is new content";

    					given()
	    					.contentType("plain/text")
	    					.content(newContent.getBytes())
    					.when()
	    					.post("/documents/" + document.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK);

    					given()
	    					.header("accept", "plain/text")
	    					.get("/documents/" + document.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo(newContent));
    				});
    				It("should be DELETEable with 204 No Content", () -> {
    					given()
    						.delete("/documents/" + document.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NO_CONTENT);

    					// and make sure that it is really gone
    					when()
    						.get("/documents/" + document.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NOT_FOUND);
    				});
    			});
			});
    	});
    }

    @SpringBootApplication
    @ComponentScan(excludeFilters={
            @Filter(type = FilterType.REGEX,
                    pattern = {
                            ".*MongoConfiguration",
            })
    })
    public static class Application {

        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }

        @Configuration
        @Import(JpaConfig.class)
        @EnableS3Stores(basePackages="examples")
        public static class AppConfig {

            private static final String BUCKET = "aws-test-bucket";

            static {
                System.setProperty("spring.content.s3.bucket", BUCKET);
            }

            @Autowired
            private Environment env;

            @Bean
            public S3Client client(/*AWSCredentials awsCredentials*/) throws URISyntaxException {
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

    @Test
    public void noop() {}
}
