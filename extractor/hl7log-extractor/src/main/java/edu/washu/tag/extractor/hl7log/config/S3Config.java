package edu.washu.tag.extractor.hl7log.config;

import java.net.URI;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * Configuration class for setting up the S3 client with custom endpoint.
 * Uses Apache HTTP client for better performance and connection management.
 */
@Configuration
public class S3Config {

    @Value("${s3.endpoint}")
    private String endpoint;

    @Value("${s3.region}")
    private String region;

    @Value("${s3.max-connections}")
    private Integer maxConnections;

    /**
     * Creates an S3 client bean.
     *
     * @return Configured S3Client instance.
     */
    @Bean
    public S3Client s3Client() {
        S3Configuration.Builder configurationBuilder = S3Configuration.builder();
        S3ClientBuilder clientBuilder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .httpClientBuilder(ApacheHttpClient.builder()
                        .maxConnections(ObjectUtils.defaultIfNull(maxConnections, 50))
                        .connectionTimeout(Duration.ofSeconds(5)));

        if (StringUtils.isNotBlank(endpoint)) {
            clientBuilder.endpointOverride(URI.create(endpoint));
            configurationBuilder.pathStyleAccessEnabled(true);
        }

        return clientBuilder
                .serviceConfiguration(configurationBuilder.build())
                .build();
    }
}
