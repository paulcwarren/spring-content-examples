package examples.versioning;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ResponseBody;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.Subject;
import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.Collection;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = {examples.versioning.Application.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class RestVersioningTest {

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

    			// delete any existing claim forms
//    			Iterable<VersionedDocument> existingClaims = repo.findAll();
//    			for (VersionedDocument existingClaim : existingClaims) {
//    				store.unsetContent(existingClaim);
//    			}

    			// and claims
//    			for (VersionedDocument existingClaim : existingClaims) {
//    				repo.delete(existingClaim);
//    			}
    		});
    		Context("given a claim", () -> {
    			BeforeEach(() -> {
    		    	doc = new VersionedDocument();
    		    	doc.setData("John");
    		    	doc = repo.save(doc);
    			});
    			FIt("should be POSTable with new content with 201 Created", () -> {
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

					given()
							.auth().preemptive().basic("paul123", "password")
							.contentType("application/json")
							.content("{\"number\":\"1.1\",\"label\":\"some minor changes\"}".getBytes())
							.put("/versionedDocuments/" + doc.getId() + "/version")
							.then()
 							.statusCode(HttpStatus.SC_OK);

					given()
							.auth().basic("paul123", "password")
							.get("/versionedDocuments/" + doc.getId() + "/findAllLatestVersion")
							.then()
							.statusCode(HttpStatus.SC_OK);

						ResponseBody rb = given()
							.auth().basic("paul123", "password")
							.get("/versionedDocuments/" + doc.getId() + "/findAllVersions").body();
						rb.prettyPrint();
//							.then()
//							.statusCode(HttpStatus.SC_OK);
				});
    			Context("given that claim has existing content", () -> {
        			BeforeEach(() -> {
        		    	doc.setMimeType("plain/text");
        		    	store.setContent(doc, new ByteArrayInputStream("This is plain text content!".getBytes()));
//        		    	doc
//        		    	repo.save(doc);
        			});
    				It("should return the content with 200 OK", () -> {
    					given()
	    					.header("accept", "plain/text")
							.auth().basic("paul123", "password")
	    					.get("/versionedDocuments/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo("This is plain text content!"));
    				});
    				It("should be POSTable with new content with 201 Created", () -> {
    					String newContent = "This is new content";

    					given()
	    					.contentType("plain/text")
	    					.content(newContent.getBytes())
    					.when()
	    					.put("/versionedDocuments/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK);

    					given()
	    					.header("accept", "plain/text")
	    					.get("/versionedDocuments/" + doc.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo(newContent));
    				});
    				It("should be DELETEable with 204 No Content", () -> {
    					given()
    						.delete("/versionedDocuments/" + doc.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NO_CONTENT);

    					// and make sure that it is really gone
    					when()
    						.get("/versionedDocuments/" + doc.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NOT_FOUND);
    				});
    			});
			});
    	});
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
