package org.chiwooplatform.integration.cloudwatch.kinesis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Configuration {

    @Autowired
    private Regions region;

    @Autowired
    public AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        builder.setRegion( region.getName() );
        builder.setCredentials( awsCredentialsProvider );
        final AmazonS3 s3 = builder.build();
        return s3;
    }
}
