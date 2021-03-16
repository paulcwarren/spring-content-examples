package examples.versioning;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import tests.versioning.VersioningTests;

@RunWith(Ginkgo4jSpringRunner.class)
@ContextConfiguration(classes = { AzureVersioningConfig.class })
//@Ginkgo4jConfiguration(threads=1)
public class AzureVersioningTest extends VersioningTests {
    // 
}
