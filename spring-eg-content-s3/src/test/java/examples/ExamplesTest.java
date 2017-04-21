package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;


@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { S3Config.class })
public class ExamplesTest extends AbstractSpringContentTests {

    @Autowired
    private S3Config config;
    
    {
        Describe("Spring Content Amazon S3", () -> {
            Describe("Backwards Compatibility", () -> {
                Context("given content in the old placement location", () -> {
                    BeforeEach(() -> {
                        // write some content in the old placement location (store root)
                        // and create a entity so we can try and fetch it
                        String contentId = UUID.randomUUID().toString();
                        SimpleStorageResourceLoader storageResourceLoader = config.simpleStorageResourceLoader();

                        Resource resource = storageResourceLoader.getResource("s3://" + config.bucket() + "/" + contentId);
                        if (resource instanceof WritableResource) {
                            OutputStream out = ((WritableResource)resource).getOutputStream();
                            out.write("Hello Content World!".getBytes());
                            out.close();
                        }
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
        });
    }
}