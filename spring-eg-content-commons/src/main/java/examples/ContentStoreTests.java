package examples;

import examples.models.Claim;
import examples.models.ClaimForm;
import examples.repositories.ClaimRepository;
import examples.stores.ClaimFormStore;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class ContentStoreTests extends AssociativeStoreTests {
	
	@Autowired protected ClaimRepository claimRepo;
	@Autowired protected ClaimFormStore claimFormStore;

	protected Claim claim;
    protected Object id;
    
    {
		Describe("ContentStore", () -> {
			
			AfterEach(() -> {
				// delete any existing claim forms
				Iterable<Claim> existingClaims = claimRepo.findAll();
				for (Claim existingClaim : existingClaims) {
					if (existingClaim.getClaimForm() != null && claimFormStore.getContent(existingClaim.getClaimForm()) != null) {
                        String contentId = existingClaim.getClaimForm().getContentId();
                        claimFormStore.unsetContent(existingClaim.getClaimForm());
                        if (existingClaim.getClaimForm() != null) {
                            Assert.assertThat(existingClaim.getClaimForm().getContentId(), is(nullValue()));
                            Assert.assertEquals(existingClaim.getClaimForm().getContentLength(), 0);

                            // double check the content got removed
                            ClaimForm deletedClaimForm = new ClaimForm();
                            deletedClaimForm.setContentId(contentId);
                            InputStream content = claimFormStore.getContent(deletedClaimForm);
                            Assert.assertThat(content, is(nullValue()));
                            IOUtils.closeQuietly(content);
                        }
                    }
				}
				
				// delete existing claims
				claimRepo.deleteAll();
			});
			
			Context("given an Entity with content", () -> {
				
				BeforeEach(() -> {
					claim = new Claim();
					claim.setFirstName("John");
					claim.setLastName("Smith");
					claim.setClaimForm(new ClaimForm());
					claimFormStore.setContent(claim.getClaimForm(), this.getClass().getResourceAsStream("/ACC_IN-1.DOC"));
					claimRepo.save(claim);
				});
				
				It("should be able to store new content", () -> {
					boolean matches = false;
					InputStream content = null;
					try {
						content = claimFormStore.getContent(claim.getClaimForm());
						matches = IOUtils.contentEquals(this.getClass().getResourceAsStream("/ACC_IN-1.DOC"), content);
					} catch (IOException e) {
					} finally {
						IOUtils.closeQuietly(content);
					}
					assertThat(matches, is(true));
				});
				
				It("should have content metadata", () -> {
					Assert.assertThat(claim.getClaimForm().getContentId(), is(notNullValue()));
					Assert.assertThat(claim.getClaimForm().getContentId().trim().length(), greaterThan(0));
					Assert.assertEquals(claim.getClaimForm().getContentLength(), 26624);
				});
				
				Context("when content is updated", () -> {
					BeforeEach(() ->{
						claimFormStore.setContent(claim.getClaimForm(), this.getClass().getResourceAsStream("/claim_form.pdf"));
						claim = claimRepo.save(claim);
					});
					
					It("should have the updated content", () -> {
						boolean matches = false;
						InputStream content = null;
						try {
							content = claimFormStore.getContent(claim.getClaimForm());
							matches = IOUtils.contentEquals(this.getClass().getResourceAsStream("/claim_form.pdf"), content);
						} catch (IOException e) {
						} finally {
							IOUtils.closeQuietly(content);
						}
						assertThat(matches, is(true));
					});
				});
				
				Context("when content is deleted", () -> {
				    BeforeEach(() -> {
                        id = claim.getClaimForm().getContentId();
						claimFormStore.unsetContent(claim.getClaimForm());
						claim = claimRepo.save(claim);
					});

				    AfterEach(() -> {
				        claimRepo.delete(claim);
                    });
					
					It("should have no content", () -> {
                        ClaimForm deletedClaimForm = new ClaimForm();
                        deletedClaimForm.setContentId((String)id);

                        InputStream content = claimFormStore.getContent(deletedClaimForm);
						Assert.assertThat(content, is(nullValue()));
						IOUtils.closeQuietly(content);
					});
					
					It("should have no metadata", () -> {
						Assert.assertThat(claim.getClaimForm().getContentId(), is(nullValue()));
						Assert.assertEquals(claim.getClaimForm().getContentLength(), 0);
					});
				});
			});
		});
    }
    
	@Test
	public void noop() throws IOException {
	}
}