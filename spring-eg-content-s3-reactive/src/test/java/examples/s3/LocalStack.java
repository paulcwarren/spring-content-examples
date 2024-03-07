package examples.s3;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import org.apache.http.client.utils.URIBuilder;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;

public class LocalStack extends LocalStackContainer implements Serializable {

    private static final DockerImageName IMAGE_NAME = DockerImageName.parse("localstack/localstack");

    private LocalStack() {
        super(IMAGE_NAME);
        withServices(Service.S3);
        start();
    }

    private static class Singleton {
        private static final LocalStack INSTANCE = new LocalStack();
    }

    public static S3AsyncClient getAmazonS3Client() throws URISyntaxException {

        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();

        S3Configuration serviceConfiguration = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .pathStyleAccessEnabled(true)
                .build();

        return S3AsyncClient.builder()
                .httpClient(httpClient)
                .region(Region.US_WEST_1)
                .endpointOverride(new URI(Singleton.INSTANCE.getEndpointConfiguration(LocalStackContainer.Service.S3).getServiceEndpoint()))
                .credentialsProvider(new LocalStack.CrossAwsCredentialsProvider(Singleton.INSTANCE.getDefaultCredentialsProvider()))
                .serviceConfiguration(serviceConfiguration)
                .build();
    }

    @Override
    public URI getEndpointOverride(EnabledService service) {
        try {
            // super method converts localhost to 127.0.0.1 which fails on macos
            // need to revert it back to whatever getContainerIpAddress() returns
            return new URIBuilder(super.getEndpointOverride(service)).setHost(getContainerIpAddress()).build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot obtain endpoint URL", e);
        }
    }

    @SuppressWarnings("unused") // Serializable safe singleton usage
    protected LocalStack readResolve() {
        return Singleton.INSTANCE;
    }

    private static class CrossAwsCredentialsProvider implements AwsCredentialsProvider {
        private final AWSCredentials credentials;

        public CrossAwsCredentialsProvider(AWSCredentialsProvider provider) {
          this.credentials = provider.getCredentials();
        }

        @Override
        public AwsCredentials resolveCredentials() {
          return AwsBasicCredentials.create(credentials.getAWSAccessKeyId(), credentials.getAWSSecretKey());
        }
    }
}
