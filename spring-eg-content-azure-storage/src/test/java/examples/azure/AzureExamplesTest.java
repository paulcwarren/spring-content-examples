package examples.azure;

import com.azure.storage.blob.BlobServiceClientBuilder;
import net.bytebuddy.utility.RandomString;
import org.junit.runner.RunWith;
import org.springframework.content.azure.config.EnableAzureStorage;
import org.springframework.test.context.ContextConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import tests.smoke.ContentStoreTests;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, AzureConfig.class})
@EnableAzureStorage("examples.stores")
public class AzureExamplesTest extends ContentStoreTests {

//    private static final String AZURITE_IMAGE = "mcr.microsoft.com/azure-storage/azurite:3.29.0";
//    private static final GenericContainer<?> AZURITE_CONTAINER = new GenericContainer<>(AZURITE_IMAGE)
//            .withCommand("azurite-blob", "--blobHost", "0.0.0.0")
//            .withExposedPorts(10000);
//
//    private static final String DEFAULT_AZURITE_CONNECTION_STRING = "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:%s/devstoreaccount1;";
//    private static final String CONTAINER_NAME = RandomString.make().toLowerCase();
//    private static final String CONTAINER_CONNECTION_STRING;
//
//    static {
//        AZURITE_CONTAINER.start();
//
//        var blobPort = AZURITE_CONTAINER.getMappedPort(10000);
//        CONTAINER_CONNECTION_STRING = String.format(DEFAULT_AZURITE_CONNECTION_STRING, blobPort);
//
//        var blobServiceClient = new BlobServiceClientBuilder().connectionString(CONTAINER_CONNECTION_STRING).buildClient();
//        blobServiceClient.createBlobContainer(CONTAINER_NAME);
//    }
//
//    @DynamicPropertySource
//    static void properties(DynamicPropertyRegistry registry) {
//        registry.add("de.rieckpil.azure.blob-storage.container-name", () -> CONTAINER_NAME);
//        registry.add("de.rieckpil.azure.blob-storage.connection-string", () -> CONTAINER_CONNECTION_STRING);
//    }
}