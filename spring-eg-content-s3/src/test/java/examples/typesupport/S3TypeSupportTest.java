 package examples.typesupport;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.s3.S3ContentId;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.S3Config;
import examples.config.JpaConfig;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, S3Config.class })
public class S3TypeSupportTest extends TypeSupportTests {

     @Autowired
     protected S3ContentIdBasedContentEntityStore s3ContentIdStore;

     Object entity;
     S3ContentId id;

     {
         Describe("S3ContentId", () -> {
             Context("given a content entity", () -> {
                 BeforeEach(() -> {
                     entity = new S3ContentIdBasedContentEntity();
                 });
                 Context("given the Application sets the ID", () -> {
                     BeforeEach(() -> {
                         id = new S3ContentId("spring-eg-content-s3", UUID.randomUUID().toString());
                         ((S3ContentIdBasedContentEntity) entity).setContentId(id);

                         s3ContentIdStore.setContent((S3ContentIdBasedContentEntity) entity, new ByteArrayInputStream("uuid".getBytes()));
                     });
                     It("should store the content successfully", () -> {
                         Assert.assertThat(IOUtils.contentEquals(s3ContentIdStore.getContent((S3ContentIdBasedContentEntity) entity), IOUtils.toInputStream("uuid")), is(true));
                     });
                 });
             });
             AfterEach(() -> {
                 s3ContentIdStore.unsetContent((S3ContentIdBasedContentEntity) entity);
                 Assert.assertThat(s3ContentIdStore.getContent((S3ContentIdBasedContentEntity) entity), is(nullValue()));
             });
         });
     }
}
