package examples.converter;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static examples.utils.Env.setEnv;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.content.s3.config.S3ObjectIdResolvers;
import org.springframework.content.s3.config.S3StoreConfigurer;
import org.springframework.content.s3.config.S3StoreConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

import com.amazonaws.regions.Regions;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;

import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.rest.S3RestExamplesTest;
import examples.stores.DocumentContentStore;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(classes = {S3RestExamplesTest.Application.class, S3RestWithIdConverterTest.IdConverterConfig.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class S3RestWithIdConverterTest {

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
    		Context("given a document", () -> {
    			BeforeEach(() -> {
    		    	document = new Document();
    		    	document = repo.save(document);
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
    			Context("given that document has existing content", () -> {
        			BeforeEach(() -> {
        		    	document = store.setContent(document, new ByteArrayInputStream("This is plain text content!".getBytes()));
        		    	document.setMimeType("plain/text");
        		    	document = repo.save(document);
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

    @Configuration
    public static class IdConverterConfig {

    	public Converter<String,String> converter() {
    		return new S3StoreConverter<String,String>() {

				@Override
				public String convert(String source) {
					return String.format("/%s", source.replaceAll("-", "/"));
				}
    		};
    	}

    	@Bean
    	public S3StoreConfigurer configurer() {
    		return new S3StoreConfigurer() {

    			@Override
    			public void configureS3StoreConverters(ConverterRegistry registry) {
    				registry.addConverter(converter());
    			}

				@Override
				public void configureS3ObjectIdResolvers(S3ObjectIdResolvers resolvers) {
				}
			};
    	}
    }

    @Test
    public void noop() {}
}
