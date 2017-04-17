package examples;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.JustBeforeEach;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import examples.typesupport.UUIDBasedContentEntity;
import examples.typesupport.UUIDBasedContentEntityStore;
import internal.org.springframework.content.commons.placement.UUIDPlacementStrategy;
import internal.org.springframework.content.fs.config.FilesystemProperties;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { ClaimTestConfig.class })
public class ClaimTest extends AbstractSpringContentTests {

	@Autowired
	private FilesystemProperties props;
	
	@Autowired
	private URIResourceStore store;
	
	@Autowired
	private DefaultConversionService converter;
	
	@Autowired
	private UUIDBasedContentEntityStore uuidBasedStore;
	
	private Resource r;
	
	private Object contentEntity = null;
	
	private UUID id;
	
	{
		Describe("Spring Content Filesystem", () -> {
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
						assertThat(new File(Paths.get(props.getFilesystemRoot(), claim.getClaimForm().getContentId()).toAbsolutePath().toString()).exists(), is(true));
					});
				});
				Describe("Custom content placement", () -> {
					Context("given a converter that converts a UUID id to a resource path", () -> {
						BeforeEach(() -> {
							converter.addConverter(new UUIDPlacementStrategy());

							id = UUID.randomUUID();
							contentEntity = new UUIDBasedContentEntity();
							((UUIDBasedContentEntity)contentEntity).setContentId(id);
							uuidBasedStore.setContent((UUIDBasedContentEntity)contentEntity, new ByteArrayInputStream("Hello Content World!".getBytes()));
						});
						AfterEach(() -> {
							converter.removeConvertible(UUID.class, String.class);
						});
						It("should store content at that path", () -> {
							String[] segments = id.toString().split("-");
							
							assertThat(new File(Paths.get(props.getFilesystemRoot(), segments).toAbsolutePath().toString()).exists(), is(true));
						});
					});
				});
			});
			
			Describe("Experimental Store API", () -> {
				Describe("Store", () -> {
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
							It("should be able to get that resource", () -> {
								assertThat(IOUtils.contentEquals(r.getInputStream(), IOUtils.toInputStream("Hello Spring Content World!", Charset.defaultCharset())), is(true));
							});
						});
					});
				});
				Describe("AssociativeStore", () -> {
					Context("given an entity", () -> {
						BeforeEach(() -> {
							claim = new Claim();
						});
						Context("given a resource", () -> {
							BeforeEach(() -> {
								// write some content in the old placement location (store root) 
								// and create a entity so we can try and fetch it
								File file = new File(Paths.get(props.getFilesystemRoot(), "some", "other", "thing").toAbsolutePath().toString());
								file.getParentFile().mkdirs();
								FileOutputStream out = new FileOutputStream(file);
								out.write("Hello Spring Content World!".getBytes());
								out.close();
							});
							It("should be possible to associate the entity and the resource", () -> {
								r = store.getResource(new URI("/some/other/thing"));
//								store.associate(claim, r);
							});
						});
					});
				});
			});
		}); 
	}
	
}
