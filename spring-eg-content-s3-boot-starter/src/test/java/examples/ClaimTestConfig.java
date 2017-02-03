package examples;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.content.s3.config.AbstractS3ContentRepositoryConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaimTestConfig extends AbstractS3ContentRepositoryConfiguration {

    @Override
    public String bucket() {
        return System.getenv("AWS_S3_BUCKET");
    }

    @Override
    public Region region() {
        return Region.getRegion(Regions.fromName(System.getenv("AWS_S3_REGION")));
    }

}
