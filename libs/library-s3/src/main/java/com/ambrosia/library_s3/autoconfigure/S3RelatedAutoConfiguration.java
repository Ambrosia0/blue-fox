package com.ambrosia.library_s3.autoconfigure;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ambrosia.library_s3.utils.S3ConfigurationProperties;

import io.awspring.cloud.autoconfigure.s3.properties.S3Properties;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties(S3ConfigurationProperties.class)
public class S3RelatedAutoConfiguration {
    @Bean
    S3Presigner presigner(
        @Value("${spring.cloud.aws.s3.endpoint-override}") URI endpoint,
        S3Client s3Client,
        AwsCredentialsProvider credentialsProvider,
        S3Properties s3Properties
    ){
        return S3Presigner.builder()
            .endpointOverride(endpoint)
            .region(s3Client.serviceClientConfiguration().region())
            .s3Client(s3Client)
            .credentialsProvider(credentialsProvider)
            .serviceConfiguration(
                s3Properties.toS3Configuration()
            )
            .build();
    }
    
}
