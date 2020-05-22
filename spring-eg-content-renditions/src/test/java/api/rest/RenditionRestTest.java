package api.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import examples.models.Claim;
import examples.models.ClaimForm;
import examples.repositories.ClaimFormRepository;
import examples.repositories.ClaimRepository;
import examples.stores.ClaimFormStore;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
public class RenditionRestTest {

	@Autowired
	private ClaimRepository claimRepo;

	@Autowired
	private ClaimFormRepository claimFormRepo;

	@Autowired
	private ClaimFormStore claimFormStore;
	
    @LocalServerPort
    int port;

    private Claim claim;
    
    @Before
    public void setUp() throws Exception {
    	
        RestAssured.port = port;
    	
    	claim = new Claim();
    	claim.setFirstName("John");
    	claim.setLastName("Smith");
    	claimRepo.save(claim);

    	ClaimForm claimForm = new ClaimForm();
    	claimForm.setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    	claimForm = claimFormStore.setContent(claimForm, this.getClass().getClassLoader().getResourceAsStream("sample-docx2.docx"));
    	claimForm = claimFormRepo.save(claimForm);

    	claim.setClaimForm(claimForm);
    	claim = claimRepo.save(claim);
    }

    @Test
    public void canGetRendition() {
    	String contentUrl = "/claimForms/" + claim.getClaimForm().getId();
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
}
