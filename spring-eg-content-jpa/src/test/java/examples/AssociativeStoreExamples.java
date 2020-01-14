package examples;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
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

@RunWith(Ginkgo4jRunner.class)
@Ginkgo4jConfiguration(threads = 1)
public class StoreExamples {

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

    private DocumentStore store;

    private String id;

    {
        for (Class<?> configClass : CONFIG_CLASSES) {

            Describe(getContextName(configClass), () -> {

                BeforeEach(() -> {
                    context = new AnnotationConfigApplicationContext();
                    context.register(configClass);
                    context.refresh();

                    ptm = context.getBean(PlatformTransactionManager.class);
                    store = context.getBean(DocumentStore.class);

                    status = ptm.getTransaction(new DefaultTransactionDefinition());
                });

                AfterEach(() -> {
                    Resource r = store.getResource(id);
                    ((DeletableResource)r).delete();

                    ptm.commit(status);
                });

                It("adding content to a resource", () -> {
                    id = getId();
                    Resource r = store.getResource(id);

                    try (InputStream is = new ByteArrayInputStream("Hello Spring Content World!".getBytes())) {
                        try (OutputStream os = ((WritableResource)r).getOutputStream()) {
                            IOUtils.copy(is, os);
                        }
                    }
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
