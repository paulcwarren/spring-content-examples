package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import tests.versioning.VersionedDocument;
import tests.versioning.VersionedDocumentAndVersioningRepository;
import tests.versioning.VersionedDocumentStore;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = {examples.versioning.Application.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class FsRestVersioningTest {

	@Autowired
	private VersionedDocumentAndVersioningRepository repo;

	@Autowired
	private VersionedDocumentStore store;

    @LocalServerPort
    int port;

    private VersionedDocument doc;

    {
    	Describe("Spring Content REST", () -> {
    		BeforeEach(() -> {
    			RestAssured.port = port;
    		});
    		Context("given a claim", () -> {
    			BeforeEach(() -> {
    		    	doc = new VersionedDocument();
    		    	doc.setData("John");
    		    	doc = repo.save(doc);
    			});
    			It("should be able to version an entity and its content", () -> {
    				// assert content does not exist
					given()
						.auth().basic("paul123", "password")
					.when()
						.get("/versionedDocuments/" + doc.getId())
					.then()
						.assertThat()
						.statusCode(HttpStatus.SC_NOT_FOUND);

					String newContent = "This is some new content";

					// POST the new content
					given()
    					.contentType("plain/text")
    					.content(newContent.getBytes())
						.auth().preemptive().basic("paul123", "password")
					.when()
    					.put("/versionedDocuments/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_CREATED);

					// assert that it now exists
					given()
						.auth().basic("paul123", "password")
    					.header("accept", "plain/text")
    					.get("/versionedDocuments/" + doc.getId())
					.then()
    					.statusCode(HttpStatus.SC_OK)
    					.assertThat()
    					.contentType(Matchers.startsWith("plain/text"))
    					.body(Matchers.equalTo(newContent));

					given()
							.auth().basic("paul123", "password")
							.put("/versionedDocuments/" + doc.getId() + "/lock")
					.then()
							.statusCode(HttpStatus.SC_OK);

					// POST the new content as john
					given()
							.contentType("plain/text")
							.content("john's content".getBytes())
							.auth().preemptive().basic("john123", "password")
							.when()
							.put("/versionedDocuments/" + doc.getId())
							.then()
							.statusCode(is(409));

					JsonPath response =
					given()
							.auth().preemptive().basic("paul123", "password")
							.contentType("application/json")
							.content("{\"number\":\"1.1\",\"label\":\"some minor changes\"}".getBytes())
							.put("/versionedDocuments/" + doc.getId() + "/version")
							.then()
 							.statusCode(HttpStatus.SC_OK)
							.extract().jsonPath();
					assertThat(response.get("_links.self.href"), endsWith("versionedDocuments/" + (doc.getId() + 1)));

					response =
					given()
							.auth().basic("paul123", "password")
							.get("/versionedDocuments/findAllLatestVersion")
							.then()
							.statusCode(HttpStatus.SC_OK)
							.extract().jsonPath();
					assertThat(response.get("_embedded.versionedDocuments[0].version"), is("1.1"));
					assertThat(response.get("_embedded.versionedDocuments[0].latest"), is(true));

					response =
					given()
							.auth().basic("paul123", "password")
							.get("/versionedDocuments/" + doc.getId() + "/findAllVersions")
							.then()
							.statusCode(HttpStatus.SC_OK)
							.extract().jsonPath();
					assertThat(response.get("_embedded.versionedDocuments[0].version"), is("1.0"));
					assertThat(response.get("_embedded.versionedDocuments[0].latest"), is(false));
					assertThat(response.get("_embedded.versionedDocuments[1].version"), is("1.1"));
					assertThat(response.get("_embedded.versionedDocuments[1].latest"), is(true));
				});
			});
    	});
    }

	@Test
    public void noop() {}
}
