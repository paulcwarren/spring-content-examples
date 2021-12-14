package examples.converters;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import javax.persistence.GeneratedValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.content.s3.config.S3StoreConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import com.amazonaws.services.s3.model.S3ObjectId;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.s3.S3Config;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, S3Config.class, PlacementTest.ConverterConfig.class })
@EnableS3Stores("examples.converters")
public class PlacementTest {

	@Autowired
	private ConverterContentStore store;

	private ConverterEntity entity;
	private UUID id;

	@Autowired
	private S3Client s3;

	@Autowired
	private String bucketName;

	{
		Describe("Placement", () -> {
			Context("given a content entity", () -> {
				BeforeEach(() -> {
					entity = new ConverterEntity();
				});
				Context("given content is added", () -> {
					BeforeEach(() -> {
						store.setContent(entity, new ByteArrayInputStream("uuid".getBytes()));
						id = entity.getContentId();
					});
					It("should have the converted entity", () -> {
                        HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                                .bucket(bucketName)
                                .key(id(id))
                                .build();

                        try {
                            s3.headObject(objectRequest);
                        } catch (NoSuchKeyException e) {
                            fail(String.format("object %s does not exist", id.toString()));
                        }
					});
					Context("given the content is removed", () -> {
						BeforeEach(() -> {
							store.unsetContent(entity);
						});
						It("should remove the content from the store", () -> {
                            HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(id(id))
                                    .build();

                            try {
                                s3.headObject(objectRequest);
                                fail(String.format("object %s was not removed", id.toString()));
                            } catch (NoSuchKeyException e) {
                            }
						});
					});
				});
			});
			AfterEach(() -> {
				if (entity.getContentId() != null) {
					store.unsetContent(entity);
				}
			});
		});
	}

	@Test
	public void noop() {}

	public interface ConverterContentStore extends ContentStore<ConverterEntity, UUID> {}

	@Getter
	@Setter
	@NoArgsConstructor
	public class ConverterEntity {

		@javax.persistence.Id
		@GeneratedValue()
		private Long Id;

		@ContentId
		private UUID contentId;
	}

	@Configuration
	public static class ConverterConfig {

	    @Autowired
	    private String bucketName;

		@Bean
		public S3StoreConfigurer configurer() {
			return new S3StoreConfigurer() {
				@Override
				public void configureS3StoreConverters(ConverterRegistry registry) {
					registry.addConverter(new Converter<ConverterEntity, S3ObjectId>() {

						@Override
						public S3ObjectId convert(ConverterEntity source) {
							return new S3ObjectId(bucketName, id(source.getContentId()));
						}
					});
				}
			};
		}

	}

	public static String id(UUID id) {
		Assert.notNull(id, "id cannot be null");
		return "some-prefix/" + id.toString();
	}
}
