package examples.events;

import org.junit.runner.RunWith;
import org.springframework.content.azure.config.EnableAzureStorage;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.azure.AzureConfig;
import tests.events.AnnotatedEventHandlerConfig;
import tests.events.AnnotatedEventHandlerStoreTests;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, AzureConfig.class, AnnotatedEventHandlerConfig.class })
@EnableAzureStorage("examples.stores")
public class AzureAnnotatedEventsTest extends AnnotatedEventHandlerStoreTests {
	//
}
