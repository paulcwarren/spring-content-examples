package examples;

import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentAssociativeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.util.UUID;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FDescribe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AssociativeStoreTests extends StoreTests {

    @Autowired
    private DocumentRepository repo;

    @Autowired
    private DocumentAssociativeStore store;

    private Document document;
    private String resourceId;
    private Resource resource;
    private Exception e;

    {
        Describe("AssociativeStore", () -> {
            Context("given a new entity", () -> {
                BeforeEach(() -> {
                    document = new Document();
                    document = repo.save(document);
                });
                It("should not have an associated resource", () -> {
                    assertThat(document.getContentId(), is(nullValue()));
                    assertThat(store.getResource(document), is(nullValue()));
                });
                Context("given a resource", () -> {
                    BeforeEach(() -> {
                        resourceId = UUID.randomUUID().toString();
                        resource = store.getResource(resourceId);
                    });
                    Context("when the resource is associated", () -> {
                       BeforeEach(() -> {
                           store.associate(document, resourceId);
                       });
                        It("should be recorded as such on the entity's @ContentId", () -> {
                            assertThat(document.getContentId(), is(resourceId));
                        });
                        Context("when the resource is unassociated", () -> {
                            BeforeEach(() -> {
                                store.unassociate(document);
                            });
                            It("should reset the entity's @ContentId", () -> {
                                assertThat(document.getContentId(), is(nullValue()));
                            });
                        });
                    });
                });
            });
        });
    }
}
