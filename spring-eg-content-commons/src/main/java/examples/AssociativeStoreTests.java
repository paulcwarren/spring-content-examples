package examples;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentAssociativeStore;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.io.DeletableResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
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
    private Resource r;
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
                });
                Context("given that entity's resource", () -> {
                    BeforeEach(() -> {
                        r = store.getResource(document);
                    });
                    It("should not exist", () -> {
                        assertThat(r.exists(), is(false));
                    });
                    Context("given content is added to that resource", () -> {
                        BeforeEach(() -> {
                            InputStream is = this.getClass().getResourceAsStream("/claim_form.pdf");
                            OutputStream os = ((WritableResource)r).getOutputStream();
                            IOUtils.copy(is, os);
                            is.close();
                            os.close();
                        });
                        AfterEach(() -> {
                            try {
                                ((DeletableResource) r).delete();
                            } catch (Exception e) {
                                // do nothing
                            }
                        });
                        It("should then exist", () -> {
                            assertThat(r.exists(), is(true));
                        });
                        It("should store that content", () -> {
                            boolean matches = false;
                            InputStream expected = this.getClass().getResourceAsStream("/claim_form.pdf");
                            InputStream actual = null;
                            try {
                                actual = r.getInputStream();
                                matches = IOUtils.contentEquals(expected, actual);
                            } catch (IOException e) {
                            } finally {
                                IOUtils.closeQuietly(expected);
                                IOUtils.closeQuietly(actual);
                            }
                            assertThat(matches, Matchers.is(true));
                        });
                        It("should be associated with the entity", () -> {
                            assertThat(document.getContentId(), is(not(nullValue())));
                        });
                        Context("given that resource us then updated", () -> {
                            BeforeEach(() -> {
                                InputStream is = this.getClass().getResourceAsStream("/ACC_IN-1.DOC");
                                OutputStream os = ((WritableResource)r).getOutputStream();
                                IOUtils.copy(is, os);
                                is.close();
                                os.close();
                            });
                            It("should still exist", () -> {
                                assertThat(r.exists(), is(true));
                            });
                            It("should store that updated content", () -> {
                                boolean matches = false;
                                InputStream expected = this.getClass().getResourceAsStream("/ACC_IN-1.DOC");
                                InputStream actual = null;
                                try {
                                    actual = r.getInputStream();
                                    matches = IOUtils.contentEquals(expected, actual);
                                } catch (IOException e) {
                                } finally {
                                    IOUtils.closeQuietly(expected);
                                    IOUtils.closeQuietly(actual);
                                }
                                assertThat(matches, Matchers.is(true));
                            });
                            It("should still be associated", () -> {
                                assertThat(document.getContentId(), is(not(nullValue())));
                            });
                        });
                        Context("given that resource is then deleted", () -> {
                            BeforeEach(() -> {
                                try {
                                    ((DeletableResource) r).delete();
                                } catch (Exception e) {
                                    this.e = e;
                                }
                            });
                            It("should succeed", () -> {
                                assertThat(e, is(nullValue()));
                            });
                        });
                        Context("given the resource is forgotten", () -> {
                            BeforeEach(() -> {
                                // TODO: call store.forgetResource(document)
                            });
                            // TODO: It("should not exist", () - {});
                            // TODO: It("should not be associated, () - {}");
                        });
                    });
                });
            });
        });
    }
}
