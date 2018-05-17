package examples.compatibility;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.ContentStoreTests;
import examples.EnableFilesystemContentRepositoriesConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { EnableFilesystemContentRepositoriesConfig.class })
public class CompatibilityTest extends ContentStoreTests {
	{
	}
}
