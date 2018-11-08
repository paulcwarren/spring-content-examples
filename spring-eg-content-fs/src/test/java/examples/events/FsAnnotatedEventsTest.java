package examples.events;

import tests.events.AnnotatedEventHandlerConfig;
import tests.events.AnnotatedEventHandlerStoreTests;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.fs.EnableFilesystemStoresConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { EnableFilesystemStoresConfig.class, AnnotatedEventHandlerConfig.class })
public class FsAnnotatedEventsTest extends AnnotatedEventHandlerStoreTests {
	//
}
