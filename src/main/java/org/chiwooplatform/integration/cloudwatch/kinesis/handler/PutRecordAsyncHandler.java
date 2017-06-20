package org.chiwooplatform.integration.cloudwatch.kinesis.handler;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;

public class PutRecordAsyncHandler
    implements AsyncHandler<PutRecordRequest, PutRecordResult> {

    private final Logger logger = LoggerFactory.getLogger( PutRecordAsyncHandler.class );

    private static String value( ByteBuffer buffer ) {
        byte[] bytes = new byte[buffer.position()];
        buffer.flip();
        buffer.get( bytes );
        return new String( bytes );
    }

    @Override
    public void onError( Exception e ) {
    }

    @Override
    public void onSuccess( PutRecordRequest req, PutRecordResult res ) {
        logger.debug( "Stream: {}, PartitionKey: {}, data: {}", req.getStreamName(), req.getPartitionKey(),
                      value( req.getData() ) );
        logger.debug( "published successfully" );
    }
}