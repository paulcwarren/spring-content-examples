package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import examples.models.Claim;
import examples.models.ClaimForm;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.fs.config.FilesystemStoreConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.typesupport.UUIDBasedContentEntity;
import examples.typesupport.UUIDBasedContentEntityStore;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { EnableFilesystemStoresConfig.class })
public class ExamplesTest extends ContentStoreTests {

	@Autowired
	private File filesystemRoot;
	
	@Autowired
	private URIResourceStore store;
	
	@Autowired
	private DefaultConversionService filesystemStoreConverter;
	
	private Resource r;
	
	private Object contentEntity = null;
	
	private UUID id;
	
	@Autowired protected UUIDBasedContentEntityStore uuidStore;
	
	{
		Describe("Storage Model", () -> {
			Describe("Default content placement", () -> {
				BeforeEach(() -> {
					claim = new Claim();
					claim.setFirstName("John");
					claim.setLastName("Smith");
					claim.setClaimForm(new ClaimForm());
					claim.getClaimForm().setContentId("12345");
					claimFormStore.setContent(claim.getClaimForm(), new ByteArrayInputStream("Hello Content World!".getBytes()));
					claimRepo.save(claim);
				});
				It("should store content in the root of the Store", () -> {
					assertThat(new File(Paths.get(filesystemRoot.getAbsolutePath(), claim.getClaimForm().getContentId()).toAbsolutePath().toString()).exists(), is(true));
				});
			});
			Describe("Custom content placement", () -> {
				Context("given a converter that converts an entity to a resource path", () -> {
					BeforeEach(() -> {
						filesystemStoreConverter.addConverter(new EntityConverter());

						id = UUID.randomUUID();
						contentEntity = new UUIDBasedContentEntity();
						((UUIDBasedContentEntity)contentEntity).setContentId(id);
						uuidStore.setContent((UUIDBasedContentEntity)contentEntity, new ByteArrayInputStream("Hello Content World!".getBytes()));
					});
					AfterEach(() -> {
						filesystemStoreConverter.removeConvertible(UUIDBasedContentEntity.class, String.class);
					});
					It("should store content at that path", () -> {
						String[] segments = id.toString().split("-");

						assertThat(new File(Paths.get(filesystemRoot.getAbsolutePath(), segments).toAbsolutePath().toString()).exists(), is(true));
					});
				});
				Context("given a converter that converts a UUID id to a resource path", () -> {
					BeforeEach(() -> {
						filesystemStoreConverter.addConverter(new UUIDConverter());

						id = UUID.randomUUID();
						contentEntity = new UUIDBasedContentEntity();
						((UUIDBasedContentEntity)contentEntity).setContentId(id);
						uuidStore.setContent((UUIDBasedContentEntity)contentEntity, new ByteArrayInputStream("Hello Content World!".getBytes()));
					});
					AfterEach(() -> {
						filesystemStoreConverter.removeConvertible(UUID.class, String.class);
					});
					It("should store content at that path", () -> {
						String[] segments = id.toString().split("-");

						assertThat(new File(Paths.get(filesystemRoot.getAbsolutePath(), segments).toAbsolutePath().toString()).exists(), is(true));
					});
				});
			});
		});
	}

	public class EntityConverter implements FilesystemStoreConverter<UUIDBasedContentEntity,String> {
		@Override
		public String convert(UUIDBasedContentEntity source) {
			UUID id = source.getContentId();
			return String.format("/%s", id.toString().replaceAll("-","/"));
		}
	}

	public class UUIDConverter implements FilesystemStoreConverter<UUID,String> {
		@Override
		public String convert(UUID source) {
			return String.format("/%s", source.toString().replaceAll("-","/"));
		}
	}
}
