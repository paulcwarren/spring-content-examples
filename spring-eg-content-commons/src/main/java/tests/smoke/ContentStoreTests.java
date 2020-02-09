package tests.smoke;

import examples.models.Claim;
import examples.models.ClaimForm;
import examples.repositories.ClaimRepository;
import examples.stores.ClaimFormStore;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public abstract class ContentStoreTests extends AssociativeStoreTests {

	@Autowired protected ClaimRepository claimRepo;
	@Autowired protected ClaimFormStore claimFormStore;

	protected Claim claim;
	protected Object id;

	{
		Describe("ContentStore", () -> {

//			AfterEach(() -> {
//				deleteAllClaimFormsContent();
//				deleteAllClaims();
//			});

			Context("given an Entity with content", () -> {

				BeforeEach(() -> {
					claim = new Claim();
					claim.setFirstName("John");
					claim.setLastName("Smith");
					claim.setClaimForm(new ClaimForm());
					claimFormStore.setContent(claim.getClaimForm(), new ByteArrayInputStream("Hello Spring Content World!".getBytes()));
					claim = claimRepo.save(claim);
				});

				It("should be able to store new content", () -> {
					boolean matches = false;
					InputStream content = null;
					try {
						content = claimFormStore.getContent(claim.getClaimForm());
						matches = IOUtils.contentEquals(new ByteArrayInputStream("Hello Spring Content World!".getBytes()), content);
					} catch (IOException e) {
					} finally {
						IOUtils.closeQuietly(content);
					}
					assertThat(matches, is(true));
				});

				It("should have content metadata", () -> {
					Assert.assertThat(claim.getClaimForm().getContentId(), is(notNullValue()));
					Assert.assertThat(claim.getClaimForm().getContentId().trim().length(), greaterThan(0));
					Assert.assertEquals(claim.getClaimForm().getContentLength(), 27L);
				});

				Context("when content is updated", () -> {
					BeforeEach(() ->{
						claimFormStore.setContent(claim.getClaimForm(), new ByteArrayInputStream("Hello Updated Spring Content World!".getBytes()));
						claim = claimRepo.save(claim);
					});

					It("should have the updated content", () -> {
						boolean matches = false;
						InputStream content = null;
						try {
							content = claimFormStore.getContent(claim.getClaimForm());
							matches = IOUtils.contentEquals(new ByteArrayInputStream("Hello Updated Spring Content World!".getBytes()), content);
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

						Assert.assertThat(claim.getClaimForm().getContentId(), is(nullValue()));
						Assert.assertEquals(claim.getClaimForm().getContentLength(), 0);
					});
				});
			});
		});
	}

	protected boolean hasContent(ClaimForm claimForm) {

		if (claimForm == null) {
			return false;
		}

		try (InputStream content = claimFormStore.getContent(claimForm)) {
			if (content != null) {
				return true;
			}
		} catch (IOException e) {
		}

		return false;
	}

	protected void deleteAllClaims() {
		claimRepo.deleteAll();
	}

	protected void deleteAllClaimFormsContent() {
		Iterable<Claim> existingClaims = claimRepo.findAll();
		for (Claim existingClaim : existingClaims) {
			if (existingClaim.getClaimForm() != null && hasContent(existingClaim.getClaimForm())) {
				String contentId = existingClaim.getClaimForm().getContentId();
				claimFormStore.unsetContent(existingClaim.getClaimForm());
				if (existingClaim.getClaimForm() != null) {
					Assert.assertThat(existingClaim.getClaimForm().getContentId(), is(nullValue()));
					Assert.assertEquals(existingClaim.getClaimForm().getContentLength(), 0);

					// double check the content got removed
					ClaimForm deletedClaimForm = new ClaimForm();
					deletedClaimForm.setContentId(contentId);
					InputStream content = claimFormStore.getContent(deletedClaimForm);
					try {
						Assert.assertThat(content, is(nullValue()));
					}
					finally {
						IOUtils.closeQuietly(content);
					}
				}
			}
		}
	}

	@Test
	public void noop() throws IOException {
	}
}