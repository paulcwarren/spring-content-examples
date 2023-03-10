package examples.converters;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import jakarta.persistence.GeneratedValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.s3.S3ObjectId;
import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.content.s3.config.S3StoreConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.s3.S3Config;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, S3Config.class, EntityConverterTest.ConverterConfig.class })
@EnableS3Stores("examples.converters")
public class EntityConverterTest {

	@Autowired
	private ConverterContentStore store;

	private ConverterEntity entity;
	private UUID id;

	@Autowired
	private S3Client s3;

	private static String bucket = "another-spring-content-bucket";

	{
		Describe("S3ObjectId", () -> {
		    BeforeEach(() -> {
                HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                        .bucket(bucket)
                        .build();

                try {
                    s3.headBucket(headBucketRequest);
                } catch (NoSuchBucketException e) {

                    CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                            .bucket(bucket)
                            .build();
                    s3.createBucket(bucketRequest);
                }
		    });
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
		                        .bucket(bucket)
		                        .key(id.toString())
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
	                                .bucket(bucket)
	                                .key(id.toString())
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
				store.unsetContent(entity);
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

		@jakarta.persistence.Id
		@GeneratedValue()
		private Long Id;

		@ContentId
		private UUID contentId;
	}

	@Configuration
	public static class ConverterConfig {

		@Bean
		public S3StoreConfigurer configurer() {
			return new S3StoreConfigurer() {
				@Override
				public void configureS3StoreConverters(ConverterRegistry registry) {
					registry.addConverter(new Converter<ConverterEntity, S3ObjectId>() {

						@Override
						public S3ObjectId convert(ConverterEntity source) {
							if (source.getContentId() == null) {
								return null;
							}
							return new S3ObjectId(bucket, source.getContentId().toString());
						}
					});
				}
			};
		}

	}
}
