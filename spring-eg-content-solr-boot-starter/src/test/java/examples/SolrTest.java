package examples;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest
public class SolrTest {

	@Autowired private DocumentRepository docRepo;
	@Autowired private DocumentContentRepository docContentRepo;
	
	@Autowired private SolrClient solr;
	
	private Document doc1;
	private Document doc2;
	private Document doc3;
	private Document doc4;

	private String id = null;
	
	{
		Describe("Solr Examples", () -> {
			Context("given a document", () -> {
				BeforeEach(() -> {
					doc1 = new Document();
					doc1.setTitle("title of document 1");
					doc1.setAuthor("author@email.com");
					docContentRepo.setContent(doc1, this.getClass().getResourceAsStream("/one.docx"));
					docRepo.save(doc1);
				});
				AfterEach(() -> {
					docContentRepo.unsetContent(doc1);
					
					docRepo.delete(doc1);
				});
				It("should index the content of that document", () -> {
					SolrQuery query = new SolrQuery();
					query.setQuery("one");
					query.addFilterQuery("id:" + doc1.getContentId().toString());
					query.setFields("content");
					
					QueryResponse response = solr.query(query);
					SolrDocumentList results = response.getResults();
					
					assertThat(results.size(), is(not(nullValue())));
					assertThat(results.size(), is(1));
				});
				Context("given that documents content is updated", () -> {
					BeforeEach(() -> {
						docContentRepo.setContent(doc1, this.getClass().getResourceAsStream("/two.rtf"));
						docRepo.save(doc1);
					});
					It("should index the new content", () -> {
						SolrQuery query2 = new SolrQuery();
						query2.setQuery("two");
						query2.addFilterQuery("id:" + doc1.getContentId().toString());
						query2.setFields("content");
						
						QueryResponse response = solr.query(query2);
						SolrDocumentList results = response.getResults();
						
						assertThat(results.size(), is(not(nullValue())));
						assertThat(results.size(), is(1));
					});
				});
				Context("given that document is deleted", () -> {
					BeforeEach(() -> {
						id = doc1.getContentId().toString();
						docContentRepo.unsetContent(doc1);
						docRepo.delete(doc1);
					});
					It("should delete the record of the content from the index", () -> {
						SolrQuery query = new SolrQuery();
						query.setQuery("one");
						query.addFilterQuery("id:" + id);
						query.setFields("content");

						QueryResponse response = solr.query(query);
						SolrDocumentList results = response.getResults();

						assertThat(results.size(), is(0));
					});
				});
			});

			It("should have a document and document content repository", () -> {
				assertThat(docRepo, is(not(nullValue())));
				assertThat(docContentRepo, is(not(nullValue())));
			});
		});
	}
	
	@Test
	public void noop() {
	}
}
