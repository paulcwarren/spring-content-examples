package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import examples.app.Application;
import examples.models.Claim;
import examples.models.ClaimForm;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.ByteArrayInputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = {examples.versioning.Application.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class RestVersioningTest {

	@Autowired
	private VersionedDocumentAndVersioningRepository repo;
	
	@Autowired
	private VersionedDocumentStore store;
	
    @LocalServerPort
    int port;
    
    private VersionedDocument doc;
    
    {
    	Describe("Spring Content REST", () -> {
    		BeforeEach(() -> {
    			RestAssured.port = port;
    			
    			// delete any existing claim forms
//    			Iterable<VersionedDocument> existingClaims = repo.findAll();
//    			for (VersionedDocument existingClaim : existingClaims) {
//    				store.unsetContent(existingClaim);
//    			}
    			
    			// and claims
//    			for (VersionedDocument existingClaim : existingClaims) {
//    				repo.delete(existingClaim);
//    			}
    		});
    		Context("given a claim", () -> {
    			BeforeEach(() -> {
    		    	doc = new VersionedDocument();
    		    	doc.setData("John");
    		    	doc = repo.save(doc);
    			});
    			FIt("should be POSTable with new content with 201 Created", () -> {
    				// assert content does not exist
					when()
						.get("/versionedDocuments/" + doc.getId())
					.then()
						.assertThat()
						.statusCode(HttpStatus.SC_NOT_FOUND);

					String newContent = "This is some new content";
					
					// POST the new content
					given()
    					.contentType("plain/text")
    					.content(newContent.getBytes())
					.when()
    					.put("/versionedDocuments/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_CREATED);
					
					// assert that it now exists
					given()
    					.header("accept", "plain/text")
    					.get("/versionedDocuments/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_OK)
    					.assertThat()
    					.contentType(Matchers.startsWith("plain/text"))
    					.body(Matchers.equalTo(newContent));
    			});
    			Context("given that claim has existing content", () -> {
        			BeforeEach(() -> {
        		    	doc.setMimeType("plain/text");
        		    	store.setContent(doc, new ByteArrayInputStream("This is plain text content!".getBytes()));
//        		    	doc
//        		    	repo.save(doc);
        			});
    				FIt("should return the content with 200 OK", () -> {
    					given()
	    					.header("accept", "plain/text")
	    					.get("/versionedDocuments/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo("This is plain text content!"));
    				});
    				It("should be POSTable with new content with 201 Created", () -> {
    					String newContent = "This is new content";
    					
    					given()
	    					.contentType("plain/text")
	    					.content(newContent.getBytes())
    					.when()
	    					.put("/versionedDocuments/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK);
    					
    					given()
	    					.header("accept", "plain/text")
	    					.get("/versionedDocuments/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo(newContent));
    				});
    				It("should be DELETEable with 204 No Content", () -> {
    					given()
    						.delete("/versionedDocuments/" + doc.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NO_CONTENT);
    					
    					// and make sure that it is really gone
    					when()
    						.get("/versionedDocuments/" + doc.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NOT_FOUND);
    				});
    			});
			});
    	});
    }
    
    @Test
    public void noop() {}
}
