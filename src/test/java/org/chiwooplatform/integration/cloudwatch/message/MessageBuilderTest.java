package org.chiwooplatform.integration.cloudwatch.message;

import org.springframework.integration.aws.support.AwsHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.chiwooplatform.integration.cloudwatch.kinesis.Constants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBuilderTest {

    private final Logger logger = LoggerFactory.getLogger( MessageBuilderTest.class );

    @Test
    public void testMessageBuilder()
        throws Exception {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader( AwsHeaders.PARTITION_KEY, "kshskdhsfkh" );
        accessor.setHeader( Constants.MESSAGE_KEY, "msg-1001" );
        accessor.setHeader( Constants.MESSAGE_TYPE, "CloudWatch" );
        Message<?> message = MessageBuilder.createMessage( "this is the data.", accessor.getMessageHeaders() );
        logger.info( "message.getHeaders(): {}", message.getHeaders() );
    }

    @Test
    public void testMessageBuilder2()
        throws Exception {
        Message<?> message = MessageBuilder.withPayload( "this is the data." )
                                           .setHeader( Constants.MESSAGE_KEY, "msg-1001" )
                                           .setHeader( Constants.MESSAGE_TYPE, "CloudWatch" ).build();
        logger.info( "message.getHeaders(): {}", message.getHeaders() );
    }
}
