package examples.typesupport;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.EnableFilesystemStoresConfig;
import examples.typesupport.TypeSupportTests;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { FsTypeSupportConfig.class })
public class FsTypeSupportTests extends TypeSupportTests {

}
