package api.rest;

import static com.jayway.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import examples.models.Claim;
import examples.models.ClaimForm;
import examples.stores.ClaimFormStore;
import examples.repositories.ClaimRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
public class RenditionRestTest {

	@Autowired
	private ClaimRepository claimRepo;
	
	@Autowired
	private ClaimFormStore claimFormStore;
	
    @LocalServerPort
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
    	String contentUrl = "/claims/" + claim.getClaimId() + "/claimForm/" + claim.getClaimForm().getContentId();
    	Response response2 = 
			given()
				.header("Accept", "text/plain")
			.when()
	    		.get(contentUrl)
	    			.andReturn();
    	
    	assertThat(response2.getStatusCode(), is(200)); 
    	assertThat(response2.getHeader("Content-Type"), Matchers.startsWith("text/plain")); 
    	assertThat(response2.getBody().asString(), is("This is the Document Title and this is the document body."));
    }
    
    @Test
    public void noRenditionProviderReturns406() {
        	String contentUrl = "/claims/" + claim.getClaimId() + "/claimForm/" + claim.getClaimForm().getContentId();
        	Response response2 = 
    			given()
    				.header("Accept", "what/ever")
    			.when()
    	    		.get(contentUrl)
        	
    	    		.andReturn();
        	assertThat(response2.getStatusCode(), is(406)); 
    }
}
