package examples.events;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.S3Config;
import examples.events.AnnotatedEventHandlerTests;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { S3Config.class, examples.events.AnnotatedEventHandlerConfig.class })
public class AnnotatedEventsTest extends AnnotatedEventHandlerTests {
	//
}
