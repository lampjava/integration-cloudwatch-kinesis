package org.chiwooplatform.integration.cloudwatch.kinesis.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;

public class PutRecordsAsyncHandler
    implements AsyncHandler<PutRecordsRequest, PutRecordsResult> {

    private final Logger logger = LoggerFactory.getLogger( PutRecordsAsyncHandler.class );

    public void onError( Exception e ) {
        // todo 
    }

    public void onSuccess( PutRecordsRequest request, PutRecordsResult result ) {
        final List<PutRecordsRequestEntry> putRecordsRequestEntryList = request.getRecords();
        final List<PutRecordsResultEntry> putRecordsResultEntryList = result.getRecords();
        final List<PutRecordsRequestEntry> failedRecordList = new ArrayList<PutRecordsRequestEntry>();
        logger.info( "Failed Count is :" + result.getFailedRecordCount() );
        for ( int i = 0; i < putRecordsResultEntryList.size(); i++ ) {
            final PutRecordsRequestEntry requestEntry = putRecordsRequestEntryList.get( i );
            final PutRecordsResultEntry resultEntry = putRecordsResultEntryList.get( i );
            String errcode = resultEntry.getErrorCode();
            if ( errcode != null   ) {
                logger.info( "Failed Record is:" + resultEntry.toString() );
                failedRecordList.add( requestEntry );
            }
        }
    }
}