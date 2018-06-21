package examples.events;

import examples.models.Document;
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

import examples.models.ClaimForm;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@Configuration
public class AnnotatedEventHandlerConfig {
	
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

		@HandleAfterGetResource
		public void handleAfterGetResource(Document doc) {
		}

		@HandleAfterGetResource
		public void handleAfterGetResourceEvent(AfterGetResourceEvent event) {
		}

		@HandleBeforeAssociate
		public void handleBeforeAssociate(Document doc) {
		}

		@HandleBeforeAssociate
		public void handleBeforeAssociateEvent(BeforeAssociateEvent event) {
		}

		@HandleAfterAssociate
		public void handleAfterAssociate(Document doc) {
		}

		@HandleAfterAssociate
		public void handleAfterAssociateEvent(AfterAssociateEvent event) {
		}

		@HandleBeforeUnassociate
		public void handleBeforeUnassociate(Document doc) {
		}

		@HandleBeforeUnassociate
		public void handleBeforeUnassociateEvent(BeforeUnassociateEvent event) {
		}

		@HandleAfterUnassociate
		public void handleAfterUnassociate(Document doc) {
		}

		@HandleAfterUnassociate
		public void handleAfterUnassociateEvent(AfterUnassociateEvent event) {
		}

		@HandleBeforeSetContent
		public void handleBeforeSetContent(ClaimForm claim) {
		}

		@HandleBeforeSetContent
		public void handleBeforeSetContentEvent(BeforeSetContentEvent event) {
		}

		@HandleAfterSetContent
		public void handleAfterSetContent(ClaimForm claim) {
		}

		@HandleAfterSetContent
		public void handleAfterSetContentEvent(AfterSetContentEvent event) {
		}

		@HandleBeforeGetContent
		public void handleBeforeGetContent(ClaimForm claim) {
		}

		@HandleBeforeGetContent
		public void handleBeforeGetContentEvent(BeforeGetContentEvent event) {
		}

		@HandleAfterGetContent
		public void handleAfterGetContent(ClaimForm claim) {
		}

		@HandleAfterGetContent
		public void handleAfterGetContentEvent(AfterGetContentEvent event) {
		}

		@HandleBeforeUnsetContent
		public void handleBeforeUnsetContent(ClaimForm claim) {
		}

		@HandleBeforeUnsetContent
		public void handleBeforeUnsetContentEvent(BeforeUnsetContentEvent event) {
		}

		@HandleAfterUnsetContent
		public void handleAfterUnsetContent(ClaimForm claim) {
		}

		@HandleAfterUnsetContent
		public void handleAfterUnsetContentEvent(AfterUnsetContentEvent event) {
		}
	}
}
