package examples.events;

import org.springframework.content.commons.annotations.HandleAfterGetContent;
import org.springframework.content.commons.annotations.HandleAfterSetContent;
import org.springframework.content.commons.annotations.HandleAfterUnsetContent;
import org.springframework.content.commons.annotations.HandleBeforeGetContent;
import org.springframework.content.commons.annotations.HandleBeforeSetContent;
import org.springframework.content.commons.annotations.HandleBeforeUnsetContent;
import org.springframework.content.commons.annotations.StoreEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import examples.ClaimForm;

@Configuration
public class AnnotatedEventHandlerConfig {
	
	@Bean
	public ExampleAnnotatedEventHandler eventHandler() {
		return new ExampleAnnotatedEventHandler();
	}
	
	@StoreEventHandler
	public static class ExampleAnnotatedEventHandler {

		private int beforeSet = 0;
		private int afterSet = 0;
		private int beforeGet = 0;
		private int afterGet = 0;
		private int beforeUnset = 0;
		private int afterUnset = 0;
		
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
