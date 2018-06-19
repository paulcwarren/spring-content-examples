package examples.solrboot;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FContext;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.jayway.restassured.RestAssured.given;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;
import org.springframework.boot.web.server.LocalServerPort;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
public class SearchContentRestTest {

	@Autowired
	private DocumentRepository docRepo;

	@Autowired
	private DocumentContentRepository docContentStore;

    @LocalServerPort
    int port;
    
    private Document existingDoc;
    
    {
    	Describe("Search Content REST Endpoint Examples", () -> {

			BeforeEach(() -> {
    			RestAssured.port = port;

    			// delete any existing docs
    			Iterable<Document> existingDocs = docRepo.findAll();
    			for (Document existingDoc : existingDocs) {
    				docContentStore.unsetContent(existingDoc);
    			}
    			docRepo.deleteAll();
    		});
    		Context("given a document with content", () -> {
    			BeforeEach(() -> {
    		    	existingDoc = new Document();
    		    	docContentStore.setContent(existingDoc, this.getClass().getResourceAsStream("/one.docx"));
    		    	docRepo.save(existingDoc);
    			});
    			It("should be findable via the /searchcontents REST endpoint", () -> {
    				Response resp =
    						given()
        		    			.header("accept", "application/hal+json")
    						.when()
	    						.get("/documents/searchContent/findKeyword?keyword=one")
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
