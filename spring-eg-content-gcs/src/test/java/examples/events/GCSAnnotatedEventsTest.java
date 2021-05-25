package examples.events;

import org.junit.runner.RunWith;
import org.springframework.content.gcs.config.EnableGCPStorage;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.gcs.GCSConfig;
import tests.events.AnnotatedEventHandlerConfig;
import tests.events.AnnotatedEventHandlerStoreTests;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, GCSConfig.class, AnnotatedEventHandlerConfig.class })
@EnableGCPStorage("examples.stores")
public class GCSAnnotatedEventsTest extends AnnotatedEventHandlerStoreTests {
	//
}
