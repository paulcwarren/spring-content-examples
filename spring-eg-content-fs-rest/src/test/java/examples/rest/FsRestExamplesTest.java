package examples.rest;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentContentStore;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
public class FsRestExamplesTest {

	@Autowired
	private DocumentRepository repo;
	
	@Autowired
	private DocumentContentStore store;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private Document doc;
    
    {
    	Describe("Spring Content REST", () -> {
    		BeforeEach(() -> {
				RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    		});
    		Context("given a document", () -> {
    			BeforeEach(() -> {
    		    	doc = new Document();
    		    	repo.save(doc);
    			});
    			It("should be POSTable with new content with 201 Created", () -> {
    				// assert content does not exist
					when()
						.get("/documents/" + doc.getId())
					.then()
						.assertThat()
						.statusCode(HttpStatus.SC_NOT_FOUND);

					String newContent = "This is some new content";

					// POST the new content
					given()
    					.contentType("text/plain")
    					.body(newContent.getBytes())
					.when()
    					.post("/documents/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_CREATED);
					
					// assert that it now exists
					given()
    					.header("accept", "text/plain")
    					.get("/documents/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_OK)
    					.assertThat()
    					.contentType(Matchers.startsWith("text/plain"))
    					.body(Matchers.equalTo(newContent));
    			});
    			Context("given that the document has existing content", () -> {
        			BeforeEach(() -> {
        		    	doc.setMimeType("text/plain");
        		    	doc = store.setContent(doc, new ByteArrayInputStream("This is plain text content!".getBytes()));
        		    	repo.save(doc);
        			});
    				It("should return the content with 200 OK", () -> {
    					given()
	    					.header("accept", "text/plain")
	    					.get("/documents/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("text/plain"))
	    					.body(Matchers.equalTo("This is plain text content!"));
    				});
    				It("should be POSTable with new content with 201 Created", () -> {
    					String newContent = "This is new content";
    					
    					given()
	    					.contentType("text/plain")
	    					.body(newContent.getBytes())
    					.when()
	    					.post("/documents/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK);
    					
    					given()
	    					.header("accept", "text/plain")
	    					.get("/documents/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("text/plain"))
	    					.body(Matchers.equalTo(newContent));
    				});
    				It("should be DELETEable with 204 No Content", () -> {
    					given()
    						.delete("/documents/" + doc.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NO_CONTENT);
    					
    					// and make sure that it is really gone
    					when()
    						.get("/documents/" + doc.getId())
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
