package examples.converter;


import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import examples.models.Claim;
import examples.models.ClaimForm;
import examples.repositories.ClaimRepository;
import examples.rest.Application;
import examples.stores.ClaimFormStore;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.content.fs.config.FilesystemStoreConfigurer;
import org.springframework.content.fs.config.FilesystemStoreConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

import java.io.ByteArrayInputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(classes = {Application.class, FsRestWithIdConverterTest.IdConverterConfig.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class FsRestWithIdConverterTest {

	@Autowired
	private ClaimRepository claimRepo;
	
	@Autowired
	private ClaimFormStore claimFormStore;
	
    @LocalServerPort
    int port;
    
    private Claim existingClaim;
    
    {
    	Describe("Spring Content REST", () -> {
    		BeforeEach(() -> {
    			RestAssured.port = port;
    			
    			// delete any existing claim forms
//    			Iterable<Claim> existingClaims = claimRepo.findAll();
//    			for (Claim existingClaim : existingClaims) {
//    				claimFormStore.unsetContent(existingClaim.getClaimForm());
//    			}
    			
    			// and claims
//    			for (Claim existingClaim : existingClaims) {
//    				claimRepo.delete(existingClaim);
//    			}
    		});
    		Context("given a claim", () -> {
    			BeforeEach(() -> {
    		    	existingClaim = new Claim();
    		    	existingClaim.setFirstName("John");
    		    	existingClaim.setLastName("Smith");
    		    	claimRepo.save(existingClaim);
    			});
    			It("should be POSTable with new content with 201 Created", () -> {
    				// assert content does not exist
					when()
						.get("/claims/" + existingClaim.getClaimId() + "/claimForm")
					.then()
						.assertThat()
						.statusCode(HttpStatus.SC_NOT_FOUND);

					String newContent = "This is some new content";
					
					// POST the new content
					given()
    					.contentType("plain/text")
    					.content(newContent.getBytes())
					.when()
    					.post("/claims/" + existingClaim.getClaimId() + "/claimForm")
					.then()
    					.statusCode(HttpStatus.SC_CREATED);
					
					// assert that it now exists
					given()
    					.header("accept", "plain/text")
    					.get("/claims/" + existingClaim.getClaimId() + "/claimForm")
					.then()
    					.statusCode(HttpStatus.SC_OK)
    					.assertThat()
    					.contentType(Matchers.startsWith("plain/text"))
    					.body(Matchers.equalTo(newContent));
    			});
    			Context("given that claim has existing content", () -> {
        			BeforeEach(() -> {
        		    	existingClaim.setClaimForm(new ClaimForm());
        		    	existingClaim.getClaimForm().setMimeType("plain/text");
        		    	claimFormStore.setContent(existingClaim.getClaimForm(), new ByteArrayInputStream("This is plain text content!".getBytes()));
        		    	claimRepo.save(existingClaim);
        			});
    				It("should return the content with 200 OK", () -> {
    					given()
	    					.header("accept", "plain/text")
	    					.get("/claims/" + existingClaim.getClaimId() + "/claimForm")
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo("This is plain text content!"));
    				});
    				It("should be POSTable with new content with 200 Created", () -> {
    					String newContent = "This is new content";
    					
    					given()
	    					.contentType("plain/text")
	    					.content(newContent.getBytes())
    					.when()
	    					.post("/claims/" + existingClaim.getClaimId() + "/claimForm")
    					.then()
	    					.statusCode(HttpStatus.SC_OK);
    					
    					given()
	    					.header("accept", "plain/text")
	    					.get("/claims/" + existingClaim.getClaimId() + "/claimForm")
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo(newContent));
    				});
    				It("should be DELETEable with 204 No Content", () -> {
    					given()
    						.delete("/claims/" + existingClaim.getClaimId() + "/claimForm")
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NO_CONTENT);
    					
    					// and make sure that it is really gone
    					when()
    						.get("/claims/" + existingClaim.getClaimId() + "/claimForm")
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
    		return new FilesystemStoreConverter<String,String>() {

				@Override
				public String convert(String source) {
					return String.format("/%s", source.replaceAll("-", "/"));
				}
    		};
    	}
    	
    	@Bean
    	public FilesystemStoreConfigurer configurer() {
    		return new FilesystemStoreConfigurer() {

    			@Override
    			public void configureFilesystemStoreConverters(ConverterRegistry registry) {
    				registry.addConverter(converter());
    			}
    		};
    	}
    }
    
    @Test
    public void noop() {}
}
