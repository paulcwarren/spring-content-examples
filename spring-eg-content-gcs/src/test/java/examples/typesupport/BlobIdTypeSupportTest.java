// package examples.typesupport;
//
// import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
//import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
//import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
//import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
//import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.nullValue;
//
//import java.io.ByteArrayInputStream;
//import java.util.UUID;
//
//import org.apache.commons.io.IOUtils;
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.content.gcs.config.EnableGCPStorage;
//import org.springframework.test.context.ContextConfiguration;
//
//import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
//import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
//import com.google.cloud.storage.BlobId;
//
//import examples.s3.GCSConfig;
//import tests.smoke.JpaConfig;
//
//@RunWith(Ginkgo4jSpringRunner.class)
//@Ginkgo4jConfiguration(threads=1)
//@ContextConfiguration(classes = { JpaConfig.class, GCSConfig.class })
//@EnableGCPStorage("examples.typesupport")
//public class BlobIdTypeSupportTest extends TypeSupportTests {
//
//     @Autowired
//     protected BlobIdBasedContentEntityStore s3ContentIdStore;
//
//     @Value("#{environment.GCP_STORAGE_BUCKET}")
//     private String bucketName;
//
//     Object entity;
//     BlobId id;
//
//     {
//         Describe("BlobId", () -> {
//             Context("given a content entity", () -> {
//                 BeforeEach(() -> {
//                     entity = new BlobIdBasedContentEntity();
//                 });
//                 Context("given the Application sets the ID", () -> {
//                     BeforeEach(() -> {
//                         id = BlobId.of(bucketName, UUID.randomUUID().toString());
//                         ((BlobIdBasedContentEntity) entity).setContentId(id);
//
//                         s3ContentIdStore.setContent((BlobIdBasedContentEntity) entity, new ByteArrayInputStream("uuid".getBytes()));
//                     });
//                     It("should store the content successfully", () -> {
//                         Assert.assertThat(IOUtils.contentEquals(s3ContentIdStore.getContent((BlobIdBasedContentEntity) entity), IOUtils.toInputStream("uuid")), is(true));
//                     });
//                 });
//             });
//             AfterEach(() -> {
//                 s3ContentIdStore.unsetContent((BlobIdBasedContentEntity) entity);
//                 Assert.assertThat(((BlobIdBasedContentEntity) entity).getContentId(), is(nullValue()));
//             });
//         });
//     }
//}
