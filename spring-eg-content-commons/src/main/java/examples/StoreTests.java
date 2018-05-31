package examples;

import examples.stores.DocumentStore;
import examples.utils.RandomString;
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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class StoreTests {

    @Autowired
    private DocumentStore store;

    private Resource r;
    private Exception e;

    {
        Describe("Store", () -> {
            Context("given a new resource", () -> {
                BeforeEach(() -> {
                    r = store.getResource(getId());
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
                    Context("given that resource is then updated", () -> {
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
                    });
                    Context("given that resource is then deleted", () -> {
                        BeforeEach(() -> {
                            try {
                                ((DeletableResource) r).delete();
                            } catch (Exception e) {
                                this.e = e;
                            }
                        });
                        It("should not exist", () -> {
                            assertThat(e, is(nullValue()));
                        });
                    });
                });
            });
        });
    }

    protected String getId() {
        RandomString random  = new RandomString(5);
        return "/store-tests/" + random.nextString();
    }
}
