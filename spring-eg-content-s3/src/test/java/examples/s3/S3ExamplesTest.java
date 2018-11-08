package examples.s3;

import org.springframework.content.s3.config.EnableS3Stores;
import tests.smoke.ContentStoreTests;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, S3Config.class})
@EnableS3Stores("examples.stores")
public class S3ExamplesTest extends ContentStoreTests {
    //
}