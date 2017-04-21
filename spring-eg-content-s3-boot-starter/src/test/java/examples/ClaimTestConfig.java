package examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.content.s3.config.AbstractS3ContentRepositoryConfiguration;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;

@Configuration
public class ClaimTestConfig extends AbstractS3ContentRepositoryConfiguration {

	@Autowired
	private AmazonS3 client;

    public Region region() {
        return Region.getRegion(Regions.fromName(System.getenv("AWS_REGION")));
    }

    @Override
	public SimpleStorageResourceLoader simpleStorageResourceLoader() {
		client.setRegion(region());
		return new SimpleStorageResourceLoader(client);
	}
}
