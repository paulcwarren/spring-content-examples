package examples.hypermedia;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.stores.DocumentContentStore;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import java.io.ByteArrayInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class FsRestTest {

   @Autowired
   private DocumentRepository claimRepo;
   
   @Autowired
   private DocumentContentStore claimFormStore;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Document canSetClaim;
    private Document canGetClaim;
    private Document canDelClaim;
    
    @Before
    public void setUp() throws Exception {

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
      
      // create a claim that can set content on
      canSetClaim = new Document();
      claimRepo.save(canSetClaim);

      // create a claim that can get content from
      canGetClaim = new Document();
      claimRepo.save(canGetClaim);
      canGetClaim.setMimeType("plain/text");
      claimFormStore.setContent(canGetClaim, new ByteArrayInputStream("This is plain text content!".getBytes()));
      claimRepo.save(canGetClaim);

      // create a doc that can delete content from
      canDelClaim = new Document();
      claimRepo.save(canDelClaim);
      canDelClaim.setMimeType("plain/text");
      claimFormStore.setContent(canDelClaim, new ByteArrayInputStream("This is plain text content!".getBytes()));
      claimRepo.save(canDelClaim);
    }

    @Test
    public void canSetContent() {
      given()
         .contentType("plain/text")
         .body("This is plain text content!".getBytes())
      .when()
         .post("/documents/" + canSetClaim.getId())
      .then()
         .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void canGetContent() {
      JsonPath response = 
         given()
            .header("accept", "application/hal+json")
              .get("/documents/" + canGetClaim.getId())
          .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
               .jsonPath();
      
      Assert.assertNotNull(response.get("_links.content"));
      Assert.assertNotNull(response.get("_links.content.href"));

      String contentUrl = response.get("_links.content.href");
      given()
         .accept("plain/text")
      .when()
         .get(contentUrl)
      .then()
         .assertThat()
            .contentType(Matchers.startsWith("plain/text"))
            .body(Matchers.equalTo("This is plain text content!"));
    }

    @Test
    public void canDeleteContent() {
        JsonPath response = given()
                .header("accept", "application/hal+json")
                .get("/documents/" + canDelClaim.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath();
      
      Assert.assertNotNull(response.get("_links.document"));
      Assert.assertNotNull(response.get("_links.document.href"));

      String contentUrl = response.get("_links.document.href");
      when()
         .delete(contentUrl)
      .then()
         .assertThat()
            .statusCode(HttpStatus.SC_NO_CONTENT);

      // and make sure that it is really gone
      when()
         .get(contentUrl)
      .then()
         .assertThat()
            .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
