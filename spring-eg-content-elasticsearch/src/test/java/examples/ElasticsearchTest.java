package examples;

import java.time.Duration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import model.Document;
import model.DocumentContentStore;
import model.DocumentRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.github.grantwest.eventually.EventuallyLambdaMatcher.eventuallyEval;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
//@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = {EmbeddedElasticConfig.class, ElasticsearchConfig.class})
public class ElasticsearchTest {

	@Autowired
	private DocumentRepository repo;

	@Autowired
	private DocumentContentStore store;

	@Autowired
	private EmbeddedElastic server;

	@Autowired
	private RestHighLevelClient client;

	private Document doc;
	private String id = null;

	{
		Describe("Elasticsearch Examples", () -> {

			Context("given a document", () -> {

				BeforeEach(() -> {
					doc = new Document();
					doc.setTitle("title of document 1");
					doc.setAuthor("author@email.com");
					store.setContent(doc, this.getClass().getResourceAsStream("/one.docx"));
					doc = repo.save(doc);
				});

				AfterEach(() -> {
					store.unsetContent(doc);
					repo.delete(doc);
				});

				Context("when the content is searched", () -> {
					It("should return the document", () -> {
						assertThat(() -> store.search("one"), eventuallyEval(hasItem(doc.getContentId()), Duration.ofSeconds(10)));
					});
				});
			});
		});
	}

	@Test
	public void noop() {
	}
}