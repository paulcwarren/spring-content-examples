package examples.azure;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.content.azure.config.EnableAzureStorage;
import org.springframework.test.context.ContextConfiguration;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import tests.smoke.ContentStoreTests;
import tests.smoke.JpaConfig;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { JpaConfig.class, AzureConfig.class})
@EnableAzureStorage("examples.stores")
public class AzureExamplesTest extends ContentStoreTests {

    @Autowired
    private BlobServiceClientBuilder client;

    @Value("#{environment.AZURE_STORAGE_BUCKET}")
    private String bucket;

    {
        AfterEach(() -> {
            PagedIterable<BlobItem> blobs = client.buildClient().getBlobContainerClient(bucket).listBlobs();
            for(BlobItem blob : blobs) {
                client.buildClient().getBlobContainerClient(bucket).getBlobClient(blob.getName()).delete();
            }
        });
    }
}