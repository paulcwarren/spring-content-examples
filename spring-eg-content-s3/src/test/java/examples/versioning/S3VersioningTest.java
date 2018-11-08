package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import tests.versioning.VersioningTests;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@ContextConfiguration(classes = { S3VersioningConfig.class })
//@Ginkgo4jConfiguration(threads=1)
public class S3VersioningTest extends VersioningTests {
    //
}
