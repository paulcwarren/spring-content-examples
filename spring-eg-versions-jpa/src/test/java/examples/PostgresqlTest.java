package examples;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import internal.org.springframework.versions.LockingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tests.smoke.ContentStoreTests;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import tests.versioning.VersionedDocument;
import tests.versioning.VersionedDocumentAndVersioningRepository;
import tests.versioning.VersioningTests;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static java.lang.String.format;

@RunWith(Ginkgo4jSpringRunner.class)
//@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { PostgresqlTestConfig.class })
public class PostgresqlTest extends VersioningTests {

//    @Autowired
//    private VersionedDocumentAndVersioningRepository repo;
//
//    @Autowired
//    private LockingService lockingService;
//
//    {
//        FIt("deadlock tests", () -> {
//            ExecutorService executor = Executors.newFixedThreadPool(5);
//            for (int i = 0; i < 5; i++) {
//                Runnable worker = new ThreadTest(i, repo);
//                executor.execute(worker);
//            }
//            executor.shutdown();
//            while (!executor.isTerminated()) {
//            }
//        });
//    }
}
