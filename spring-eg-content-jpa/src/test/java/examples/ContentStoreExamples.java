package examples;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentContentStore;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads = 1)
@ContextConfiguration(classes = { H2Config.class })
public class ContentStoreExamples {

    @Autowired
    private PlatformTransactionManager ptm;
    private TransactionStatus status;

    @Autowired
    private DocumentRepository repo;

    @Autowired
    private DocumentContentStore store;

    private Document document;

    {
        Describe("ContentStore API", () -> {

            BeforeEach(() -> {
                status = ptm.getTransaction(new DefaultTransactionDefinition());
            });

            AfterEach(() -> {
                store.unsetContent(document);
                repo.delete(document);

                ptm.commit(status);
            });

            It("associate content with an entity", () -> {
                document = repo.save(new Document());

                document = store.setContent(document, new ByteArrayInputStream("Hello Spring Content World!".getBytes()));

                assertThat(document.getContentId(), is(not(nullValue())));

                try (InputStream content = store.getContent(document)) {
                    assertThat(IOUtils.toString(content), is("Hello Spring Content World!"));
                }
            });
        });
    }

    public static String getContextName(Class<?> configClass) {
        return configClass.getSimpleName().replaceAll("Config", "");
    }

    @Test
    public void noop() {}
}
