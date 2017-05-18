package examples.events;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.springframework.beans.factory.annotation.Autowired;

import examples.AbstractSpringContentTests;
import examples.events.AnnotatedEventHandlerConfig.ExampleAnnotatedEventHandler;

public class AnnotatedEventHandlerTests extends AbstractSpringContentTests {
	
	@Autowired private ExampleAnnotatedEventHandler eventHandler;
	
	{
		Describe("Annotated Event Handler", () -> {
			It("should have called BeforeSetContent", () -> {
				assertThat(eventHandler.handleBeforeSetCallCount(), is(greaterThan(0)));
			});
			It("should have called AfterSetContent", () -> {
				assertThat(eventHandler.handleAfterSetCallCount(), is(greaterThan(0)));
			});
			It("should have called BeforeGetContent", () -> {
				assertThat(eventHandler.handleBeforeGetCallCount(), is(greaterThan(0)));
			});
			It("should have called AfterGetContent", () -> {
				assertThat(eventHandler.handleAfterGetCallCount(), is(greaterThan(0)));
			});
			It("should have called BeforeUnsetContent", () -> {
				assertThat(eventHandler.handleBeforeUnsetCallCount(), is(greaterThan(0)));
			});
			It("should have called AfterUnsetContent", () -> {
				assertThat(eventHandler.handleAfterUnsetCallCount(), is(greaterThan(0)));
			});
		});
	}
}
