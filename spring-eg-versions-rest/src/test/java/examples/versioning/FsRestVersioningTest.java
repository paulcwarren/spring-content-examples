package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.web.context.WebApplicationContext;
import tests.versioning.VersionedDocument;
import tests.versioning.VersionedDocumentAndVersioningRepository;
import tests.versioning.VersionedDocumentStore;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import io.restassured.path.json.JsonPath;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = {examples.versioning.Application.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class FsRestVersioningTest {

	@Autowired
	private VersionedDocumentAndVersioningRepository repo;

	@Autowired
	private VersionedDocumentStore store;

	@Autowired
	private WebApplicationContext webApplicationContext;

    private VersionedDocument doc;

    {
    	Describe("Spring Content REST Versioning", () -> {
    		BeforeEach(() -> {
				RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    		});
    		Context("given a versionable entity with content", () -> {
    			BeforeEach(() -> {
    		    	doc = new VersionedDocument();
    		    	doc.setData("John");
    		    	doc = repo.save(doc);
    			});
    			It("should be able to version an entity and its content", () -> {
    				// assert content does not exist
					given()
//						.auth().basic("paul123", "password")
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul123").password("password"))
					.when()
						.get("/versionedDocumentsContent/" + doc.getId())
					.then()
						.assertThat()
						.statusCode(HttpStatus.SC_NOT_FOUND);

					String newContent = "This is some new content";

					// POST the new content
					given()
    					.contentType("plain/text")
    					.body(newContent.getBytes())
//						.auth().preemptive().basic("paul123", "password")
						.auth().with(SecurityMockMvcRequestPostProcessors.user("paul123").password("password"))
					.when()
    					.put("/versionedDocumentsContent/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_CREATED);

					// assert that it now exists
					given()
//						.auth().basic("paul123", "password")
						.auth().with(SecurityMockMvcRequestPostProcessors.user("paul123").password("password"))
    					.header("accept", "plain/text")
    					.get("/versionedDocumentsContent/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_OK)
    					.assertThat()
    					.contentType(Matchers.startsWith("plain/text"))
    					.body(Matchers.equalTo(newContent));

					JsonPath response =
					given()
//							.auth().basic("paul123", "password")
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul123").password("password"))
							.header("accept", "application/json")
							.put("/versionedDocuments/" + doc.getId() + "/lock")
					.then()
							.statusCode(HttpStatus.SC_OK).extract().jsonPath();

					// POST the new content as john
					given()
							.contentType("plain/text")
							.body("john's content".getBytes())
//							.auth().preemptive().basic("john123", "password")
							.auth().with(SecurityMockMvcRequestPostProcessors.user("john123").password("password"))
							.when()
							.put("/versionedDocumentsContent/" + doc.getId())
							.then()
							.statusCode(is(409));

					response =
					given()
//							.auth().preemptive().basic("paul123", "password")
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul123").password("password"))
							.contentType("application/json")
							.body("{\"number\":\"1.1\",\"label\":\"some minor changes\"}".getBytes())
							.put("/versionedDocuments/" + doc.getId() + "/version")
							.then()
 							.statusCode(HttpStatus.SC_OK)
							.extract().jsonPath();
					assertThat(response.get("_links.self.href"), endsWith("versionedDocuments/" + (doc.getId() + 1)));

					response =
					given()
//							.auth().basic("paul123", "password")
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul123").password("password"))
							.get("/versionedDocuments/findAllVersionsLatest")
							.then()
							.statusCode(HttpStatus.SC_OK)
							.extract().jsonPath();
					assertThat(response.get("_embedded.versionedDocuments[0].version"), is("1.1"));
					assertThat(response.get("_embedded.versionedDocuments[0].successorId"), is(nullValue()));

					response =
					given()
//							.auth().basic("paul123", "password")
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul123").password("password"))
							.get("/versionedDocuments/" + (doc.getId() + 1) + "/findAllVersions")
							.then()
							.statusCode(HttpStatus.SC_OK)
							.extract().jsonPath();
					assertThat(response.get("_embedded.versionedDocuments[0].version"), is("1.0"));
					assertThat(response.get("_embedded.versionedDocuments[0].successorId"), is((int)(doc.getId() + 1)));
					assertThat(response.get("_embedded.versionedDocuments[1].version"), is("1.1"));
					assertThat(response.get("_embedded.versionedDocuments[1].successorId"), is(nullValue()));
				});
			});
    	});
    }

	@Test
    public void noop() {}
}
