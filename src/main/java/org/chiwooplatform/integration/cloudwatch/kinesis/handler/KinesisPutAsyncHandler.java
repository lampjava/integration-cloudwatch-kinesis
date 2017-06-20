package org.chiwooplatform.integration.cloudwatch.kinesis.handler;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kinesis.model.PutRecordRequest;

public class KinesisPutAsyncHandler
    implements AsyncHandler<AmazonWebServiceRequest, AmazonWebServiceResult<com.amazonaws.ResponseMetadata>> {

    private final Logger logger = LoggerFactory.getLogger( KinesisPutAsyncHandler.class );

    private static String value( ByteBuffer buffer ) {
        byte[] bytes = new byte[buffer.position()];
        buffer.flip();
        buffer.get( bytes );
        return new String( bytes );
    }

    @Override
    public void onError( Exception e ) {
        logger.error( e.getMessage(), e );
    }

    @Override
    public void onSuccess( AmazonWebServiceRequest request,
                           AmazonWebServiceResult<com.amazonaws.ResponseMetadata> result ) {
        if ( request.getClass().isInstance( PutRecordRequest.class ) ) {
            PutRecordRequest req = (PutRecordRequest) request;
            logger.debug( "Stream: {}, PartitionKey: {}, data: {}", req.getStreamName(), req.getPartitionKey(),
                          value( req.getData() ) );
        } else {
            logger.info( "request.getClass(): {}", request.getClass().getName() );
        }
    }
}