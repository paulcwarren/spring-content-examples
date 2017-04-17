package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import examples.typesupport.BigIntegerBasedContentEntity;
import examples.typesupport.BigIntegerBasedContentEntityStore;
import examples.typesupport.LongBasedContentEntity;
import examples.typesupport.LongBasedContentEntityStore;
import examples.typesupport.URIBasedContentEntity;
import examples.typesupport.URIBasedContentEntityStore;
import examples.typesupport.UUIDBasedContentEntity;
import examples.typesupport.UUIDBasedContentEntityStore;

public abstract class AbstractSpringContentTests {

	@Autowired protected ClaimRepository claimRepo;
	@Autowired protected ClaimFormStore claimFormStore;

	@Autowired protected UUIDBasedContentEntityStore uuidStore;
	@Autowired protected URIBasedContentEntityStore uriStore;
	@Autowired protected LongBasedContentEntityStore longStore;
	@Autowired protected BigIntegerBasedContentEntityStore bigIntStore;

	private Object entity;

	protected Claim claim;
    protected Object id;
    
    {
		Describe("Spring Content", () -> {
			
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
		
		Describe("Spring Content Type Support", () -> {
			Context("java.util.UUID", () -> {
				Context("given a content entity", () -> {
					BeforeEach(() -> {
						entity = new UUIDBasedContentEntity();
					});
					Context("given the Application sets the ID", () -> {
						BeforeEach(() -> {
							id = UUID.randomUUID();
							((UUIDBasedContentEntity)entity).setContentId((UUID)id);

							uuidStore.setContent((UUIDBasedContentEntity)entity, new ByteArrayInputStream("uuid".getBytes()));
						});
						It("should store the content successfully", () -> {
							Assert.assertThat(IOUtils.contentEquals(uuidStore.getContent((UUIDBasedContentEntity)entity), IOUtils.toInputStream("uuid")), is(true));
						});
					});
					Context("given Spring Content generates the ID", () -> {
						BeforeEach(() -> {
							uuidStore.setContent((UUIDBasedContentEntity)entity, new ByteArrayInputStream("uuid".getBytes()));
						});
						It("should store the content successfully", () -> {
							Assert.assertThat(IOUtils.contentEquals(uuidStore.getContent((UUIDBasedContentEntity)entity), IOUtils.toInputStream("uuid")), is(true));
						});
					});
				});
				AfterEach(() -> {
					uuidStore.unsetContent((UUIDBasedContentEntity)entity);
					Assert.assertThat(uuidStore.getContent((UUIDBasedContentEntity)entity), is(nullValue()));
				});
			});
			Context("java.net.URI", () -> {
				Context("given a content entity", () -> {
					BeforeEach(() -> {
						entity = new URIBasedContentEntity();
					});
					Context("given the Application sets the ID", () -> {
						BeforeEach(() -> {
							id = new URI("/some/deep/location");
							((URIBasedContentEntity)entity).setContentId((URI)id);

							uriStore.setContent((URIBasedContentEntity)entity, new ByteArrayInputStream("uri".getBytes()));
						});
						It("should store the content successfully", () -> {
							Assert.assertThat(IOUtils.contentEquals(uriStore.getContent((URIBasedContentEntity)entity), IOUtils.toInputStream("uri")), is(true));
						});
					});
					Context("given Spring Content generates the ID", () -> {
						BeforeEach(() -> {
							uriStore.setContent((URIBasedContentEntity)entity, new ByteArrayInputStream("uri".getBytes()));
						});
						It("should store the content successfully", () -> {
							Assert.assertThat(IOUtils.contentEquals(uriStore.getContent((URIBasedContentEntity)entity), IOUtils.toInputStream("uri")), is(true));
						});
					});
				});
				AfterEach(() -> {
					uriStore.unsetContent((URIBasedContentEntity)entity);
					Assert.assertThat(uriStore.getContent((URIBasedContentEntity)entity), is(nullValue()));
				});
			});
			Context("java.lang.Long", () -> {
				Context("given a content entity", () -> {
					BeforeEach(() -> {
						entity = new LongBasedContentEntity();
					});
					Context("given the Application sets the ID", () -> {
						BeforeEach(() -> {
							id = Long.MAX_VALUE;
							((LongBasedContentEntity)entity).setContentId((Long)id);

							longStore.setContent((LongBasedContentEntity)entity, new ByteArrayInputStream("long".getBytes()));
						});
						It("should store the content successfully", () -> {
							Assert.assertThat(IOUtils.contentEquals(longStore.getContent((LongBasedContentEntity)entity), IOUtils.toInputStream("long")), is(true));
						});
					});
				});
				AfterEach(() -> {
					longStore.unsetContent((LongBasedContentEntity)entity);
					Assert.assertThat(longStore.getContent((LongBasedContentEntity)entity), is(nullValue()));
				});
			});
			Context("java.math.BigInteger", () -> {
				Context("given a content entity", () -> {
					BeforeEach(() -> {
						entity = new BigIntegerBasedContentEntity();
					});
					Context("given the Application sets the ID", () -> {
						BeforeEach(() -> {
							id = BigInteger.valueOf(Long.MAX_VALUE);
							((BigIntegerBasedContentEntity)entity).setContentId((BigInteger)id);

							bigIntStore.setContent((BigIntegerBasedContentEntity)entity, new ByteArrayInputStream("big-int".getBytes()));
						});
						It("should store the content successfully", () -> {
							Assert.assertThat(IOUtils.contentEquals(bigIntStore.getContent((BigIntegerBasedContentEntity)entity), IOUtils.toInputStream("big-int")), is(true));
						});
					});
				});
				AfterEach(() -> {
					bigIntStore.unsetContent((BigIntegerBasedContentEntity)entity);
					Assert.assertThat(bigIntStore.getContent((BigIntegerBasedContentEntity)entity), is(nullValue()));
				});
			});
		});
	}

	@Test
	public void noop() throws IOException {
	}
}