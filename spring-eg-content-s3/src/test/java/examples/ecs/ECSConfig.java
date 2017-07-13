package examples.ecs;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.content.s3.config.EnableS3ContentRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;

@Configuration
@EnableJpaRepositories(basePackages="examples")
@EnableS3ContentRepositories(basePackages="examples")
public class ECSConfig {

    @Value("${ecs.url:#{environment.ECS_URL}}")
    private String url;

    @Value("${ecs.accessKey:#{environment.ECS_ACCESS_KEY}}")
    private String accessKey;

    @Value("${ecs.secretKey:#{environment.ECS_SECRET_KEY}}")
    private String secretKey;

    @Value("${ecs.bucket:#{environment.ECS_BUCKET}}")
    private String bucket;

    @Bean
    public String bucket() {
    	return bucket;
    }
    
    @Bean
    public SimpleStorageResourceLoader simpleStorageResourceLoader() {
        return new SimpleStorageResourceLoader(amazonS3Client(basicAWSCredentials()));
    }

    @Bean
    public BasicAWSCredentials basicAWSCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }
    
    @Bean ClientConfiguration clientConfig() {
    	ClientConfiguration clientConfig = new ClientConfiguration(); 
    	SSLSocketFactory sf = createSSLConnection(); 
    	sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
    	clientConfig.getApacheHttpClientConfig().setSslSocketFactory(sf);
    	return clientConfig;
    }
    
    private SSLSocketFactory createSSLConnection() 
    { 
        SSLContext sslcontext = SSLContexts.createSystemDefault(); 
        final TrustManager trustEverything = new X509TrustManager() { 
            private final X509Certificate[]  
                    acceptedIssuers = new X509Certificate[0]; 
 
            @Override 
            public void checkClientTrusted(X509Certificate[] chain, String authType) {} 
 
            @Override 
            public void checkServerTrusted(X509Certificate[] chain, String authType) {} 
 
            @Override 
            public X509Certificate[] getAcceptedIssuers() { 
                return this.acceptedIssuers; 
            } 
        }; 
         
        TrustManager[] managers = {trustEverything}; 
        try { 
            sslcontext = SSLContext.getInstance("TLS"); 
            sslcontext.init(null, managers, null); 
        } catch (Exception e) { 
            new IllegalStateException("Cannot create security context", e); 
        } 
        return new SSLSocketFactory(sslcontext, 
                SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER); 
    } 

	@Bean
    S3ClientOptions s3ClientOptions() {
        S3ClientOptions opts = new S3ClientOptions();
        opts.setPathStyleAccess(true);
        return opts;
    }

    @Bean
    public AmazonS3Client amazonS3Client(AWSCredentials awsCredentials) {
        AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials, clientConfig());
        amazonS3Client.setEndpoint(url);
        amazonS3Client.setS3ClientOptions(s3ClientOptions());
        return amazonS3Client;
    }
}
