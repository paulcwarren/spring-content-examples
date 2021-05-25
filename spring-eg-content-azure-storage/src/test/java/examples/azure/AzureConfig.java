package examples.azure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Configuration
public class AzureConfig {

    private static final BlobServiceClientBuilder builder = Azurite.getBlobServiceClientBuilder();
    private static final BlobContainerClient client = builder.buildClient().getBlobContainerClient("test");

    static {
        if (!client.exists()) {
            client.create();
        }

        System.setProperty("spring.content.azure.bucket", "azure-test-bucket");
    }

    @Value("#{environment.AZURE_STORAGE_ENDPOINT}")
    private String endpoint;

    @Value("#{environment.AZURE_STORAGE_CONNECTION_STRING}")
    private String connString;

    @Bean
    public BlobServiceClientBuilder blobServiceClientBuilder() {
        return builder;
    }
}
