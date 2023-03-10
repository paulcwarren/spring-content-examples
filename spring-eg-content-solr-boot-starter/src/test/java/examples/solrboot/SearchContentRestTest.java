package examples.solrboot;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.StringReader;

import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.content.solr.SolrProperties;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = {Application.class, TestConfig.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class SearchContentRestTest {

    @Autowired
    private DocumentRepository docRepo;

    @Autowired
    private DocumentContentRepository docContentStore;

    @Autowired
    private SolrProperties solrProperties;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Document existingDoc;

    {
        Describe("Search Content REST Endpoint Examples", () -> {

            BeforeEach(() -> {
                RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
            });
            Context("given a document with content", () -> {
                BeforeEach(() -> {
                    solrProperties.setUser(System.getenv("SOLR_USER"));
                    solrProperties.setPassword(System.getenv("SOLR_PASSWORD"));

                    existingDoc = new Document();
                    docContentStore.setContent(existingDoc, this.getClass().getResourceAsStream("/one.docx"));
                    docRepo.save(existingDoc);
                });
                It("should be findable via the /searchContents REST endpoint", () -> {
                    MockMvcResponse resp =
                            given()
                            .header("accept", "application/hal+json")
                            .when()
                            .get("/documents/searchContent?queryString=one")
                            .andReturn();

                    resp.then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);

                    RepresentationFactory representationFactory = new StandardRepresentationFactory();
                    ReadableRepresentation halResponse = representationFactory.readRepresentation("application/hal+json", new StringReader(resp.asString()));
                    assertThat(halResponse.getResourcesByRel("documents").size(), is(1));
                    assertThat(halResponse.getResourcesByRel("documents").get(0).getValue("contentId"), is(existingDoc.getContentId().toString()));
                });
            });
        });
    }

    @Test
    public void noop() {}
}
