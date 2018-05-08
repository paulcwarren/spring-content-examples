package examples.typesupport;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.UUID;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TypeSupportTests {
	
	@Autowired protected UUIDBasedContentEntityStore uuidStore;
	@Autowired protected URIBasedContentEntityStore uriStore;
	@Autowired protected LongBasedContentEntityStore longStore;
	@Autowired protected BigIntegerBasedContentEntityStore bigIntStore;

	Object entity;
    Object id;
	
    {
		Describe("java.util.UUID", () -> {
			Context("given a content entity", () -> {
				BeforeEach(() -> {
					entity = new UUIDBasedContentEntity();
				});
				Context("given the Application sets the ID", () -> {
					BeforeEach(() -> {
						id = UUID.randomUUID();
						((UUIDBasedContentEntity)entity).setContentId((UUID)id);

						uuidStore.setContent((UUIDBasedContentEntity)entity, new ByteArrayInputStream("uuid".getBytes()));
					});
					It("should store the content successfully", () -> {
						Assert.assertThat(IOUtils.contentEquals(uuidStore.getContent((UUIDBasedContentEntity)entity), IOUtils.toInputStream("uuid")), is(true));
					});
				});
				Context("given Spring Content generates the ID", () -> {
					BeforeEach(() -> {
						uuidStore.setContent((UUIDBasedContentEntity)entity, new ByteArrayInputStream("uuid".getBytes()));
					});
					It("should store the content successfully", () -> {
						Assert.assertThat(IOUtils.contentEquals(uuidStore.getContent((UUIDBasedContentEntity)entity), IOUtils.toInputStream("uuid")), is(true));
					});
				});
			});
			AfterEach(() -> {
				uuidStore.unsetContent((UUIDBasedContentEntity)entity);
				Assert.assertThat(((UUIDBasedContentEntity) entity).getContentId(), is(nullValue()));
			});
		});
		Describe("java.net.URI", () -> {
			Context("given a content entity", () -> {
				BeforeEach(() -> {
					entity = new URIBasedContentEntity();
				});
				Context("given the Application sets the ID", () -> {
					BeforeEach(() -> {
						id = new URI("/some/deep/location");
						((URIBasedContentEntity)entity).setContentId((URI)id);

						uriStore.setContent((URIBasedContentEntity)entity, new ByteArrayInputStream("uri".getBytes()));
					});
					It("should store the content successfully", () -> {
						Assert.assertThat(IOUtils.contentEquals(uriStore.getContent((URIBasedContentEntity)entity), IOUtils.toInputStream("uri")), is(true));
					});
				});
			});
			AfterEach(() -> {
				uriStore.unsetContent((URIBasedContentEntity)entity);
				Assert.assertThat(((URIBasedContentEntity) entity).getContentId(), is(nullValue()));
			});
		});
		Describe("java.lang.Long", () -> {
			Context("given a content entity", () -> {
				BeforeEach(() -> {
					entity = new LongBasedContentEntity();
				});
				Context("given the Application sets the ID", () -> {
					BeforeEach(() -> {
						id = Long.MAX_VALUE;
						((LongBasedContentEntity)entity).setContentId((Long)id);

						longStore.setContent((LongBasedContentEntity)entity, new ByteArrayInputStream("long".getBytes()));
					});
					It("should store the content successfully", () -> {
						Assert.assertThat(IOUtils.contentEquals(longStore.getContent((LongBasedContentEntity)entity), IOUtils.toInputStream("long")), is(true));
					});
				});
			});
			AfterEach(() -> {
				longStore.unsetContent((LongBasedContentEntity)entity);
				Assert.assertThat(((LongBasedContentEntity) entity).getContentId(), is(nullValue()));
			});
		});
		Describe("java.math.BigInteger", () -> {
			Context("given a content entity", () -> {
				BeforeEach(() -> {
					entity = new BigIntegerBasedContentEntity();
				});
				Context("given the Application sets the ID", () -> {
					BeforeEach(() -> {
						id = BigInteger.valueOf(Long.MAX_VALUE);
						((BigIntegerBasedContentEntity)entity).setContentId((BigInteger)id);

						bigIntStore.setContent((BigIntegerBasedContentEntity)entity, new ByteArrayInputStream("big-int".getBytes()));
					});
					It("should store the content successfully", () -> {
						Assert.assertThat(IOUtils.contentEquals(bigIntStore.getContent((BigIntegerBasedContentEntity)entity), IOUtils.toInputStream("big-int")), is(true));
					});
				});
			});
			AfterEach(() -> {
				bigIntStore.unsetContent((BigIntegerBasedContentEntity)entity);
				Assert.assertThat(((BigIntegerBasedContentEntity) entity).getContentId(), is(nullValue()));
			});
		});
    }

    
	@Test
	public void noop() throws IOException {
	}
}
