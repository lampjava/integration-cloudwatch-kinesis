package org.chiwooplatform.integration.cloudwatch.kinesis.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.integration.aws.support.AwsHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.chiwooplatform.integration.cloudwatch.kinesis.Constants;
import org.chiwooplatform.integration.cloudwatch.message.SnsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@RestController
public class IntegrationAdapterController {

    protected static final String BASE_URI = "/adapter/cloudwatch";
 

    protected final Logger logger = LoggerFactory.getLogger( IntegrationAdapterController.class );

    @Autowired
    private MessageChannel kinesisChannel;

    @Autowired
    private RestTemplate restTemplate;

    private Message<?> buildMessage( final String msg, final String messageKey ) {
        return MessageBuilder.withPayload( msg ).setHeader( Constants.MESSAGE_KEY, messageKey )
                             .setHeader( Constants.MESSAGE_TYPE, "CloudWatch" ).build();
    }

    private void send( final String channelId, final String msg, final String tXID ) {
        final String messageKey = "cloudwatch.json." + tXID;
        Message<?> message = MessageBuilder.withPayload( buildMessage( msg, messageKey ) )
                                           .setHeader( AwsHeaders.PARTITION_KEY, channelId ).build();
        kinesisChannel.send( message );
    }


    @RequestMapping(value = BASE_URI + "/{channelId}", method = RequestMethod.POST, produces = {
        MediaType.TEXT_PLAIN_VALUE })
    public void send( @PathVariable("channelId") String channelId, HttpServletRequest request ) {
        // request.getInputStream().
        logger.info( " channelId: {}", channelId );
        String tXID = (String) request.getAttribute( Constants.TXID );
        logger.info( "You can add TXID '{}' header-key of message for tracing requested transaction", tXID );
        Gson gson = new Gson();
        SnsMessage message = null;
        try {
            String payload = IOUtils.toString( request.getReader() );
            logger.info( "payload: {}", payload );
            message = gson.fromJson( payload, SnsMessage.class );
            logger.info( "message: {}", message );
            if ( "SubscriptionConfirmation".equals( message.getType() ) ) {
                String url = message.getSubscribeURL();
                logger.info( "Type: {}, SubscribeURL: {}", message.getType(), url );
                String response = restTemplate.getForObject( url, String.class );
                logger.info( "AWS-RESPONSE: {}", response );
            } else if ( "Notification".equals( message.getType() ) ) {
                logger.info( "Notify AWS-SNS Msg." );
                this.send( channelId, message.toString(), tXID );
            }
        } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
        }
    }

    @RequestMapping(value = BASE_URI + "/feedback", method = RequestMethod.GET)
    public String feedback( HttpServletRequest request ) {
        return "ok, I've got it.";
    }
}
