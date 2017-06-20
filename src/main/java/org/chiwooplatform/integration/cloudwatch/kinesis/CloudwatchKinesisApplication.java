package org.chiwooplatform.integration.cloudwatch.kinesis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;

import org.chiwooplatform.integration.cloudwatch.kinesis.support.TransactionLoggingFilter;

@EnableIntegration
@SpringBootApplication
public class CloudwatchKinesisApplication {

    public static final String TXID = "TXID";

    public static void main( String[] args ) {
        SpringApplication.run( CloudwatchKinesisApplication.class, args );
    }

    @Bean
    public TransactionLoggingFilter TransactionLoggingFilter() {
        return new TransactionLoggingFilter();
    }
}
