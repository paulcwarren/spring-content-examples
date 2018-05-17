package examples.events;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.EnableFilesystemStoresConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { EnableFilesystemStoresConfig.class, examples.events.AnnotatedEventHandlerConfig.class })
public class AnnotatedEventsTest extends AnnotatedEventHandlerStoreTests {
	//
}
