package examples;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

import java.io.ByteArrayInputStream;

import examples.models.Claim;
import examples.models.ClaimForm;
import examples.repositories.ClaimRepository;
import examples.stores.ClaimFormStore;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//@WebAppConfiguration   
public class ClaimRestTest {

	@Autowired
	private ClaimRepository claimRepo;
	
	@Autowired
	private ClaimFormStore claimFormStore;
	
    @Value("${local.server.port}") 
    int port;

    private Claim canSetClaim;
    private Claim canGetClaim;
    private Claim canDelClaim;
    
    @Before
    public void setUp() throws Exception {
    	
        RestAssured.port = port;
    	
		// delete any existing claim forms
		Iterable<Claim> existingClaims = claimRepo.findAll();
		for (Claim existingClaim : existingClaims) {
			claimFormStore.unsetContent(existingClaim.getClaimForm());
		}
		
    	// ensure clean state
    	claimRepo.deleteAll();

    	// create a claim that can set content on
    	canSetClaim = new Claim();
    	canSetClaim.setFirstName("John");
    	canSetClaim.setLastName("Smith");
    	claimRepo.save(canSetClaim);

    	// create a claim that can get content from
    	canGetClaim = new Claim();
    	canGetClaim.setFirstName("John");
    	canGetClaim.setLastName("Smith");
    	claimRepo.save(canGetClaim);
    	canGetClaim.setClaimForm(new ClaimForm());
    	canGetClaim.getClaimForm().setMimeType("plain/text");
    	claimFormStore.setContent(canGetClaim.getClaimForm(), new ByteArrayInputStream("This is plain text content!".getBytes()));
    	claimRepo.save(canGetClaim);

    	// create a doc that can delete content from
    	canDelClaim = new Claim();
    	canDelClaim.setFirstName("John");
    	canDelClaim.setLastName("Smith");
    	claimRepo.save(canDelClaim);
    	canDelClaim.setClaimForm(new ClaimForm());
    	canDelClaim.getClaimForm().setMimeType("plain/text");
    	claimFormStore.setContent(canDelClaim.getClaimForm(), new ByteArrayInputStream("This is plain text content!".getBytes()));
    	claimRepo.save(canDelClaim);
    }

    @Test
    public void canSetContent() {
    	JsonPath response = 
	    	given()
				.contentType("plain/text")
				.content("This is plain text content!".getBytes())
		    .when()
		        .post("/claims/" + canSetClaim.getClaimId() + "/claimForm")
		    .then()
		    	.statusCode(HttpStatus.SC_CREATED)
		    	.extract()
		    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links"));
    	Assert.assertNotNull(response.get("_links.self"));
    	Assert.assertNotNull(response.get("_links.self.href"));
    }

    @Test
    public void canGetContent() {
    	JsonPath response = 
    		given()
    			.header("accept", "application/hal+json")
		        .get("/claims/" + canGetClaim.getClaimId())
		    .then()
		    	.statusCode(HttpStatus.SC_OK)
		    	.extract()
		    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links.claimForm"));
    	Assert.assertNotNull(response.get("_links.claimForm.href"));

    	String contentUrl = response.get("_links.claimForm.href");
    	when()
    		.get(contentUrl)
    	.then()
    		.assertThat()
    			.contentType(Matchers.startsWith("plain/text"))
    			.body(Matchers.equalTo("This is plain text content!"));
    }

    @Test
    public void canDeleteContent() {
    	JsonPath response =
    		given()
    			.header("accept", "application/hal+json")
		        .get("/claims/" + canDelClaim.getClaimId())
		    .then()
		    	.statusCode(HttpStatus.SC_OK)
		    	.extract()
		    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links.claimForm"));
    	Assert.assertNotNull(response.get("_links.claimForm.href"));

    	String contentUrl = response.get("_links.claimForm.href");
    	when()
    		.delete(contentUrl)
    	.then()
    		.assertThat()
    			.statusCode(HttpStatus.SC_NO_CONTENT);

    	// and make sure that it is really gone
    	when()
    		.get(contentUrl)
    	.then()
    		.assertThat()
    			.statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
