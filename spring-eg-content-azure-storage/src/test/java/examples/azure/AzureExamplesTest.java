package examples.azure;

import org.junit.runner.RunWith;
import org.springframework.content.azure.config.EnableAzureStorage;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import tests.smoke.ContentStoreTests;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, AzureConfig.class})
@EnableAzureStorage("examples.stores")
public class AzureExamplesTest extends ContentStoreTests {
}