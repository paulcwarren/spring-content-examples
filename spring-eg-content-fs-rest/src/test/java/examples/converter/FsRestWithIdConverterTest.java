package examples.converter;


import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import examples.models.Document;
import examples.repositories.DocumentRepository;
import examples.rest.Application;
import examples.stores.DocumentContentStore;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.content.fs.config.FilesystemStoreConfigurer;
import org.springframework.content.fs.config.FilesystemStoreConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(classes = {Application.class, FsRestWithIdConverterTest.IdConverterConfig.class}, webEnvironment=WebEnvironment.RANDOM_PORT)
public class FsRestWithIdConverterTest {

	@Autowired
	private DocumentRepository repo;
	
	@Autowired
	private DocumentContentStore store;

	@Autowired
	private WebApplicationContext webApplicationContext;

    private Document document;
    
    {
    	Describe("Spring Content REST", () -> {
    		BeforeEach(() -> {
				RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    		});
    		Context("given a document", () -> {
    			BeforeEach(() -> {
    		    	document = new Document();
    		    	repo.save(document);
    			});
    			It("should be POSTable with new content with 201 Created", () -> {
    				// assert content does not exist
					when()
						.get("/documents/" + document.getId())
					.then()
						.assertThat()
						.statusCode(HttpStatus.SC_NOT_FOUND);

					String newContent = "This is some new content";
					
					// POST the new content
					given()
    					.contentType("plain/text")
    					.body(newContent.getBytes())
					.when()
    					.post("/documents/" + document.getId())
					.then()
    					.statusCode(HttpStatus.SC_CREATED);
					
					// assert that it now exists
					given()
    					.header("accept", "plain/text")
    					.get("/documents/" + document.getId())
					.then()
    					.statusCode(HttpStatus.SC_OK)
    					.assertThat()
    					.contentType(Matchers.startsWith("plain/text"))
    					.body(Matchers.equalTo(newContent));
    			});
    			Context("given that the document has existing content", () -> {
        			BeforeEach(() -> {
        		    	document.setMimeType("plain/text");
        		    	document = store.setContent(document, new ByteArrayInputStream("This is plain text content!".getBytes()));
        		    	repo.save(document);
        			});
    				It("should return the content with 200 OK", () -> {
    					given()
	    					.header("accept", "plain/text")
	    					.get("/documents/" + document.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo("This is plain text content!"));
    				});
    				It("should be POSTable with new content with 200 Created", () -> {
    					String newContent = "This is new content";
    					
    					given()
	    					.contentType("plain/text")
	    					.body(newContent.getBytes())
    					.when()
	    					.post("/documents/" + document.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK);
    					
    					given()
	    					.header("accept", "plain/text")
	    					.get("/documents/" + document.getId())
    					.then()
	    					.statusCode(HttpStatus.SC_OK)
	    					.assertThat()
	    					.contentType(Matchers.startsWith("plain/text"))
	    					.body(Matchers.equalTo(newContent));
    				});
    				It("should be DELETEable with 204 No Content", () -> {
    					given()
    						.delete("/documents/" + document.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NO_CONTENT);
    					
    					// and make sure that it is really gone
    					when()
    						.get("/documents/" + document.getId())
    					.then()
	    					.assertThat()
	    					.statusCode(HttpStatus.SC_NOT_FOUND);
    				});
    			});
			});
    	});
    }
    
    @Configuration
    public static class IdConverterConfig {
    	
    	public Converter<String,String> converter() {
    		return new FilesystemStoreConverter<String,String>() {

				@Override
				public String convert(String source) {
					return String.format("/%s", source.replaceAll("-", "/"));
				}
    		};
    	}
    	
    	@Bean
    	public FilesystemStoreConfigurer configurer() {
    		return new FilesystemStoreConfigurer() {

    			@Override
    			public void configureFilesystemStoreConverters(ConverterRegistry registry) {
    				registry.addConverter(converter());
    			}
    		};
    	}
    }
    
    @Test
    public void noop() {}
}
