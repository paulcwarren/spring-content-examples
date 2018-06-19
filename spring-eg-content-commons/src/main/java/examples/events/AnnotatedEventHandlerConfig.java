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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import examples.models.ClaimForm;

@Configuration
public class AnnotatedEventHandlerConfig {
	
	@Bean
	public ExampleAnnotatedEventHandler eventHandler() {
		return new ExampleAnnotatedEventHandler();
	}
	
	@StoreEventHandler
	public static class ExampleAnnotatedEventHandler {

		private int beforeGetResource = 0;
		private int afterGetResource = 0;
		private int beforeAssociate = 0;
		private int afterAssociate = 0;
		private int beforeUnassociate = 0;
		private int afterUnassociate = 0;
		private int beforeSet = 0;
		private int afterSet = 0;
		private int beforeGet = 0;
		private int afterGet = 0;
		private int beforeUnset = 0;
		private int afterUnset = 0;

		@HandleBeforeGetResource
		public void handleBeforeGetResource(Document doc) {
			beforeGetResource++;
		}

		public int handleBeforeGetResourceCallCount() {
			return beforeGetResource;
		}

		@HandleAfterGetResource
		public void handleAfterGetResource(Document doc) {
			afterGetResource++;
		}

		public int handleAfterGetResourceCallCount() { return afterGetResource; }

		@HandleBeforeAssociate
		public void handleBeforeAssociate(Document doc) {
			beforeAssociate++;
		}

		public int handleBeforeAssociateCallCount() {
			return beforeAssociate;
		}

		@HandleAfterAssociate
		public void handleAfterAssociate(Document doc) {
			afterAssociate++;
		}

		public int handleAfterAssociateCallCount() {
			return afterAssociate;
		}

		@HandleBeforeUnassociate
		public void handleBeforeUnassociate(Document doc) {
			beforeUnassociate++;
		}

		public int handleBeforeUnassociateCallCount() {
			return beforeUnassociate;
		}

		@HandleAfterUnassociate
		public void handleAfterUnassociate(Document doc) {
			afterUnassociate++;
		}

		public int handleAfterUnassociateCallCount() {
			return afterUnassociate;
		}

		@HandleBeforeSetContent
		public void handleBeforeSetContent(ClaimForm claim) {
			beforeSet++;
		}
		
		public int handleBeforeSetCallCount() {
			return beforeSet;
		}
		
		@HandleAfterSetContent
		public void handleAfterSetContent(ClaimForm claim) {
			afterSet++;
		}
		
		public int handleAfterSetCallCount() {
			return afterSet;
		}

		@HandleBeforeGetContent
		public void handleBeforeGetContent(ClaimForm claim) {
			beforeGet++;
		}
		
		public int handleBeforeGetCallCount() {
			return beforeGet;
		}
		
		@HandleAfterGetContent
		public void handleAfterGetContent(ClaimForm claim) {
			afterGet++;
		}
		
		public int handleAfterGetCallCount() {
			return afterGet;
		}
	
		@HandleBeforeUnsetContent
		public void handleBeforeUnsetContent(ClaimForm claim) {
			beforeUnset++;
		}
		
		public int handleBeforeUnsetCallCount() {
			return beforeUnset;
		}
		
		@HandleAfterUnsetContent
		public void handleAfterUnsetContent(ClaimForm claim) {
			afterUnset++;
		}
		
		public int handleAfterUnsetCallCount() {
			return afterUnset;
		}
	}
}
