package tests.events;

import examples.models.Claim;
import examples.models.ClaimForm;
import examples.models.Document;
import examples.repositories.ClaimRepository;
import examples.stores.ClaimFormStore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.property.PropertyPath;
import org.springframework.content.commons.repository.GetResourceParams;
import org.springframework.content.commons.repository.SetContentParams;
import org.springframework.content.commons.repository.UnsetContentParams;
import org.springframework.content.commons.repository.events.AfterAssociateEvent;
import org.springframework.content.commons.repository.events.AfterGetContentEvent;
import org.springframework.content.commons.repository.events.AfterGetResourceEvent;
import org.springframework.content.commons.repository.events.AfterSetContentEvent;
import org.springframework.content.commons.repository.events.AfterUnassociateEvent;
import org.springframework.content.commons.repository.events.AfterUnsetContentEvent;
import org.springframework.content.commons.repository.events.BeforeAssociateEvent;
import org.springframework.content.commons.repository.events.BeforeGetContentEvent;
import org.springframework.content.commons.repository.events.BeforeGetResourceEvent;
import org.springframework.content.commons.repository.events.BeforeSetContentEvent;
import org.springframework.content.commons.repository.events.BeforeUnassociateEvent;
import org.springframework.content.commons.repository.events.BeforeUnsetContentEvent;
import org.springframework.core.io.ByteArrayResource;
import tests.events.AnnotatedEventHandlerConfig.ExampleAnnotatedEventHandler;
import tests.smoke.ContentStoreTests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class AnnotatedEventHandlerStoreTests /*extends ContentStoreTests*/ {

	@Autowired protected ClaimRepository claimRepo;
	@Autowired protected ClaimFormStore claimFormStore;

	@Autowired private ExampleAnnotatedEventHandler eventHandler;

	protected Claim claim;
	protected Object id;

	{
		Describe("ContentStore", () -> {

			Context("given an Entity with content", () -> {

				BeforeEach(() -> {
					claim = new Claim();
					claim.setFirstName("John");
					claim.setLastName("Smith");
					claim.setClaimForm(new ClaimForm());
					claimFormStore.setContent(claim.getClaimForm(), new ByteArrayInputStream("Hello Spring Content World!".getBytes()));
					claim = claimRepo.save(claim);
				});

				Context("given getResource is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.getResource(claim.getClaimForm());
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeGetResource(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeGetResourceEvent((BeforeGetResourceEvent) argThat(instanceOf(BeforeGetResourceEvent.class)));
						verify(eventHandler, never()).handleBeforeGetResourceEvent((org.springframework.content.commons.store.events.BeforeGetResourceEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeGetResourceEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetResource(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetResourceEvent((AfterGetResourceEvent) argThat(instanceOf(AfterGetResourceEvent.class)));
						verify(eventHandler, never()).handleAfterGetResourceEvent((org.springframework.content.commons.store.events.AfterGetResourceEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterGetResourceEvent.class)));
					});
				});

				Context("given getResource (with property path) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.getResource(claim.getClaimForm(), PropertyPath.from("content"));
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeGetResource(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeGetResourceEvent((BeforeGetResourceEvent) argThat(instanceOf(BeforeGetResourceEvent.class)));
						verify(eventHandler, never()).handleBeforeGetResourceEvent((org.springframework.content.commons.store.events.BeforeGetResourceEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeGetResourceEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetResource(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetResourceEvent((AfterGetResourceEvent) argThat(instanceOf(AfterGetResourceEvent.class)));
						verify(eventHandler, never()).handleAfterGetResourceEvent((org.springframework.content.commons.store.events.AfterGetResourceEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterGetResourceEvent.class)));
					});
				});

				Context("given getResource (with getresource params) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.getResource(claim.getClaimForm(), PropertyPath.from("content"), GetResourceParams.builder().build());
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeGetResource(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeGetResourceEvent((BeforeGetResourceEvent) argThat(instanceOf(BeforeGetResourceEvent.class)));
						verify(eventHandler, never()).handleBeforeGetResourceEvent((org.springframework.content.commons.store.events.BeforeGetResourceEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeGetResourceEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetResource(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetResourceEvent((AfterGetResourceEvent) argThat(instanceOf(AfterGetResourceEvent.class)));
						verify(eventHandler, never()).handleAfterGetResourceEvent((org.springframework.content.commons.store.events.AfterGetResourceEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterGetResourceEvent.class)));
					});
				});

				Context("given associate is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						String resourceId = UUID.randomUUID().toString();
						claimFormStore.associate(claim.getClaimForm(), resourceId);
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeAssociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeAssociateEvent((BeforeAssociateEvent) argThat(instanceOf(BeforeGetResourceEvent.class)));
						verify(eventHandler, never()).handleBeforeAssociateEvent((org.springframework.content.commons.store.events.BeforeAssociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeAssociateEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterAssociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterAssociateEvent((AfterAssociateEvent) argThat(instanceOf(AfterGetResourceEvent.class)));
						verify(eventHandler, never()).handleAfterAssociateEvent((org.springframework.content.commons.store.events.AfterAssociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterAssociateEvent.class)));
					});
				});

				Context("given associate (with property path) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						String resourceId = UUID.randomUUID().toString();
						claimFormStore.associate(claim.getClaimForm(), PropertyPath.from("content"), resourceId);
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeAssociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeAssociateEvent((BeforeAssociateEvent) argThat(instanceOf(BeforeGetResourceEvent.class)));
						verify(eventHandler, never()).handleBeforeAssociateEvent((org.springframework.content.commons.store.events.BeforeAssociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeAssociateEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterAssociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterAssociateEvent((AfterAssociateEvent) argThat(instanceOf(AfterGetResourceEvent.class)));
						verify(eventHandler, never()).handleAfterAssociateEvent((org.springframework.content.commons.store.events.AfterAssociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterAssociateEvent.class)));
					});
				});

				Context("given unassociate is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.unassociate(claim.getClaimForm());
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeUnassociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeUnassociateEvent((BeforeUnassociateEvent) argThat(instanceOf(BeforeUnassociateEvent.class)));
						verify(eventHandler, never()).handleBeforeUnassociateEvent((org.springframework.content.commons.store.events.BeforeUnassociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeUnassociateEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnassociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnassociateEvent((AfterUnassociateEvent) argThat(instanceOf(AfterUnassociateEvent.class)));
						verify(eventHandler, never()).handleAfterUnassociateEvent((org.springframework.content.commons.store.events.AfterUnassociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterUnassociateEvent.class)));
					});
				});

				Context("given unassociate (with property path) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.unassociate(claim.getClaimForm(), PropertyPath.from("content"));
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeUnassociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeUnassociateEvent((BeforeUnassociateEvent) argThat(instanceOf(BeforeUnassociateEvent.class)));
						verify(eventHandler, never()).handleBeforeUnassociateEvent((org.springframework.content.commons.store.events.BeforeUnassociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeUnassociateEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnassociate(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnassociateEvent((AfterUnassociateEvent) argThat(instanceOf(AfterUnassociateEvent.class)));
						verify(eventHandler, never()).handleAfterUnassociateEvent((org.springframework.content.commons.store.events.AfterUnassociateEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterUnassociateEvent.class)));
					});
				});

				Context("given setContent is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.setContent(claim.getClaimForm(), new ByteArrayInputStream("blah blah blah".getBytes()));
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeSetContentEvent((BeforeSetContentEvent) argThat(instanceOf(BeforeSetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeSetContentEvent((org.springframework.content.commons.store.events.BeforeSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeSetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContentEvent((AfterSetContentEvent) argThat(instanceOf(AfterSetContentEvent.class)));
						verify(eventHandler, never()).handleAfterSetContentEvent((org.springframework.content.commons.store.events.AfterSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterSetContentEvent.class)));
					});
				});

				Context("given setContent (with property path) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.setContent(claim.getClaimForm(), PropertyPath.from("content"), new ByteArrayInputStream("blah blah blah".getBytes()));
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeSetContentEvent((BeforeSetContentEvent) argThat(instanceOf(BeforeSetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeSetContentEvent((org.springframework.content.commons.store.events.BeforeSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeSetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContentEvent((AfterSetContentEvent) argThat(instanceOf(AfterSetContentEvent.class)));
						verify(eventHandler, never()).handleAfterSetContentEvent((org.springframework.content.commons.store.events.AfterSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterSetContentEvent.class)));
					});
				});

				Context("given setContent (with content len) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.setContent(claim.getClaimForm(), PropertyPath.from("content"), new ByteArrayInputStream("blah blah blah".getBytes()), 14L);
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeSetContentEvent((BeforeSetContentEvent) argThat(instanceOf(BeforeSetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeSetContentEvent((org.springframework.content.commons.store.events.BeforeSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeSetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContentEvent((AfterSetContentEvent) argThat(instanceOf(AfterSetContentEvent.class)));
						verify(eventHandler, never()).handleAfterSetContentEvent((org.springframework.content.commons.store.events.AfterSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterSetContentEvent.class)));
					});
				});

				Context("given setContent (with setcontent params) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.setContent(claim.getClaimForm(), PropertyPath.from("content"), new ByteArrayInputStream("blah blah blah".getBytes()), SetContentParams.builder().build());
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeSetContentEvent((BeforeSetContentEvent) argThat(instanceOf(BeforeSetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeSetContentEvent((org.springframework.content.commons.store.events.BeforeSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeSetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContentEvent((AfterSetContentEvent) argThat(instanceOf(AfterSetContentEvent.class)));
						verify(eventHandler, never()).handleAfterSetContentEvent((org.springframework.content.commons.store.events.AfterSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterSetContentEvent.class)));
					});
				});

				Context("given setContent (with resource) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.setContent(claim.getClaimForm(), PropertyPath.from("content"), new ByteArrayResource("blah blah blah".getBytes()));
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeSetContentEvent((BeforeSetContentEvent) argThat(instanceOf(BeforeSetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeSetContentEvent((org.springframework.content.commons.store.events.BeforeSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeSetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterSetContentEvent((AfterSetContentEvent) argThat(instanceOf(AfterSetContentEvent.class)));
						verify(eventHandler, never()).handleAfterSetContentEvent((org.springframework.content.commons.store.events.AfterSetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterSetContentEvent.class)));
					});
				});

				Context("given getContent is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.getContent(claim.getClaimForm());
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeGetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeGetContentEvent((BeforeGetContentEvent) argThat(instanceOf(BeforeGetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeGetContentEvent((org.springframework.content.commons.store.events.BeforeGetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeGetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetContentEvent((AfterGetContentEvent) argThat(instanceOf(AfterGetContentEvent.class)));
						verify(eventHandler, never()).handleAfterGetContentEvent((org.springframework.content.commons.store.events.AfterGetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterGetContentEvent.class)));
					});
				});

				Context("given getContent (with property path) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.getContent(claim.getClaimForm(), PropertyPath.from("content"));
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeGetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeGetContentEvent((BeforeGetContentEvent) argThat(instanceOf(BeforeGetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeGetContentEvent((org.springframework.content.commons.store.events.BeforeGetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeGetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterGetContentEvent((AfterGetContentEvent) argThat(instanceOf(AfterGetContentEvent.class)));
						verify(eventHandler, never()).handleAfterGetContentEvent((org.springframework.content.commons.store.events.AfterGetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterGetContentEvent.class)));
					});
				});

				Context("given unsetContent is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.unsetContent(claim.getClaimForm());
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeUnsetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeUnsetContentEvent((BeforeUnsetContentEvent) argThat(instanceOf(BeforeUnsetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeUnsetContentEvent((org.springframework.content.commons.store.events.BeforeUnsetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeUnsetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnsetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnsetContentEvent((AfterUnsetContentEvent) argThat(instanceOf(AfterUnsetContentEvent.class)));
						verify(eventHandler, never()).handleAfterUnsetContentEvent((org.springframework.content.commons.store.events.AfterUnsetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterUnsetContentEvent.class)));
					});
				});

				Context("given unsetContent (with property path) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.unsetContent(claim.getClaimForm(), PropertyPath.from("content"));
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeUnsetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeUnsetContentEvent((BeforeUnsetContentEvent) argThat(instanceOf(BeforeUnsetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeUnsetContentEvent((org.springframework.content.commons.store.events.BeforeUnsetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeUnsetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnsetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnsetContentEvent((AfterUnsetContentEvent) argThat(instanceOf(AfterUnsetContentEvent.class)));
						verify(eventHandler, never()).handleAfterUnsetContentEvent((org.springframework.content.commons.store.events.AfterUnsetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterUnsetContentEvent.class)));
					});
				});

				Context("given unsetContent (with unsetcontent params) is called", () -> {
					BeforeEach(() -> {
						Mockito.reset(eventHandler);
						claimFormStore.unsetContent(claim.getClaimForm(), PropertyPath.from("content"), UnsetContentParams.builder().build());
					});
					It("should have called BeforeGetResource once only", () -> {
						verify(eventHandler, atMostOnce()).handleBeforeUnsetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleBeforeUnsetContentEvent((BeforeUnsetContentEvent) argThat(instanceOf(BeforeUnsetContentEvent.class)));
						verify(eventHandler, never()).handleBeforeUnsetContentEvent((org.springframework.content.commons.store.events.BeforeUnsetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.BeforeUnsetContentEvent.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnsetContent(argThat(instanceOf(Document.class)));
						verify(eventHandler, atMostOnce()).handleAfterUnsetContentEvent((AfterUnsetContentEvent) argThat(instanceOf(AfterUnsetContentEvent.class)));
						verify(eventHandler, never()).handleAfterUnsetContentEvent((org.springframework.content.commons.store.events.AfterUnsetContentEvent) argThat(instanceOf(org.springframework.content.commons.store.events.AfterUnsetContentEvent.class)));
					});
				});
			});
		});
	}

	@Test
	public void noop() throws IOException {
	}
}
