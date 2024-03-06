package examples.s3;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Configuration
public class S3Config {

    private static final String BUCKET = "aws-test-bucket";

    static {
        System.setProperty("spring.content.s3.bucket", BUCKET);

//        try {
//            Map<String,String> props = new HashMap<>();
//            props.put("AWS_REGION", Regions.US_WEST_1.getName());
//            props.put("AWS_ACCESS_KEY_ID", "user");
//            props.put("AWS_SECRET_KEY", "password");
//            setEnv(props);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


	@Autowired
	private Environment env;

	@Bean
	public S3Client client(/*AWSCredentials awsCredentials*/) throws URISyntaxException {
        S3Client client = LocalStack.getAmazonS3Client();

        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(BUCKET)
                .build();

        try {
            client.headBucket(headBucketRequest);
        } catch (NoSuchBucketException e) {

            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(BUCKET)
                    .build();
            client.createBucket(bucketRequest);
        }

        return client;
	}

	@Bean
	public String bucketName() {
	    return BUCKET;
	}

    @Bean
    public PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

//    @SuppressWarnings("unchecked")
//    public static void setEnv(Map<String, String> newenv) throws Exception {
//        try {
//            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
//            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
//            theEnvironmentField.setAccessible(true);
//            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
//            env.putAll(newenv);
//            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
//            theCaseInsensitiveEnvironmentField.setAccessible(true);
//            Map<String, String> cienv = (Map<String, String>)theCaseInsensitiveEnvironmentField.get(null);
//            cienv.putAll(newenv);
//        } catch (NoSuchFieldException e) {
//            Class[] classes = Collections.class.getDeclaredClasses();
//            Map<String, String> env = System.getenv();
//            for(Class cl : classes) {
//                if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
//                    Field field = cl.getDeclaredField("m");
//                    field.setAccessible(true);
//                    Object obj = field.get(env);
//                    Map<String, String> map = (Map<String, String>) obj;
//                    map.clear();
//                    map.putAll(newenv);
//                }
//            }
//        }
//    }
}
