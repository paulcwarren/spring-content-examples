package examples;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.junit.runner.RunWith;
import tests.versioning.VersioningTests;

import org.springframework.test.context.ContextConfiguration;

@RunWith(Ginkgo4jSpringRunner.class)
//@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { SqlServerTestConfig.class })
public class SqlServerTest extends VersioningTests {

//    @Autowired
//    private VersionedDocumentAndVersioningRepository repo;
//
//    @Autowired
//    private LockingService lockingService;
//
//    {
//        FIt("deadlock tests", () -> {
//            ExecutorService executor = Executors.newFixedThreadPool(25);
//            for (int i = 0; i < 25; i++) {
//                Runnable worker = new ThreadTest(i, repo, true);
//                executor.execute(worker);
//            }
//            executor.shutdown();
//            while (!executor.isTerminated()) {
//            }
//        });
//    }
}
