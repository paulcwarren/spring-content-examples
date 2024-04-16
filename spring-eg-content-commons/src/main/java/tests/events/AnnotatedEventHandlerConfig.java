package tests.events;

import examples.models.ClaimForm;
import examples.models.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.content.commons.annotations.HandleAfterAssociate;
import org.springframework.content.commons.annotations.HandleAfterGetContent;
import org.springframework.content.commons.annotations.HandleAfterGetResource;
import org.springframework.content.commons.annotations.HandleAfterSetContent;
import org.springframework.content.commons.annotations.HandleAfterUnassociate;
import org.springframework.content.commons.annotations.HandleAfterUnsetContent;
import org.springframework.content.commons.annotations.HandleBeforeAssociate;
import org.springframework.content.commons.annotations.HandleBeforeGetContent;
import org.springframework.content.commons.annotations.HandleBeforeGetResource;
import org.springframework.content.commons.annotations.HandleBeforeSetContent;
import org.springframework.content.commons.annotations.HandleBeforeUnassociate;
import org.springframework.content.commons.annotations.HandleBeforeUnsetContent;
import org.springframework.content.commons.annotations.StoreEventHandler;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tests.versioning.VersioningTests;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;

@Configuration
public class AnnotatedEventHandlerConfig {

	private static Log logger = LogFactory.getLog(AnnotatedEventHandlerConfig.class);

	@Bean
	public ExampleAnnotatedEventHandler eventHandler() {
		return mock(ExampleAnnotatedEventHandler.class);
	}
	
	@StoreEventHandler
	public static class ExampleAnnotatedEventHandler {

		@HandleBeforeGetResource
		public void handleBeforeGetResource(Document doc) {
		}

		@HandleBeforeGetResource
		public void handleBeforeGetResourceEvent(BeforeGetResourceEvent event) {
		}

		@HandleBeforeGetResource
		public void handleBeforeGetResourceEvent(org.springframework.content.commons.store.events.BeforeGetResourceEvent event) {
		}

		@HandleAfterGetResource
		public void handleAfterGetResource(Document doc) {
		}

		@HandleAfterGetResource
		public void handleAfterGetResourceEvent(AfterGetResourceEvent event) {
		}

		@HandleAfterGetResource
		public void handleAfterGetResourceEvent(org.springframework.content.commons.store.events.AfterGetResourceEvent event) {
		}
		@HandleBeforeAssociate
		public void handleBeforeAssociate(Document doc) {
		}

		@HandleBeforeAssociate
		public void handleBeforeAssociateEvent(BeforeAssociateEvent event) {
		}

		@HandleBeforeAssociate
		public void handleBeforeAssociateEvent(org.springframework.content.commons.store.events.BeforeAssociateEvent event) {
		}

		@HandleAfterAssociate
		public void handleAfterAssociate(Document doc) {
		}

		@HandleAfterAssociate
		public void handleAfterAssociateEvent(AfterAssociateEvent event) {
		}
		@HandleAfterAssociate
		public void handleAfterAssociateEvent(org.springframework.content.commons.store.events.AfterAssociateEvent event) {
		}

		@HandleBeforeUnassociate
		public void handleBeforeUnassociate(Document doc) {
		}

		@HandleBeforeUnassociate
		public void handleBeforeUnassociateEvent(BeforeUnassociateEvent event) {
		}
		@HandleBeforeUnassociate
		public void handleBeforeUnassociateEvent(org.springframework.content.commons.store.events.BeforeUnassociateEvent event) {
		}

		@HandleAfterUnassociate
		public void handleAfterUnassociate(Document doc) {
		}

		@HandleAfterUnassociate
		public void handleAfterUnassociateEvent(AfterUnassociateEvent event) {
		}
		@HandleAfterUnassociate
		public void handleAfterUnassociateEvent(org.springframework.content.commons.store.events.AfterUnassociateEvent event) {
		}

		@HandleBeforeSetContent
		public void handleBeforeSetContent(ClaimForm claim) {
		}

		@HandleBeforeSetContent
		public void handleBeforeSetContentEvent(BeforeSetContentEvent event) {
		}

		@HandleBeforeSetContent
		public void handleBeforeSetContentEvent(org.springframework.content.commons.store.events.BeforeSetContentEvent event) {
		}

		@HandleAfterSetContent
		public void handleAfterSetContent(ClaimForm claim) {
		}

		@HandleAfterSetContent
		public void handleAfterSetContentEvent(AfterSetContentEvent event) {
		}

		@HandleAfterSetContent
		public void handleAfterSetContentEvent(org.springframework.content.commons.store.events.AfterSetContentEvent event) {
		}

		@HandleBeforeGetContent
		public void handleBeforeGetContent(ClaimForm claim) {
		}

		@HandleBeforeGetContent
		public void handleBeforeGetContentEvent(BeforeGetContentEvent event) {
		}

		@HandleBeforeGetContent
		public void handleBeforeGetContentEvent(org.springframework.content.commons.store.events.BeforeGetContentEvent event) {
		}
		@HandleAfterGetContent
		public void handleAfterGetContent(ClaimForm claim) {
		}

		@HandleAfterGetContent
		public void handleAfterGetContentEvent(AfterGetContentEvent event) {
		}
		@HandleAfterGetContent
		public void handleAfterGetContentEvent(org.springframework.content.commons.store.events.AfterGetContentEvent event) {
		}

		@HandleBeforeUnsetContent
		public void handleBeforeUnsetContent(ClaimForm claim) {
		}

		@HandleBeforeUnsetContent
		public void handleBeforeUnsetContentEvent(BeforeUnsetContentEvent event) {
		}

		@HandleBeforeUnsetContent
		public void handleBeforeUnsetContentEvent(org.springframework.content.commons.store.events.BeforeUnsetContentEvent event) {
		}
		@HandleAfterUnsetContent
		public void handleAfterUnsetContent(ClaimForm claim) {
		}

		@HandleAfterUnsetContent
		public void handleAfterUnsetContentEvent(AfterUnsetContentEvent event) {
		}
		@HandleAfterUnsetContent
		public void handleAfterUnsetContentEvent(org.springframework.content.commons.store.events.AfterUnsetContentEvent event) {
		}
	}
}
