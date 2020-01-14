package examples;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentAssociativeStore;
import examples.stores.DocumentStore;
import examples.utils.RandomString;
import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;

import org.springframework.content.commons.io.DeletableResource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class AssociativeStoreExamples {

    private static Class<?>[] CONFIG_CLASSES = new Class[]{
            H2TestConfig.class,
            HSQLTestConfig.class,
            MysqlTestConfig.class,
            PostgresqlTestConfig.class,
            SqlServerTestConfig.class
    };

    private AnnotationConfigApplicationContext context = null;

    private PlatformTransactionManager ptm;
    private TransactionStatus status;

    private DocumentRepository repo;
    private DocumentAssociativeStore store;

    private Document document;

    {
        for (Class<?> configClass : CONFIG_CLASSES) {

            Describe(getContextName(configClass), () -> {

                BeforeEach(() -> {
                    context = new AnnotationConfigApplicationContext();
                    context.register(configClass);
                    context.refresh();

                    ptm = context.getBean(PlatformTransactionManager.class);
                    repo = context.getBean(DocumentRepository.class);
                    store = context.getBean(DocumentAssociativeStore.class);

                    status = ptm.getTransaction(new DefaultTransactionDefinition());
                });

                AfterEach(() -> {
                    repo.delete(document);

                    ptm.commit(status);
                });

                It("associating a resource with an entity", () -> {
                    document = repo.save(new Document());

                    String resourceId = UUID.randomUUID().toString();

                    store.getResource(resourceId);

                    store.associate(document, resourceId);

                    assertThat(document.getContentId(), is(resourceId));
                });
            });
        }
    }

    public static String getContextName(Class<?> configClass) {
        return configClass.getSimpleName().replaceAll("Config", "");
    }

    public static String getId() {
        RandomString random  = new RandomString(5);
        return "/store-tests/" + random.nextString();
    }
}
