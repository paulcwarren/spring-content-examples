package examples.events;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import examples.models.Claim;
import examples.models.ClaimForm;
import examples.models.Document;
import org.springframework.beans.factory.annotation.Autowired;

import examples.ContentStoreTests;
import examples.events.AnnotatedEventHandlerConfig.ExampleAnnotatedEventHandler;
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

public class AnnotatedEventHandlerStoreTests extends ContentStoreTests {
	
	@Autowired private ExampleAnnotatedEventHandler eventHandler;
	
	{
		Describe("Annotated Event Handler", () -> {
			It("should have called BeforeGetResource", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeGetResource(argThat(instanceOf(Document.class)));
			});
			It("should have called BeforeGetResourceEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeGetResourceEvent(argThat(instanceOf(BeforeGetResourceEvent.class)));
			});
			It("should have called AfterGetResource", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterGetResource(argThat(instanceOf(Document.class)));
			});
			It("should have called AfterGetResourceEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterGetResourceEvent(argThat(instanceOf(AfterGetResourceEvent.class)));
			});
			It("should have called BeforeAssociate", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeAssociate(argThat(instanceOf(Document.class)));
			});
			It("should have called BeforeAssociateEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeAssociateEvent(argThat(instanceOf(BeforeAssociateEvent.class)));
			});
			It("should have called AfterAssociate", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterAssociate(argThat(instanceOf(Document.class)));
			});
			It("should have called AfterAssociateEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterAssociateEvent(argThat(instanceOf(AfterAssociateEvent.class)));
			});
			It("should have called BeforeUnassociate", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeUnassociate(argThat(instanceOf(Document.class)));
			});
			It("should have called BeforeUnassociateEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeUnassociateEvent(argThat(instanceOf(BeforeUnassociateEvent.class)));
			});
			It("should have called AfterAssociate", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterUnassociate(argThat(instanceOf(Document.class)));
			});
			It("should have called AfterAssociateEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterUnassociateEvent(argThat(instanceOf(AfterUnassociateEvent.class)));
			});
			It("should have called BeforeSetContent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeSetContent(argThat(instanceOf(ClaimForm.class)));
			});
			It("should have called BeforeSetContentEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeSetContentEvent(argThat(instanceOf(BeforeSetContentEvent.class)));
			});
			It("should have called AfterSetContent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterSetContent(argThat(instanceOf(ClaimForm.class)));
			});
			It("should have called AfterSetContentEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterSetContentEvent(argThat(instanceOf(AfterSetContentEvent.class)));
			});
			It("should have called BeforeGetContent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeGetContent(argThat(instanceOf(ClaimForm.class)));
			});
			It("should have called BeforeGetContentEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeGetContentEvent(argThat(instanceOf(BeforeGetContentEvent.class)));
			});
			It("should have called AfterGetContent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterGetContent(argThat(instanceOf(ClaimForm.class)));
			});
			It("should have called AfterGetContentEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterGetContentEvent(argThat(instanceOf(AfterGetContentEvent.class)));
			});
			It("should have called BeforeUnsetContent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeUnsetContent(argThat(instanceOf(ClaimForm.class)));
			});
			It("should have called BeforeUnsetContentEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleBeforeUnsetContentEvent(argThat(instanceOf(BeforeUnsetContentEvent.class)));
			});
			It("should have called AfterUnsetContent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterUnsetContent(argThat(instanceOf(ClaimForm.class)));
			});
			It("should have called AfterUnsetContentEvent", () -> {
				verify(eventHandler, atLeastOnce()).handleAfterUnsetContentEvent(argThat(instanceOf(AfterUnsetContentEvent.class)));
			});
		});
	}
}
