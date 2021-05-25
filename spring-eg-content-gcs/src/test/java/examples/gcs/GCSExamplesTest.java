package examples.gcs;

import org.junit.runner.RunWith;
import org.springframework.content.gcs.config.EnableGCPStorage;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import tests.smoke.ContentStoreTests;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, GCSConfig.class})
@EnableGCPStorage("examples.stores")
public class GCSExamplesTest extends ContentStoreTests {
    //
}