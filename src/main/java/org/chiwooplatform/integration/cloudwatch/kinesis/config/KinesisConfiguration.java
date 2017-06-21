package org.chiwooplatform.integration.cloudwatch.kinesis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.aws.outbound.KinesisMessageHandler;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import org.chiwooplatform.integration.cloudwatch.kinesis.handler.KinesisPutAsyncHandler;
import org.chiwooplatform.integration.cloudwatch.kinesis.handler.PutRecordAsyncHandler;
import org.chiwooplatform.integration.cloudwatch.kinesis.handler.PutRecordsAsyncHandler;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;

@Configuration
public class KinesisConfiguration {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.kinesis.access.id}")
    private String accessKey;

    @Value("${aws.kinesis.access.secret}")
    private String secretKey;

    @Value("${aws.kinesis.streamName}")
    private String streamName;

    @Autowired
    protected AmazonKinesisAsync amazonKinesis;

    private AWSCredentialsProvider credentialsProvider() {
        return new AWSStaticCredentialsProvider( new BasicAWSCredentials( accessKey, secretKey ) );
        // return new AWSCredentialsProviderChain( new ProfileCredentialsProvider("tnt-dev") );
    }

    private static final int TIMEOUT_SEC5 = 1000 * 5;

    @Bean
    public AmazonKinesisAsync amazonKinesis() {
        ClientConfiguration config = new ClientConfiguration();
        config.setConnectionTimeout( TIMEOUT_SEC5 );
        config.setClientExecutionTimeout( TIMEOUT_SEC5 );
        config.setRequestTimeout( TIMEOUT_SEC5 );
        // config.setRetryPolicy( retryPolicy );
        AmazonKinesisAsyncClientBuilder builder = AmazonKinesisAsyncClientBuilder.standard();
        builder.setClientConfiguration( config );
        builder.setRegion( region ); // 
        builder.setCredentials( credentialsProvider() );
        //    builder.setEndpointConfiguration( endpointConfiguration );
        //    builder.setExecutorFactory( executorFactory );
        //    builder.setRequestHandlers( handlers );
        AmazonKinesisAsync amazonKinesis = builder.build();
        //        amazonKinesis.describeStream( KINESIS_STREAM );
        //        amazonKinesis.putRecord( new PutRecordRequest() );
        //        amazonKinesis.putRecords( new PutRecordsRequest() );
        amazonKinesis.putRecordAsync( new PutRecordRequest(), new PutRecordAsyncHandler() );
        amazonKinesis.putRecordsAsync( new PutRecordsRequest(), new PutRecordsAsyncHandler() );
        return amazonKinesis;
    }

    @Bean
    public AsyncHandler<?, ?> asyncHandler() {
        return new KinesisPutAsyncHandler();
    }

    @Bean
    public Converter<Object, byte[]> converter() {
        return new Converter<Object, byte[]>() {

            private SerializingConverter converter = new SerializingConverter();

            @Override
            public byte[] convert( Object source ) {
                System.out.println( "source.getClass().getName(): " + source.getClass().getName() );
                return this.converter.convert( source );
            }
        };
    }

    private static final String KINESIS_CHANNEL = "kinesisChannel";

    @Bean
    public MessageChannel kinesisChannel() {
        return MessageChannels.direct( KINESIS_CHANNEL ).get();
    }

    @Bean
    @ServiceActivator(inputChannel = KINESIS_CHANNEL)
    public MessageHandler kinesisMessageHandler() {
        KinesisMessageHandler messageHandler = new KinesisMessageHandler( amazonKinesis() );
        messageHandler.setStream( streamName );
        // messageHandler.setSync( true );
        messageHandler.setConverter( converter() );
        messageHandler.setAsyncHandler( asyncHandler() );
        return messageHandler;
    }
}
