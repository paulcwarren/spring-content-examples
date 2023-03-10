package examples.azure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class AzureConfig {

    private static final BlobServiceClientBuilder builder = Azurite.getBlobServiceClientBuilder();
    private static final BlobContainerClient client = builder.buildClient().getBlobContainerClient("test");

    static {
        System.setProperty("spring.content.azure.bucket", "azure-test-bucket");

        if (!client.exists()) {
            client.create();
        }
    }

    @Value("#{environment.AZURE_STORAGE_ENDPOINT}")
    private String endpoint;

    @Value("#{environment.AZURE_STORAGE_CONNECTION_STRING}")
    private String connString;

    @Bean
    public BlobServiceClientBuilder blobServiceClientBuilder() {
        return builder;
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
