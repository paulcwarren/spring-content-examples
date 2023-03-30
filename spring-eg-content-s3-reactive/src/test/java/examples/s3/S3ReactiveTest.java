package examples.s3;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.property.PropertyPath;
import org.springframework.content.commons.repository.ReactiveContentStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@RunWith(Ginkgo4jSpringRunner.class)
public class S3ReactiveTest {

    @Autowired
    private TEntityRepository repo;

    @Autowired
    private TEntityStore store;

    {
        Describe("S3 Reactive", () -> {
            It("should be reactive", () -> {

                final Integer nChunks = 0;

                repo.saveAll(Arrays.asList(new TEntity()))
                    .blockLast(Duration.ofSeconds(10000));

                    repo.findById(1L).doOnNext(entity -> {

                        Random r = new Random();
                        int low = 0;
                        int high = 127;

                        int size = 1024*20;
                        byte[] content = new byte[size];
                        for (int i=0; i < size; i++) {
                            content[i] = (byte) (r.nextInt(high-low) + low);
                        }

                        store.setContent(entity, PropertyPath.from("content"), size, Flux.just(ByteBuffer.wrap(content)))
                            .doOnSuccess(updatedEntity -> {

                                Counter ctr = new Counter();
                                byte[] actual = new byte[size];

                                store.getContent(updatedEntity, PropertyPath.from("content"))
                                    .doOnNext(buffer -> {

                                        int nLen = buffer.remaining();
                                        buffer.get(actual, ctr.read(), nLen);
                                        ctr.add(nLen);

                                    }).blockLast(Duration.ofSeconds(10000));

                                assertThat(ctr.read(), is(content.length));
                                assertThat(Arrays.equals(actual, content), is(true));

                            }).doOnSuccess(updatedEntity -> {
                                store.unsetContent(updatedEntity, PropertyPath.from("content"))
                                    .doOnSuccess(deletedEntity -> {

                                        Counter ctr = new Counter();
                                        byte[] actual = new byte[size];

                                        store.getContent(deletedEntity, PropertyPath.from("content"))
                                        .doOnNext(buffer -> {

                                            int nLen = buffer.remaining();
                                            ctr.add(nLen);

                                        }).blockLast(Duration.ofSeconds(10000));

                                    assertThat(ctr.read(), is(0));
                                    }).block(Duration.ofSeconds(10000));
                            })
                            .block(Duration.ofSeconds(10000));
                    }).block(Duration.ofSeconds(10000));
            });
        });
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableR2dbcRepositories(considerNestedRepositories=true)
    public static class TestConfig {

        private static final String BUCKET = "spring-eg-content-s3-reactive";

        static {
            System.setProperty("spring.content.s3.bucket", BUCKET);
            System.setProperty("aws.region", "us-west-1");
        }

        @Bean
        public S3AsyncClient s3client() throws Exception {

            S3AsyncClient client = LocalStack.getAmazonS3Client();

            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(BUCKET)
                    .build();

            client.headBucket(headBucketRequest)
                .exceptionally(v -> {

                    CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                            .bucket(BUCKET)
                            .build();

                    client.createBucket(bucketRequest)
                        .exceptionally(ex -> { throw new IllegalStateException(); });

                    return null;
                }).get();

            return client;
        }
    }

    @Getter
    @Setter
    public static class TEntity {

        @Id
        private Long id;
        private String firstName;
        private String lastName;

        @ContentId
        private UUID contentId;
        @ContentLength
        private long contentLen;
    }

    public interface TEntityRepository extends ReactiveCrudRepository<TEntity, Long> {}
    public interface TEntityStore extends ReactiveContentStore<TEntity, UUID> {}

    public static class Counter {

        int nRead = 0;

        public void add(int len) {
            nRead = nRead + len;
        }

        public int read() {
            return nRead;
        }
    }

    @Test
    public void noop() {}
}
