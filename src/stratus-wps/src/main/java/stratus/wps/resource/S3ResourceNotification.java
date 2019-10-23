/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.resource;

import org.geoserver.platform.resource.ResourceNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class S3ResourceNotification extends ResourceNotification {


    /**
     * Notification changes to directory contents.
     *
     * @param bucketName S3 bucket
     * @param key S3 key
     * @param kind nature of change
     * @param timestamp local time stamp of change
     * @param delta List of changes
     */
    @SuppressWarnings("unchecked")
    public S3ResourceNotification( String bucketName, String key, Kind kind, long timestamp, List<Event> delta ){
        super("s3://"+bucketName+"/"+key,kind,timestamp,delta);
    }

    public static List<Event> delta(String bucketName, String key, List<String> created, List<String> removed, List<String> modified) {
        if( created == null ){
            created = Collections.emptyList();
        }
        if( removed == null ){
            removed = Collections.emptyList();
        }
        if( modified == null ){
            modified = Collections.emptyList();
        }
        int size = created.size()+removed.size()+modified.size();
        if( size == 0 ) {
            return null;
        }
        List<Event> delta = new ArrayList<Event>( size );
        for( String createdKey : created ){
            String newPath = bucketName+"/"+createdKey;
            delta.add( new Event(newPath,  Kind.ENTRY_CREATE ) );
        }
        for( String removedKey : removed ){
            String deletePath = bucketName+"/"+removedKey;
            delta.add( new Event(deletePath,  Kind.ENTRY_DELETE ) );
        }
        for( String modifiedKey : modified ){
            String changedPath = bucketName+"/"+modifiedKey;
            delta.add( new Event(changedPath,  Kind.ENTRY_MODIFY ) );
        }
        return delta;
    }
}
