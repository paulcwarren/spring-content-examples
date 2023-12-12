package examples;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import internal.org.springframework.content.mongo.boot.autoconfigure.MongoContentAutoConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import tests.smoke.ContentStoreTests;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = Application.class)
public class FsBootExamplesTest extends ContentStoreTests {

}
