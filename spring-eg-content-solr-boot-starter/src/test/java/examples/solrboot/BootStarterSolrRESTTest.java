package examples.solrboot;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.apache.solr.client.solrj.SolrClient;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.content.solr.SolrProperties;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.when;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
public class BootStarterSolrRESTTest {

	@Autowired private DocumentRepository docRepo;
	@Autowired private DocumentContentRepository docContentRepo;
	
	@Autowired private SolrClient solr; //for tests
	@Autowired private SolrProperties solrProperties;

	private Document doc;

	@LocalServerPort
    int port;

	private String id = null;
{
		Describe("Solr Examples", () -> {
			BeforeEach(() -> {
    			RestAssured.port = port;
			});
			Context("given a insecure Solr Server", () -> {
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
					Context("when the content is searched", () -> {
						It("should return the searched content", () -> {
							when()
							.get("/documents/searchContent?queryString=one")
						.then()
							.assertThat()
							.statusCode(HttpStatus.SC_OK)
	    					.body(Matchers.containsString(doc.getId().toString()));
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
