package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import tests.versioning.VersioningTests;

@RunWith(Ginkgo4jSpringRunner.class)
@ContextConfiguration(classes = { S3VersioningConfig.class })
//@Ginkgo4jConfiguration(threads=1)
public class S3VersioningTest extends VersioningTests {
    //
}
