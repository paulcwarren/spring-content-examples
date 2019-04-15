package examples.cmis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import support.cmis.Application;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
//@Ginkgo4jConfiguration(threads=1)
@SpringBootTest(classes = Application.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CmisTest {

	static {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(false);
	}

	@LocalServerPort
	private int port;

	private Session s;
	private Folder root, folder;
	private Document doc, pwc;

	private String mimetype = "text/plain; charset=UTF-8";
	private String content = "This is some test content.";
	private String filename = "some-file.txt";

	{
		Describe("CMIS Tests", () -> {
			BeforeEach(() -> {
				SessionFactory f = SessionFactoryImpl.newInstance();
				Map<String, String> parameter = new HashMap<String, String>();

				parameter.put(SessionParameter.BROWSER_URL, "http://localhost:" + port + "/browser");
				parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
				parameter.put(SessionParameter.REPOSITORY_ID, "1");

				parameter.put(SessionParameter.USER, "test");
				parameter.put(SessionParameter.PASSWORD, "test");

				parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
				parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en");

				s = f.createSession(parameter);
				assertThat(s, is(not(nullValue())));
			});

			Context("given a folder is created in the root", () -> {
				BeforeEach(() -> {
					root = s.getRootFolder();

					Map<String, Object> properties = new HashMap<String, Object>();
					properties.put(PropertyIds.NAME, "folder1");
					properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");

					folder = root.createFolder(properties);
					assertThat(folder, is(not(nullValue())));
				});

				Context("given a document is created", () -> {

					BeforeEach(() -> {
						Map<String, Object> properties = new HashMap<String, Object>();
						properties.put(PropertyIds.NAME, "doc1");
						properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

						doc = root.createDocument(properties, new ContentStreamImpl("test", "text/plain", ""), VersioningState.NONE);
					});

					Context("given the document has content", () -> {

						BeforeEach(() -> {
							mimetype = "text/plain; charset=UTF-8";
							content = "This is some test content.";
							filename = "some-file.txt";

							byte[] buf = content.getBytes("UTF-8");
							ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes("UTF-8"));

							ContentStream contentStream = s.getObjectFactory().createContentStream(filename, buf.length, mimetype, input);
							doc.setContentStream(contentStream, false);
						});

						It("should be retrievable", () -> {
							ContentStream contentStream = doc.getContentStream();
							assertThat(contentStream, is(not(nullValue())));

							InputStream actualStream = contentStream.getStream();
							InputStream expectedStream = new ByteArrayInputStream(content.getBytes());
							IOUtils.contentEquals(expectedStream, actualStream);
							IOUtils.closeQuietly(expectedStream, actualStream);
						});

						It("should be update-able", () -> {
							content = "This is some updated test content.";

							byte[] buf = content.getBytes("UTF-8");
							ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes("UTF-8"));

							ContentStream contentStream = s.getObjectFactory().createContentStream(filename, buf.length, mimetype, input);
							doc.setContentStream(contentStream, false);

							contentStream = doc.getContentStream();
							assertThat(contentStream, is(not(nullValue())));
							InputStream actualStream = contentStream.getStream();
							InputStream expectedStream = new ByteArrayInputStream(content.getBytes());
							IOUtils.contentEquals(expectedStream, actualStream);
							IOUtils.closeQuietly(expectedStream, actualStream);
						});

						Context("given a private working copy is created", () -> {

							BeforeEach(() -> {
								ObjectId id = doc.checkOut();
								doc = (Document) s.getObject(id);
								assertThat(doc.isVersionSeriesCheckedOut(), is(true));
								pwc = (Document) s.getObject(doc.getVersionSeriesCheckedOutId());
							});

							It("should have a new id and be flagged as a working copy", () -> {
								assertThat(doc.getId(), is(not(pwc.getId())));
								assertThat(pwc.isPrivateWorkingCopy(), is(true));
								assertThat(pwc.isVersionSeriesCheckedOut(), is(true));
								assertThat(doc.isVersionSeriesCheckedOut(), is(true));
								assertThat(pwc.getVersionSeriesCheckedOutBy(), is("test"));
							});

							Context("given the working copy is updated and checked in", () -> {

								BeforeEach(() -> {
									Map<String, Object> properties = new HashMap<>();
									properties.put(PropertyIds.DESCRIPTION, "a description");

									ObjectId id = pwc.checkIn(true,
										properties,
										s.getObjectFactory().createContentStream(filename, 0, mimetype, new ByteArrayInputStream("".getBytes())),
										"a check-in comment");

									doc = (Document) s.getObject(id);
								});

								It("should promote the working copy to the latest", () -> {
									assertThat(doc.getId(), is(pwc.getId()));
									assertThat(doc.getPropertyValue("cmis:description"), is("a description"));
									assertThat(doc.isLatestVersion(), is(true));
									assertThat(doc.isLatestMajorVersion(), is(true));
									assertThat(doc.isVersionSeriesCheckedOut(), is(false));
									assertThat(doc.getCheckinComment(), is("a check-in comment"));
								});
							});
						});
					});
				});
			});
		});
	}

	@Test
	public void noop(){}
}
