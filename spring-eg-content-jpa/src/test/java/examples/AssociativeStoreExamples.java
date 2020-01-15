package examples;

import java.util.UUID;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentAssociativeStore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads = 1)
@ContextConfiguration(classes = { H2Config.class })
public class AssociativeStoreExamples {

    @Autowired
    private PlatformTransactionManager ptm;
    private TransactionStatus status;

    @Autowired
    private DocumentRepository repo;

    @Autowired
    private DocumentAssociativeStore store;

    private Document document;

    {
        Describe("AssociativeStore API", () -> {

            BeforeEach(() -> {
                status = ptm.getTransaction(new DefaultTransactionDefinition());
            });

            AfterEach(() -> {
                repo.delete(document);

                ptm.commit(status);
            });

            It("associate a resource with an entity", () -> {
                document = repo.save(new Document());

                String resourceId = UUID.randomUUID().toString();

                store.getResource(resourceId);

                store.associate(document, resourceId);

                assertThat(document.getContentId(), is(resourceId));
            });
        });
    }

    public static String getContextName(Class<?> configClass) {
        return configClass.getSimpleName().replaceAll("Config", "");
    }

    @Test
    public void noop() {}
}
