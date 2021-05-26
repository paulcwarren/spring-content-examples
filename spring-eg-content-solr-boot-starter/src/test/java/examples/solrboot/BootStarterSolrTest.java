package examples.solrboot;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.content.solr.SolrProperties;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes={Application.class, TestConfig.class})
public class BootStarterSolrTest {

    @Autowired private DocumentRepository docRepo;
    @Autowired private DocumentContentRepository docContentRepo;

    @Autowired private SolrClient solr; //for tests
    @Autowired private SolrProperties solrProperties;

    private Document doc;

    private String id = null;
    {
        Describe("Solr Examples", () -> {
            Context("given a Solr Server", () -> {
                Context("given a document", () -> {
                    BeforeEach(() -> {
                        doc = new Document();
                        doc.setTitle("title of document 1");
                        doc.setAuthor("author@email.com");
                        docContentRepo.setContent(doc, this.getClass().getResourceAsStream("/one.docx"));
                        docRepo.save(doc);
                    });
                    AfterEach(() -> {
                        docContentRepo.unsetContent(doc);
                        docRepo.delete(doc);
                    });
                    It("should index the content of that document", () -> {
                        SolrQuery query = new SolrQuery();
                        query.setQuery("one");
                        query.addFilterQuery("id:" + "examples.solrboot.Document\\:" + doc.getContentId().toString());
                        query.setFields("content");

                        QueryRequest request = new QueryRequest(query);
                        request.setBasicAuthCredentials(System.getenv("SOLR_USER"), System.getenv("SOLR_PASSWORD"));
                        QueryResponse response = request.process(solr);

                        SolrDocumentList results = response.getResults();

                        assertThat(results.size(), is(not(nullValue())));
                        assertThat(results.size(), is(1));
                    });
                    Context("when the content is searched", () -> {
                        It("should return the searched content", () -> {
                            Iterable<String> content = docContentRepo.search("one");
                            assertThat(content, CoreMatchers.hasItem(doc.getContentId()));
                        });
                    });
                    Context("given that documents content is updated", () -> {
                        BeforeEach(() -> {
                            docContentRepo.setContent(doc, this.getClass().getResourceAsStream("/two.rtf"));
                            docRepo.save(doc);
                        });
                        It("should index the new content", () -> {
                            SolrQuery query2 = new SolrQuery();
                            query2.setQuery("two");
                            query2.addFilterQuery("id:examples.solrboot.Document\\:" + doc.getContentId().toString());
                            query2.setFields("content");

                            QueryRequest request = new QueryRequest(query2);
                            request.setBasicAuthCredentials(System.getenv("SOLR_USER"), System.getenv("SOLR_PASSWORD"));
                            QueryResponse response = request.process(solr);
                            SolrDocumentList results = response.getResults();

                            assertThat(results.size(), is(not(nullValue())));
                            assertThat(results.size(), is(1));
                        });
                    });
                    Context("given that document is deleted", () -> {
                        BeforeEach(() -> {
                            id = doc.getContentId().toString();
                            docContentRepo.unsetContent(doc);
                            docRepo.delete(doc);
                        });
                        It("should delete the record of the content from the index", () -> {
                            SolrQuery query = new SolrQuery();
                            query.setQuery("one");
                            query.addFilterQuery("id:" + "examples.solrboot.Document\\:" + id);
                            query.setFields("content");

                            QueryRequest request = new QueryRequest(query);
                            request.setBasicAuthCredentials(System.getenv("SOLR_USER"), System.getenv("SOLR_PASSWORD"));
                            QueryResponse response = request.process(solr);
                            SolrDocumentList results = response.getResults();

                            assertThat(results.size(), is(0));
                        });
                    });
                });
            });
        });
    }

    @Test
    public void noop() {
    }
}
