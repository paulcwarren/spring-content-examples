package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.JustBeforeEach;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import internal.org.springframework.content.fs.config.FilesystemProperties;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { ClaimTestConfig.class })
public class ClaimTest extends AbstractSpringContentTests {

	@Autowired
	private FilesystemProperties props;
	
	@Autowired
	private URIResourceStore store;
	
	private Resource r;
	
	{
		Describe("Spring Content Filesystem", () -> {
			Describe("Backwards Compatibility", () -> {
				Context("given content in the old placement location", () -> {
					BeforeEach(() -> {
						// write some content in the old placement location (store root) 
						// and create a entity so we can try and fetch it
						String contentId = UUID.randomUUID().toString();
						FileOutputStream out = new FileOutputStream(Paths.get(props.getFilesystemRoot(), contentId).toAbsolutePath().toString());
						out.write("Hello Content World!".getBytes());
						out.close();

						ClaimForm claimForm = new ClaimForm();
						claimForm.setContentId(contentId);
						claimForm.setContentLength(20L);
						claimForm.setMimeType("text/plain");

						claim = new Claim();
						claim.setFirstName("John");
						claim.setLastName("Smith");
						claim.setClaimForm(claimForm);
						claimRepo.save(claim);
					});
					It("getContent will find it", () -> {
						assertThat(IOUtils.contentEquals(claimFormStore.getContent(claim.getClaimForm()), IOUtils.toInputStream("Hello Content World!", Charset.defaultCharset())), is(true));
					});
				});
			});
			
			Describe("Store Interactions", () -> {
				Context("given a uri-based resource store", () -> {
					Context("given an existing resource", () -> {
						BeforeEach(() -> {
							// write some content in the old placement location (store root) 
							// and create a entity so we can try and fetch it
							File file = new File(Paths.get(props.getFilesystemRoot(), "some", "thing").toAbsolutePath().toString());
							file.getParentFile().mkdirs();
							FileOutputStream out = new FileOutputStream(file);
							out.write("Hello Spring Content World!".getBytes());
							out.close();
						});
						JustBeforeEach(() -> {
							r = store.getResource(new URI("/some/thing"));
						});
						It("something", () -> {
							assertThat(IOUtils.contentEquals(r.getInputStream(), IOUtils.toInputStream("Hello Spring Content World!", Charset.defaultCharset())), is(true));
						});
					});
				});
			});
		}); 
	}
	
}
