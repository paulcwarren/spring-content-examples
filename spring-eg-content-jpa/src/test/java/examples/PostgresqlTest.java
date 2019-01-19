package examples;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import tests.smoke.ContentStoreTests;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { PostgresqlTestConfig.class })
public class PostgresqlTest extends ContentStoreTests {

}
