 package examples.typesupport;

 import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.azure.config.BlobId;
import org.springframework.content.azure.config.EnableAzureStorage;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.azure.AzureConfig;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, AzureConfig.class })
@EnableAzureStorage("examples.typesupport")
public class BlobIdTypeSupportTest extends TypeSupportTests {

     @Autowired
     protected BlobIdBasedContentEntityStore contentIdStore;

     Object entity;
     BlobId id;

     {
         Describe("BlobId", () -> {
             Context("given a content entity", () -> {
                 BeforeEach(() -> {
                     entity = new BlobIdBasedContentEntity();
                 });
                 Context("given the Application sets the ID", () -> {
                     BeforeEach(() -> {
                         String bucketName = System.getProperty("spring.content.azure.bucket");
                         id = new BlobId(bucketName, UUID.randomUUID().toString());
                         ((BlobIdBasedContentEntity) entity).setContentId(id);

                         contentIdStore.setContent((BlobIdBasedContentEntity) entity, new ByteArrayInputStream("uuid".getBytes()));
                     });
                     It("should store the content successfully", () -> {
                         Assert.assertThat(IOUtils.contentEquals(contentIdStore.getContent((BlobIdBasedContentEntity) entity), IOUtils.toInputStream("uuid")), is(true));
                     });
                 });
             });
         });
     }
}
