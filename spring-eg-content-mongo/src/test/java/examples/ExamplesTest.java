package examples;

import examples.utils.RandomString;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { MongoConfig.class })
public class ExamplesTest extends ContentStoreTests {

//    @Override
//    protected String getId() {
//        RandomString random  = new RandomString(10);
//        return random.nextString();
//    }

}
