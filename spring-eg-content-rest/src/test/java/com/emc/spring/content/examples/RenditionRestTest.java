package com.emc.spring.content.examples;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.emc.spring.content.examples.Claim;
import com.emc.spring.content.examples.ClaimForm;
import com.emc.spring.content.examples.ClaimFormStore;
import com.emc.spring.content.examples.ClaimRepository;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = com.emc.spring.content.examples.Application.class)
@WebAppConfiguration   
@IntegrationTest("server.port:0")  
public class RenditionRestTest {

	@Autowired
	private ClaimRepository claimRepo;
	
	@Autowired
	private ClaimFormStore claimFormStore;
	
    @Value("${local.server.port}")   // 6
    int port;

    private Claim claim;
    
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

    	// create a claim that can get content from
    	claim = new Claim();
    	claim.setFirstName("John");
    	claim.setLastName("Smith");
    	claimRepo.save(claim);
    	claim.setClaimForm(new ClaimForm());
    	claim.getClaimForm().setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    	claimFormStore.setContent(claim.getClaimForm(), this.getClass().getClassLoader().getResourceAsStream("sample-docx2.docx"));
    	claimRepo.save(claim);
    }

    @Test
    public void canGetRendition() {
    	JsonPath response = 
		    when()
		        .get("/claims/" + claim.getClaimId())
		    .then()
		    	.statusCode(HttpStatus.SC_OK)
		    	.extract()
		    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links.claimForm"));
    	Assert.assertNotNull(response.get("_links.claimForm.href"));

    	String contentUrl = response.get("_links.claimForm.href");
    	Response response2 = 
			given()
				.header("Accept", "text/plain")
			.when()
	    		.get(contentUrl)
	    			.andReturn();
    	
    	assertThat(response2.getStatusCode(), is(200)); 
    	assertThat(response2.getHeader("Content-Type"), Matchers.startsWith("text/plain")); 
    	assertThat(response2.getBody().asString(), is("This is the Document Title and this is the document body."));

    	// TODO: assert content
    }
    
    @Test
    public void noRenditionProviderReturns406() {
    	JsonPath response = 
    		    when()
    		        .get("/claims/" + claim.getClaimId())
    		    .then()
    		    	.statusCode(HttpStatus.SC_OK)
    		    	.extract()
    		    		.jsonPath();
        	
        	Assert.assertNotNull(response.get("_links.claimForm"));
        	Assert.assertNotNull(response.get("_links.claimForm.href"));

        	String contentUrl = response.get("_links.claimForm.href");
        	Response response2 = 
    			given()
    				.header("Accept", "what/ever")
    			.when()
    	    		.get(contentUrl)
        	
    	    		.andReturn();
        	assertThat(response2.getStatusCode(), is(406)); 

        	// TODO: assert content
    }
}
