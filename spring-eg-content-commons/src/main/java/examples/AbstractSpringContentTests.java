package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static examples.utils.EventuallyLambdaMatcher.eventuallyEval;
import static examples.utils.RealTimeSeries.sample;
import static examples.utils.EventuallyMatcher.eventually;
import static org.hamcrest.MatcherAssert.assertThat;
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
	
	@Autowired protected ClaimRepository claimRepo;
	@Autowired protected ClaimFormStore claimFormStore;

	protected Claim claim;
    protected Object id;
    
    {
		Describe("Spring Content Basics", () -> {
			
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
                            Assert.assertThat(claimFormStore.getContent(deletedClaimForm), is(nullValue()));
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
					claimFormStore.setContent(claim.getClaimForm(), this.getClass().getResourceAsStream("/claim_form.pdf"));
					claimRepo.save(claim);
				});
				
				It("should be able to store new content", () -> {
					assertThat(sample(() -> {
						try {
							return IOUtils.contentEquals(this.getClass().getResourceAsStream("/claim_form.pdf"), claimFormStore.getContent(claim.getClaimForm()));
						} catch (IOException e) {
							return false;
						}
					}), eventually(is(true)));
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
						assertThat(sample(() -> {
							try {
								return IOUtils.contentEquals(this.getClass().getResourceAsStream("/ACC_IN-1.DOC"), claimFormStore.getContent(claim.getClaimForm()));
							} catch (IOException e) {
								return false;
							}
						}), eventually(is(true)));
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

						Assert.assertThat(claimFormStore.getContent(deletedClaimForm), is(nullValue()));
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

	public class TestThread implements Runnable {

		private Signal ref;
		private ClaimFormStore store;
		private Claim claim;

		public TestThread(Signal ref, ClaimFormStore store, Claim claim) {
			this.ref = ref;
			this.store = store;
			this.claim = claim;
		}

		@Override
		public void run() {
			while (ref.isSignalled() == false) {
				try {
					System.out.println("Matches is " + ref);
					boolean matches = IOUtils.contentEquals(this.getClass().getResourceAsStream("/claim_form.pdf"), this.store.getContent(this.claim.getClaimForm()));
					System.out.println("test is " + matches);
					this.ref.signal();
					System.out.println("signalled");
				} catch (Exception e) {
					try {
						Thread.currentThread().sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public class Signal {
    	private boolean val = false;
		public void signal() {
			val = true;
		}
		public boolean isSignalled() {
			return true;
		}
	}
}