package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { ExamplesConfig.class })
public class ExamplesTest {

	@Autowired private RestTemplate template;
	
	{
		Describe("Elasticsearch", () -> {
			It("should have a template", () -> {
				assertThat(template, is(not(nullValue())));
			});
			
//			FIt("should do what?", () -> {
//				HttpHeaders headers = new HttpHeaders();
//				headers.setContentType(MediaType.APPLICATION_JSON);
//				HttpEntity<String> entity = new HttpEntity<String>("", headers);
//
//				try {
//					template.exchange("http://somehting/that/doesnt/exist/", HttpMethod.GET, entity, String.class);
//					assertTrue(false);
//				} catch (RestClientException rae) {
//					rae.printStackTrace();
//					assertTrue(true);
//				}
//			});
			
			Context("given an index", () -> {
				BeforeEach(() -> {
					JSONObject request = new JSONObject();
//					request.put("settings", new JSONObject().put("index", new JSONObject().put("number_of_shards", 3).put("number_of_replicas", 2)));
					request.put("mappings", 
							new JSONObject().put("doc", 
								new JSONObject().put("properties", 
									new JSONObject()
										.put("original-content", new JSONObject().put("type", "attachment"))
										.put("id", new JSONObject().put("type", "text"))
									)));
					
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

					ResponseEntity<String> response = template.exchange("http://search-spring-content-cc4bqyhqoiokxrakhfp4s2y3tm.us-east-1.es.amazonaws.com/docs", HttpMethod.PUT, entity, String.class);
					System.out.println(response.getBody());  
					assertThat(response.getStatusCode(), is(HttpStatus.OK));
				});
				AfterEach(() -> {
					ResponseEntity<String> response = template.exchange("http://search-spring-content-cc4bqyhqoiokxrakhfp4s2y3tm.us-east-1.es.amazonaws.com/docs", HttpMethod.DELETE, null, String.class);
//					assertThat(response.getStatusCode(), is(HttpStatus.OK));
				});
				
				Context("given a PDF", () -> {
					BeforeEach(() -> {
						JSONObject request = new JSONObject();
						request.put("original-content", "UWJveCBtYWtlcyBpdCBlYXN5IGZvciB1cyB0byBwcm92aXNpb24gYW4gRWxhc3RpY3NlYXJjaCBjbHVzdGVyIHdpdGhvdXQgd2FzdGluZyB0aW1lIG9uIGFsbCB0aGUgZGV0YWlscyBvZiBjbHVzdGVyIGNvbmZpZ3VyYXRpb24u")
								.put("id", "12345");
						
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);
						HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

						ResponseEntity<String> response = template
						  .exchange("http://search-spring-content-cc4bqyhqoiokxrakhfp4s2y3tm.us-east-1.es.amazonaws.com/docs/doc/1", HttpMethod.PUT, entity, String.class);
						  
						assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
					});
					Context("given the index has had time to index the PDF", () -> {
						BeforeEach(() -> {
							Thread.sleep(500);
						});
						It("should find the document when searched", () -> {
							JSONObject request = new JSONObject();
//							request.put("query", new JSONObject()
//									.put("query_string", new JSONObject()
//											.put("query", "QBOX")));

//							request.put("query", new JSONObject()
//									.put("match_phrase", new JSONObject()
//											.put("original-content", new JSONObject()
//												.put("query", "provision cluster")
//												.put("slop", "10"))));
							
//							request.put("query", new JSONObject()
//										.put("match_phrase", new JSONObject()
//												.put("original-content", new JSONObject()
//													.put("query", "provision cluster"))));

//							request.put("query", new JSONObject()
//									.put("match_phrase", new JSONObject()
//										.put("query", "provision cluster")
//										.put("slop", 10)))
							
							request.put("query", new JSONObject()
									.put("query_string", new JSONObject()
											.put("query", "\"provision cluster\"~2")));

							HttpHeaders headers = new HttpHeaders();
							headers.setContentType(MediaType.APPLICATION_JSON);
							HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
							
							ResponseEntity<String> response = template
									.exchange("http://search-spring-content-cc4bqyhqoiokxrakhfp4s2y3tm.us-east-1.es.amazonaws.com/docs/doc/_search", HttpMethod.POST, entity, String.class);
							
							assertThat(response.getStatusCode(), is(HttpStatus.OK));
							assertThat(response.getBody().contains("\"id\":\"12345\""), is(true));
						});
					});
				});
			});
		}); 
	}
	
	@Test
	public void noop() {}
}
