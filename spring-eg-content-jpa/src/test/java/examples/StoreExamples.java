package examples;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import examples.stores.DocumentStore;
import examples.utils.RandomString;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.io.DeletableResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads = 1)
@ContextConfiguration(classes = { H2Config.class })
public class StoreExamples {

    @Autowired
    private PlatformTransactionManager ptm;
    private TransactionStatus status;

    @Autowired
    private DocumentStore store;

    private String id;

    {
        Describe("Store API", () -> {

            BeforeEach(() -> {
                status = ptm.getTransaction(new DefaultTransactionDefinition());
            });

            AfterEach(() -> {
                Resource r = store.getResource(id);
                ((DeletableResource)r).delete();

                ptm.commit(status);
            });

            It("add content via resource API", () -> {
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

    public static String getContextName(Class<?> configClass) {
        return configClass.getSimpleName().replaceAll("Config", "");
    }

    public static String getId() {
        RandomString random  = new RandomString(5);
        return "/store-tests/" + random.nextString();
    }

    @Test
    public void noop() {}
}
