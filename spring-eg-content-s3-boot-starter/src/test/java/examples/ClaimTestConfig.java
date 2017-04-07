package examples;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;

import internal.org.springframework.content.commons.placement.UUIDPlacementStrategy;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.content.commons.placement.PlacementStrategy;
import org.springframework.content.s3.config.AbstractS3ContentRepositoryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaimTestConfig extends AbstractS3ContentRepositoryConfiguration {

	@Autowired
	private AmazonS3 client;

    @Override
    public String bucket() {
        return System.getenv("AWS_BUCKET");
    }

    @Override
    public Region region() {
        return Region.getRegion(Regions.fromName(System.getenv("AWS_REGION")));
    }

    @Override
	public SimpleStorageResourceLoader simpleStorageResourceLoader() {
		client.setRegion(region());
		return new SimpleStorageResourceLoader(client);
	}

	@Bean
	public PlacementStrategy<String> placementStrategy() {
		return new PlacementStrategy<String>() {

			@Override
			public String getLocation(String contentId) {
				return new UUIDPlacementStrategy().getLocation(UUID.fromString(contentId));
			}
		};
	}
}
