package api.java;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.renditions.RenditionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import examples.models.Claim;
import examples.models.ClaimForm;
import examples.stores.ClaimFormStore;
import examples.repositories.ClaimRepository;
import examples.config.JpaConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaConfig.class, RenditionTestConfig.class })
public class RenditionTest {

	@Autowired
	private ClaimRepository claimRepo;
	
	@Autowired
	private ClaimFormStore claimFormStore;
	
	@Autowired
	private RenditionService renditions;
	
    private Claim claim;
    
    @Before
    public void setUp() throws Exception {
    	
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
    public void testRenditionService() {
    	assertThat(renditions, is(not(nullValue())));
    }
    
    @Test
    public void testRendition() throws Exception {
    	InputStream is = claimFormStore.getRendition(claim.getClaimForm(), "text/plain");
    	String content = IOUtils.toString(is);
		assertThat(content, is(not(nullValue())));
		assertThat(content, is("This is the Document Title and this is the document body."));
    }
}
