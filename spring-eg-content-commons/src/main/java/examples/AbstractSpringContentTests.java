package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSpringContentTests {

	@Autowired
	private ClaimRepository claimRepo;
	
	@Autowired 
	private ClaimFormStore claimFormStore;
	
	private Claim claim;
	{
		Describe("Spring Content", () -> {
			
			Context("given an Entity with @Content", () -> {
				
				BeforeEach(() -> {
					claim = new Claim();
					claim.setFirstName("John");
					claim.setLastName("Smith");
					claim.setClaimForm(new ClaimForm());
					claimFormStore.setContent(claim.getClaimForm(), this.getClass().getResourceAsStream("/claim_form.pdf"));
					claimRepo.save(claim);
				});
				
				It("should be able to store new content", () -> {
					Assert.assertTrue(IOUtils.contentEquals(this.getClass().getResourceAsStream("/claim_form.pdf"), claimFormStore.getContent(claim.getClaimForm())));
				});
				
				It("should have content metadata", () -> {
					Assert.assertThat(claim.getClaimForm().getContentId(), is(notNullValue()));
					Assert.assertThat(claim.getClaimForm().getContentId().trim().length(), greaterThan(0));
					Assert.assertEquals(claim.getClaimForm().getContentLength(), 1226609);
				});
				
				Context("when content is updated", () -> {
					BeforeEach(() ->{
						claimFormStore.setContent(claim.getClaimForm(), this.getClass().getResourceAsStream("/ACC_IN-1.DOC"));
						claim = claimRepo.save(claim);
					});
					
					It("should have the updated content", () -> {
						Assert.assertTrue(IOUtils.contentEquals(this.getClass().getResourceAsStream("/ACC_IN-1.DOC"), claimFormStore.getContent(claim.getClaimForm())));
					});
				});
				
				Context("when content is deleted", () -> {
					BeforeEach(() ->{
						claimFormStore.unsetContent(claim.getClaimForm());
						claim = claimRepo.save(claim);
					});
					
					It("should have no content or metadata", () -> {
						Assert.assertThat(claim.getClaimForm().getContentId(), is(nullValue()));
						Assert.assertEquals(claim.getClaimForm().getContentLength(), 0);
						Assert.assertThat(claimFormStore.getContent(claim.getClaimForm()), is(nullValue()));
					});
				});
			});
			
			AfterEach(() -> {
				// delete any existing claim forms
				Iterable<Claim> existingClaims = claimRepo.findAll();
				for (Claim existingClaim : existingClaims) {
					claimFormStore.unsetContent(existingClaim.getClaimForm());
					if (existingClaim.getClaimForm() != null) {
						Assert.assertThat(existingClaim.getClaimForm().getContentId(), is(nullValue()));
						Assert.assertEquals(existingClaim.getClaimForm().getContentLength(), 0);
						Assert.assertThat(claimFormStore.getContent(existingClaim.getClaimForm()), is(nullValue()));
					}
				}
				
				// delete existing claims
				claimRepo.deleteAll();
			});
		});
	}

	@Test
	public void noop() throws IOException {
	}
}