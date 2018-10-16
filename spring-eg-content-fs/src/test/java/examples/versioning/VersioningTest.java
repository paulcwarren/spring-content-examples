package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import internal.org.springframework.versions.LockingService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.versions.LockOwnerException;
import org.springframework.versions.VersionInfo;

import javax.persistence.OptimisticLockException;
import javax.security.auth.Subject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FDescribe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@ContextConfiguration(classes = { VersioningConfig.class })
//@Ginkgo4jConfiguration(threads=1)
public class VersioningTest {

    private static Log logger = LogFactory.getLog(VersioningTest.class);

    @Autowired
    private VersionedDocumentAndVersioningRepository repo;

    @Autowired
    private VersionedDocumentStore store;

    @Autowired
    private LockingService lockingService;

    private Long v0Id, v1Id, v2Id, vstamp;
    private VersionedDocument doc, stale, next, old;
    private Throwable e;



    {
        Describe("LockingService", () -> {
            It("should behavior appropriately", () -> {
                VersionedDocument doc = new VersionedDocument();
                doc.setId(12345L);

                assertThat(lockingService.lock(doc.getId(), principal("some-user")), is(true));
                assertThat(lockingService.lock(doc.getId(), principal("some-other-user")), is(false));

                assertThat(lockingService.isLockOwner(doc.getId(), principal("some-user")), is(true));
                assertThat(lockingService.isLockOwner(doc.getId(), principal("some-other-user")), is(false));

                assertThat(lockingService.unlock(doc.getId(), principal("some-other-user")), is(false));
                assertThat(lockingService.unlock(doc.getId(), principal("some-user")), is(true));

                assertThat(lockingService.isLockOwner(doc.getId(), principal("some-user")), is(false));
            });
        });

//        Describe("LockingAndVersioningRepository", () -> {
//            Context("given a security context", () -> {
//                BeforeEach(() -> {
//                    securityContext("some-user");
//                });
//                Context("given a new entity", () -> {
//                    BeforeEach(() -> {
//                        doc = new VersionedDocument();
//                    });
//                    Context("when it is saved", () -> {
//                        BeforeEach(() -> {
//                            result = repo.save(doc);
//                        });
//                        It("should succeed and hold the lock", () -> {
//                            assertThat(result, is(not(nullValue())));
//                            assertThat(result.getId(), is(not(nullValue())));
//                            assertThat(result.getLockOwner(), is("some-user"));
//                        });
//                    });
//                    Context("given an existing, unlocked entity", () -> {
//                        BeforeEach(() -> {
//                            doc = repo.save(doc);
////                            doc = repo.unlock(doc);
//                            assertThat(doc.getLockOwner(), is(nullValue()));
//                        });
//                        Context("a lock can be obtained", () -> {
//                            BeforeEach(() -> {
//                                result = repo.lock(doc);
//                            });
//                            It("should succeed", () -> {
//                                assertThat(result, is(not(nullValue())));
//                                assertThat(result.getId(), is(not(nullValue())));
//                                assertThat(result.getLockOwner(), is("some-user"));
//                            });
//                            Context("given changes are made", () -> {
//                                BeforeEach(() -> {
//                                    doc.setData("some data");
//                                    result = repo.save(doc);
//                                });
//                                It("should succeed and lock should remain", () -> {
//                                    assertThat(result, is(not(nullValue())));
//                                    assertThat(result.getId(), is(not(nullValue())));
//                                    assertThat(result.getData(), is("some data"));
//                                    assertThat(result.getLockOwner(), is("some-user"));
//                                });
//                                Context("given is it unlocked", () -> {
//                                    BeforeEach(() -> {
//                                        result = repo.unlock(doc);
//                                    });
//                                    It("should relinqiush the lock and the changed data should persist", () -> {
//                                        assertThat(result, is(not(nullValue())));
//                                        assertThat(result.getId(), is(not(nullValue())));
//                                        assertThat(result.getData(), is("some data"));
//                                        assertThat(result.getLockOwner(), is(nullValue()));
//                                    });
//                                });
//                                Context("given another user tries to make changes", () -> {
//                                    BeforeEach(() -> {
//                                        securityContext("some-other-user");
//
//                                        doc.setData("some other user changes");
//                                        try {
//                                            result = repo.save(doc);
//                                        } catch (Exception e) {
//                                            this.e = e;
//                                        }
//                                    });
//                                    It("should fail with a not lock owner exception", () -> {
//                                        assertThat(result, is(not(nullValue())));
//                                        assertThat(result.getId(), is(not(nullValue())));
//                                        assertThat(result.getData(), is("some data"));
//                                        assertThat(result.getLockOwner(), is("some-user"));
//                                        assertThat(e, is(instanceOf(LockOwnerException.class)));
//                                    });
//                                });
//                            });
//                        });
//                    });
//                });
//            });
//        });

//        FDescribe("@Version semantics", () -> {
//            BeforeEach(() -> {
//                securityContext("some-user");
//            });
//            Context("given a new entity", () -> {
//                BeforeEach(() -> {
//                    doc = new VersionedDocument();
//                });
//                It("should have a vstamp of null", () -> {
//                    assertThat(doc.getVstamp(), is(nullValue()));
//                });
//                Context("when it is saved", () -> {
//                    BeforeEach(() -> {
//                        doc.setData("0");
//                        doc = repo.save(doc);
//                    });
//                    It("should increment the vstamp to 0", () -> {
//                        assertThat(doc.getVstamp(), is(0L));
//                    });
//                    Context("when it is saved again", () -> {
//                        BeforeEach(() -> {
//                            doc.setData("1");
//                            doc = repo.save(doc);
//                        });
//                        It("should increment the vstamp to 1", () -> {
//                            assertThat(doc.getVstamp(), is(1L));
//                        });
//                        Context("when it is saved again again", () -> {
//                            BeforeEach(() -> {
//                                doc.setData("2");
//                                doc = repo.save(doc);
//                            });
//                            It("should increment the vstamp to 2", () -> {
//                                assertThat(doc.getVstamp(), is(2L));
//                            });
//                            Context("when is it saved with an older vstamp", () -> {
//                                BeforeEach(() -> {
//                                    doc.setData("3");
//                                    doc.setVstamp(1L);
//                                    try {
//                                        doc = repo.save(doc);
//                                    } catch (Exception e) {
//                                        this.e = e;
//                                    }
//                                });
//                                It("should throw an OptimisticLockException", () -> {
//                                    assertThat(e, is(instanceOf(ObjectOptimisticLockingFailureException.class)));
//                                });
//                            });
//                        });
//                    });
//                    Context("when content is added", () -> {
//                        BeforeEach(() -> {
////                            doc.setData("foo");
////                            doc = repo.save(doc);
//                            store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
//                        });
//                        FIt("should update the doc's vstamp", () -> {
//                            doc = repo.findById(doc.getId()).get();
//                            assertThat(doc.getVstamp(), is(2L));
//                        });
//                    });
//                });
//            });
//        });
        Describe("Versioning", () -> {
            BeforeEach(() -> {

                securityContext("some-user");

                doc = new VersionedDocument();
                doc.setLatest(true);
                doc = repo.save(doc);
                assertThat(doc.getVstamp(), is(0L));

                v0Id = doc.getId();
            });
            It("should have a 1.0 version number", () -> {
                assertThat(doc.getVersion(), is("1.0"));
            });
            It("be returned as a latest version", () -> {
                assertThat(doc.getAncestralRootId(), is(nullValue()));
                assertThat(doc.getVstamp(), is(0L));

                List<VersionedDocument> latestVersions = repo.findAllLatestVersion();
                assertThat(latestVersions, hasItem(doc));
            });
            Context("when versioned", () -> {
                BeforeEach(() -> {
                    doc = repo.lock(doc);
                    assertThat(doc.getVstamp(), is(1L));

                    next = repo.version(doc, new VersionInfo("1.1", "some minor changes"));
                    v1Id = next.getId();
                });
                It("should create a new entity and carry over the lock", () -> {
                    assertThat(next.getId(), is(not(v0Id)));
                    assertThat(next.isLatest(), is(true));
                    assertThat(next.getAncestralRootId(), is(v0Id));
                    assertThat(next.getVstamp(), is(0L));
                    assertThat(next.getLockOwner(), is("some-user"));
                    assertThat(next.getVersion(), is("1.1"));
                    assertThat(next.getLabel(), is("some minor changes"));
                });
                It("should update existing as the ancestor and release its lock", () -> {
                    doc = repo.findById(v0Id).get();

                    assertThat(doc.getId(), is(v0Id));
                    assertThat(doc.isLatest(), is(false));
                    assertThat(doc.getAncestralRootId(), is(v0Id));
                    assertThat(doc.getVstamp(), is(2L));
                    assertThat(doc.getLockOwner(), is(nullValue()));
                    assertThat(doc.getVersion(), is("1.0"));
                    assertThat(doc.getLabel(), is(nullValue()));
                });
                Context("#findAllVersionsLatest", () -> {
                    It("should return the new version as latest but not the old", () -> {
                        doc = repo.findById(v0Id).get();

                        List<VersionedDocument> latestVersions = repo.findAllLatestVersion();
                        assertThat(latestVersions, hasItem(next));
                        assertThat(latestVersions, not(hasItem(doc)));
                    });
                });
                Context("#findAllVersions with the latest version", () ->{
                    It("should return both entities as versions", () -> {
                        doc = repo.findById(v0Id).get();

                         List<VersionedDocument> versions = repo.findAllVersions(next);
                        assertThat(versions, hasItem(next));
                        assertThat(versions, hasItem(doc));
                    });
                });
                Context("#findAllVersions with an old version", () ->{
                    It("should return both entities as versions", () -> {
                        doc = repo.findById(v0Id).get();

                        List<VersionedDocument> versions = repo.findAllVersions(doc);
                        assertThat(versions, hasItem(next));
                        assertThat(versions, hasItem(doc));
                    });
                });
                Context("when new version is unlocked", () -> {
                    BeforeEach(() -> {
                        next = repo.unlock(next);
                    });
                    It("should succeed", () -> {
                        assertThat(next.isLatest(), is(true));
                        assertThat(next.getAncestralRootId(), is(v0Id));
                        assertThat(next.getVstamp(), is(1L));
                        assertThat(next.getLockOwner(), is(nullValue()));
                    });
                });
                Context("when an unlock is attempted on the old version", () -> {
                    BeforeEach(() -> {
                        doc = repo.findById(v0Id).get();
                        try {
                            doc = repo.unlock(doc);
                        } catch (Exception e) {
                            this.e = e;
                        }
                    });
                    It("should fail", () -> {
                        assertThat(e, is(instanceOf(LockOwnerException.class)));
                    });
                });
                Context("when versioned again", () -> {
                    BeforeEach(() -> {
                        next = repo.version(next, new VersionInfo("2.0", "some major changes"));
                        v2Id = next.getId();
                    });
                    It("should create a new entity and carry over the lock", () -> {
                        assertThat(next.getId(), is(not(v0Id)));
                        assertThat(next.getId(), is(not(v1Id)));
                        assertThat(next.isLatest(), is(true));
                        assertThat(next.getAncestorId(), is(v1Id));
                        assertThat(next.getAncestralRootId(), is(v0Id));
                        assertThat(next.getVstamp(), is(0L));
                        assertThat(next.getLockOwner(), is("some-user"));
                        assertThat(next.getVersion(), is("2.0"));
                        assertThat(next.getLabel(), is("some major changes"));
                    });
                    It("should update existing as the ancestor and release its lock", () -> {
                        doc = repo.findById(v1Id).get();

                        assertThat(doc.getId(), is(v1Id));
                        assertThat(doc.isLatest(), is(false));
                        assertThat(doc.getAncestorId(), is(v0Id));
                        assertThat(doc.getAncestralRootId(), is(v0Id));
                        assertThat(doc.getVstamp(), is(1L));
                        assertThat(doc.getLockOwner(), is(nullValue()));
                        assertThat(doc.getVersion(), is("1.1"));
                        assertThat(doc.getLabel(), is("some minor changes"));
                    });
                });
            });
        });

        Describe("Pessimistic Locking", () -> {
            BeforeEach(() -> {
                securityContext("some-user");

                doc = new VersionedDocument();
                doc = repo.save(doc);
                assertThat(doc.getVstamp(), is(0L));

                v0Id = doc.getId();
            });
            It("should allow saves on unlocked objects", () -> {

                // metadata update + save
                {
                    doc.setData("update 1");
                    doc = repo.save(doc);
                    assertThat(doc.getVstamp(), is(1L));
                }

                // content update + save
                {
                    try {
                        store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                        doc = repo.findById(v0Id).get();
                    } catch (Exception e1) {
                        e = e1;
                    }
                    assertThat(e, is(nullValue()));
                    assertThat(doc.getVstamp(), is(2L));
                }

                // some-other-user fetches entity
                {
                    securityContext("some-other-user");
                }

                // metadata update + save
                {
                    doc.setData("update 2");
                    doc = repo.save(doc);
                    assertThat(doc.getVstamp(), is(3L));
                }

                // some-other-user content update + save
                {
                    try {
                        store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                        doc = repo.findById(v0Id).get();
                    } catch (Exception e1) {
                        e = e1;
                    }
                    assertThat(e, is(nullValue()));
                    assertThat(doc.getVstamp(), is(4L));
                }
            });
            Context("when an entity is locked", () -> {
                BeforeEach(() -> {
                    doc = repo.lock(doc);
                    assertThat(doc.getVstamp(), is(1L));
                });
                It("should accept metadata updates from the lock owner", () -> {

                    // metadata update + save
                    {
                        doc.setData("update 1");
                        doc = repo.save(doc);
                        assertThat(doc.getVstamp(), is(2L));
                    }
                });
                It("should reject metadata updates from a non-lock owner", () -> {

                    securityContext("some-other-user");

                    // metadata update + save
                    {
                        doc.setData("update 1");
                        try {
                            doc = repo.save(doc);
                        } catch (Exception e) {
                            this.e = e;
                        }
                        assertThat(e, is(instanceOf(LockOwnerException.class)));
                        assertThat(doc.getVstamp(), is(1L));
                    }

                });
                It("should accept content updates from the lock owner", () -> {

                    // content update + save
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                            doc = repo.findById(v0Id).get();
                        } catch (Exception e1) {
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                        assertThat(doc.getVstamp(), is(2L));
                    }
                });
                It("should reject content updates from the non-lock owner", () -> {

                    securityContext("some-other-user");

                    // some-other-user content update + save
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                            doc = repo.findById(v0Id).get();
                        } catch (Exception e1) {
                            e = e1;
                        }
                        assertThat(e, is(instanceOf(LockOwnerException.class)));
                        assertThat(doc.getVstamp(), is(1L));
                    }
                });
                Context("given there is content", () -> {
                    BeforeEach(() -> {
                        store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                        doc = repo.findById(v0Id).get();
                        assertThat(doc.getVstamp(), is(2L));
                    });
                    It("should accept content deletions from the lock owner", () -> {

                        // content update + save
                        {
                            try {
                                store.unsetContent(doc);
                                doc = repo.findById(v0Id).get();
                            } catch (Exception e1) {
                                e = e1;
                            }
                            assertThat(e, is(nullValue()));
                            assertThat(doc.getVstamp(), is(3L));
                        }
                    });
                    It("should reject content deletions from the non-lock owner", () -> {

                        securityContext("some-other-user");

                        // some-other-user content update + save
                        {
                            try {
                                store.unsetContent(doc);
                                doc = repo.findById(v0Id).get();
                            } catch (Exception e1) {
                                e = e1;
                            }
                            assertThat(e, is(instanceOf(LockOwnerException.class)));
                            assertThat(doc.getVstamp(), is(2L));
                        }
                    });
                    It("should accept content fetches from the lock owner", () -> {

                        {
                            String content = null;

                            try {
                                InputStream in = store.getContent(doc);
                                content = IOUtils.toString(in);
                                IOUtils.closeQuietly(in);

                                doc = repo.findById(v0Id).get();
                            } catch (Exception e1) {
                                e = e1;
                            }
                            assertThat(e, is(nullValue()));
                            assertThat(content, is("foo"));
                            assertThat(doc.getVstamp(), is(2L));
                        }
                    });
                    It("should accept content fetches from the non-lock owner", () -> {

                        securityContext("sone-other-user");

                        {
                            String content = null;

                            try {
                                InputStream in = store.getContent(doc);
                                content = IOUtils.toString(in);
                                IOUtils.closeQuietly(in);

                                doc = repo.findById(v0Id).get();
                            } catch (Exception e1) {
                                e = e1;
                            }
                            assertThat(e, is(nullValue()));
                            assertThat(content, is("foo"));
                            assertThat(doc.getVstamp(), is(2L));
                        }
                    });
                });
                It("should be accept unlock by the lock owner", () -> {
                    doc = repo.unlock(doc);
                    assertThat(doc.getVstamp(), is(2L));
                });
                It("should be reject unlock by the lock owner", () -> {

                    securityContext("some-other-user");

                    try {
                        doc = repo.unlock(doc);
                    } catch (Exception e) {
                        this.e = e;
                    }
                    assertThat(e, is(instanceOf(LockOwnerException.class)));
                    assertThat(doc.getVstamp(), is(1L));
                });
            });
        });

        FDescribe("Optimistic Locking", () -> {
            Describe("setContent", () -> {
                It("should reject content updates to a stale entity", () -> {

                    securityContext("some-user");

                    // create entity
                    {
                        doc = new VersionedDocument();
                        try {
                            doc = repo.save(doc);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        assertThat(doc.getVstamp(), is(0L));
                        v0Id = doc.getId();
                        vstamp = doc.getVstamp();
                    }

                    // fetch
                    {
                        stale = repo.findById(v0Id).get();
                        assertThat(stale.getVstamp(), is(vstamp));
                    }

                    // update entity (to invalidate 'stale')
                    {
                        doc.setData("updated");
                        doc = repo.save(doc);
                        assertThat(doc.getVstamp(), is(vstamp + 1));
                    }

                    // set content with stale entity
                    {
                        try {
                            store.setContent(stale, new ByteArrayInputStream("foo".getBytes()));
                        } catch (Exception e1) {
                            e = e1;
                        }
                        assertThat(e, is(instanceOf(OptimisticLockException.class)));

                        stale = repo.findById(v0Id).get();
                        assertThat(stale.getVstamp(), is(vstamp + 1));
                    }
                });

                It("should accept content updates to a current entity", () -> {

                    // create new entity
                    {
                        doc = new VersionedDocument();
                        doc = repo.save(doc);
                        v0Id = doc.getId();
                        assertThat(doc.getVstamp(), is(0L));
                        vstamp = doc.getVstamp();
                    }

                    // add content
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }

                    }

                    // re-fetch so we have the latest
                    {
                        doc = repo.findById(v0Id).get();
                    }

                    // update content
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("bar".getBytes()));
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                    }

                    // check content was updated
                    {
                        doc = repo.findById(v0Id).get();
                        assertThat(doc.getVstamp(), is(vstamp + 2));
                        InputStream content = store.getContent(doc);
                        assertThat(IOUtils.toString(content), is("bar"));
                        IOUtils.closeQuietly(content);
                    }
                });
            });

            Describe("getContent", () -> {
                It("should return content for a current entity", () -> {

                    // create new entity
                    {
                        doc = new VersionedDocument();
                        try {
                            doc = repo.save(doc);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        assertThat(doc.getVstamp(), is(0L));
                        v0Id = doc.getId();
                        vstamp = doc.getVstamp();
                    }

                    // add content
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                    }

                    // fetch
                    {
                        doc = repo.findById(v0Id).get();
                        assertThat(doc.getVstamp(), is(vstamp + 1));
                    }

                    // check content
                    {
                        InputStream content = store.getContent(doc);
                        assertThat(IOUtils.toString(content), is("foo"));
                        IOUtils.closeQuietly(content);
                    }

                    // update content
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("bar".getBytes()));
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                    }

                    // fetch
                    {
                        doc = repo.findById(v0Id).get();
                        assertThat(doc.getVstamp(), is(vstamp + 2));
                    }

                    // check content
                    {
                        InputStream content = store.getContent(doc);
                        assertThat(IOUtils.toString(content), is("bar"));
                        IOUtils.closeQuietly(content);
                    }
                });

                It("should reject content request for a stale entity", () -> {

                    // create new entity
                    {
                        doc = new VersionedDocument();
                        try {
                            doc = repo.save(doc);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        assertThat(doc.getVstamp(), is(0L));
                        v0Id = doc.getId();
                        vstamp = doc.getVstamp();
                    }

                    // add content
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                    }

                    // fetch
                    {
                        doc = repo.findById(v0Id).get();
                        assertThat(doc.getVstamp(), is(vstamp + 1));

                        stale = repo.findById(v0Id).get();
                    }

                    // check content
                    {
                        InputStream content = store.getContent(doc);
                        assertThat(IOUtils.toString(content), is("foo"));
                        IOUtils.closeQuietly(content);
                    }

                    // update content
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("bar".getBytes()));
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                    }

                    // check content
                    {
                        InputStream content = null;
                        try {
                            content = store.getContent(stale);
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        } finally {
                            IOUtils.closeQuietly(content);
                        }
                        assertThat(e, is(instanceOf(OptimisticLockException.class)));
                    }
                });
            });

            Describe("unsetContent", () -> {
                It("should reject content deletions to a stale entity", () -> {

                    // create entity
                    {
                        doc = new VersionedDocument();
                        logger.info(format("thread 1 after creation, vstamp: %s", doc.getVstamp()));
                        doc = repo.save(doc);
                        assertThat(doc.getVstamp(), is(0L));

                        v0Id = doc.getId();
                        vstamp = doc.getVstamp();
                    }

                    // fetch
                    {
                        stale = repo.findById(v0Id).get();
                        assertThat(stale.getVstamp(), is(vstamp));
                    }

                    // update entity
                    {
                        doc.setData("updated");
                        doc = repo.save(doc);
                        assertThat(doc.getVstamp(), is(vstamp + 1));
                    }

                    // set content on stale entity
                    {
                        try {
                            logger.info(format("thread 2 unsetting content %s", v0Id));
                            store.unsetContent(stale);
                        } catch (Exception e1) {
                            e = e1;
                        }
                        assertThat(e, is(instanceOf(OptimisticLockException.class)));
                    }
                });

                It("should accept content deletions to a current entity", () -> {

                    // create entity
                    {
                        doc = new VersionedDocument();
                        doc = repo.save(doc);

                        v0Id = doc.getId();
                        assertThat(doc.getVstamp(), is(0L));
                        vstamp = doc.getVstamp();
                    }

                    // update content
                    {
                        try {
                            store.setContent(doc, new ByteArrayInputStream("foo".getBytes()));
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                    }

                    // fetch
                    {
                        doc = repo.findById(v0Id).get();
                        assertThat(doc.getVstamp(), is(vstamp + 1));
                    }

                    // delete content
                    {
                        try {
                            store.unsetContent(doc);
                        } catch (Exception e1) {
                            logger.error(e1);
                            e = e1;
                        }
                        assertThat(e, is(nullValue()));
                    }


                    // fetch
                    {
                        doc = repo.findById(v0Id).get();
                        assertThat(doc.getVstamp(), is(vstamp + 2));
                        assertThat(store.getContent(doc), is(nullValue()));
                    }
                });
            });
        });
    }

    private static void sleep() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private static void inParallel(Runnable f1, Runnable f2) throws Throwable {
        CountDownLatch latch = new CountDownLatch(2);

        final Throwable t = null;
        DelegationRunner r1 = new VersioningTest.DelegationRunner(f1, latch);
        DelegationRunner r2 = new VersioningTest.DelegationRunner(f2, latch);

        try {
            new Thread(r1).start();
            new Thread(r2).start();

            latch.await();
        } finally {
            if (r1.getThrowable() != null) {
                throw r1.getThrowable();
            }
            if (r2.getThrowable() != null) {
                throw r2.getThrowable();
            }
        }
    }

    private static class DelegationRunner implements Runnable {

        private Runnable delegate;
        private CountDownLatch latch;
        private Throwable t;

        public DelegationRunner(Runnable delegate, CountDownLatch latch) {
            this.delegate = delegate;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                delegate.run();
            } catch (Throwable t1) {
                t = t1;
            } finally {
                latch.countDown();
            }
        }

        public Throwable getThrowable() {
            return t;
        }
    }

    private static void securityContext(String user) {
        SecurityContextHolder.setContext(new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                return new Authentication() {
                    @Override
                    public String getName() {
                        return user;
                    }

                    @Override
                    public boolean implies(Subject subject) {
                        return false;
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        return null;
                    }

                    @Override
                    public Object getPrincipal() {
                        return user;
                    }

                    @Override
                    public boolean isAuthenticated() {
                        return true;
                    }

                    @Override
                    public void setAuthenticated(boolean b) throws IllegalArgumentException {

                    }
                };
            }

            @Override
            public void setAuthentication(Authentication authentication) {

            }
        });
    }

    private static Principal principal(String user) {
        return () -> user;
    }

    @Test
    public void noop() {}
}
