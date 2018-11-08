package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import tests.versioning.VersioningTests;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Ginkgo4jSpringRunner.class)
@ContextConfiguration(classes = { FsVersioningConfig.class })
//@Ginkgo4jConfiguration(threads=1)
public class FsVersioningTest extends VersioningTests {
    //
}
