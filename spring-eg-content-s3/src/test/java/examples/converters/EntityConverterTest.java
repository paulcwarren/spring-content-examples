package examples.converters;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import examples.s3.S3Config;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.content.s3.config.S3ObjectIdResolvers;
import org.springframework.content.s3.config.S3StoreConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.test.context.ContextConfiguration;
import tests.smoke.JpaConfig;

import javax.persistence.GeneratedValue;
import java.io.ByteArrayInputStream;
import java.util.UUID;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
	private AmazonS3 s3;

	private static String bucket = "another-spring-content-bucket";

	{
		Describe("S3ObjectId", () -> {
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
						assertThat(s3.doesObjectExist(bucket, id.toString()), is(true));
					});
					Context("given the content is removed", () -> {
						BeforeEach(() -> {
							store.unsetContent(entity);
						});
						It("should remove the content from the store", () -> {
							assertThat(s3.doesObjectExist(bucket, id.toString()), is(false));
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

		@javax.persistence.Id
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

				@Override
				public void configureS3ObjectIdResolvers(S3ObjectIdResolvers resolvers) {
					//
				}
			};
		}

	}
}
