package examples;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.junit.runner.RunWith;
import tests.smoke.ContentStoreTests;

import org.springframework.test.context.ContextConfiguration;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { H2TestConfig.class })
public class H2Test extends ContentStoreTests {

}
