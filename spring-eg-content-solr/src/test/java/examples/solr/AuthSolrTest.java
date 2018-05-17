package examples.solr;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.solr.SolrProperties;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = {SolrConfig.class})
public class AuthSolrTest {

    @Autowired private DocumentRepository docRepo;
    @Autowired private DocumentContentRepository docContentRepo;
    @Autowired private SOPDocumentRepository sopRepo;
    @Autowired private SOPDocumentContentRepository sopContentRepo;

    @Autowired private SolrClient solr;
    @Autowired private SolrProperties solrProperties;

    private Document doc;
    private SOPDocument sopDoc;

    private String id = null;
    private String url, user, password;

    {
        Describe("Solr Examples", () -> {
            Context("given a secured Solr Server", () -> {
                Context("given two documents of different types", () -> {
                    BeforeEach(() -> {
                        doc = new Document();
                        doc.setTitle("title of document 1");
                        doc.setAuthor("author@email.com");
                        docContentRepo.setContent(doc, this.getClass().getResourceAsStream("/one.docx"));
                        docRepo.save(doc);

                        sopDoc = new SOPDocument();
                        sopDoc.setTitle("title of sop document 1");
                        sopDoc.setAuthor("sopauthor@email.com");
                        sopContentRepo.setContent(sopDoc, this.getClass().getResourceAsStream("/one.docx"));
                        sopRepo.save(sopDoc);
                    });
                    AfterEach(() -> {
                        docContentRepo.unsetContent(doc);
                        docRepo.delete(doc);
                        sopContentRepo.unsetContent(sopDoc);
                        sopRepo.delete(sopDoc);
                    });
                    It("should index the content of that document", () -> {
                        SolrQuery query = new SolrQuery();
                        query.setQuery("one");
                        query.addFilterQuery("id:" + doc.getClass().getCanonicalName() + "\\:" + doc.getContentId().toString());
                        query.setFields("content");
                        QueryRequest request = new QueryRequest(query);
                        request.setBasicAuthCredentials(solrProperties.getUser(), solrProperties.getPassword());
                        QueryResponse response = request.process(solr);

                        SolrDocumentList results = response.getResults();

                        assertThat(results.size(), is(not(nullValue())));
                        assertThat(results.size(), is(1));
                    });
                    Context("when the content is searched", () -> {
                        It("should return the searched content and no more", () -> {
                            Iterable<String> content = docContentRepo.findKeyword("one");
                            List<String> result = stream(content.spliterator(), false).collect(Collectors.toList());
                            assertThat(result, hasItem(doc.getContentId()));
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
                            query2.addFilterQuery("id:" + doc.getClass().getCanonicalName() + "\\:" + doc.getContentId().toString());
                            query2.setFields("content");

                            QueryRequest request = new QueryRequest(query2);
                            request.setBasicAuthCredentials(solrProperties.getUser(), solrProperties.getPassword());
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
                            query.addFilterQuery("id:" + id);
                            query.setFields("content");

                            QueryRequest request = new QueryRequest(query);
                            request.setBasicAuthCredentials(solrProperties.getUser(), solrProperties.getPassword());
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
