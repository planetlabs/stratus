/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.resource;

import com.amazonaws.services.s3.AmazonS3;
import stratus.wps.s3.S3Connector;
import org.geoserver.platform.resource.ResourceListener;
import org.geoserver.platform.resource.ResourceNotification;
import org.geoserver.platform.resource.ResourceNotificationDispatcher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Resource watcher that uses Amazon S3 client tools and a newSingleThreadScheduledExecutor thread pool to monitor GeoServer Resources
 */

public class S3Watcher implements ResourceNotificationDispatcher, DisposableBean {
    List<ResourceListener> listeners = new CopyOnWriteArrayList<ResourceListener>();

    private ScheduledExecutorService pool;

    protected long lastmodified;

    CopyOnWriteArrayList<S3Watcher.Watch> watchers = new CopyOnWriteArrayList<S3Watcher.Watch>();

    private final AmazonS3 amazonS3;

    public S3Watcher(AmazonS3 amazonS3){
        this.amazonS3 = amazonS3;
        this.pool = Executors.newSingleThreadScheduledExecutor(tFactory);

    }



    /**
     * Tracks S3 Resource status
     */
    static class Delta {
        final String bucketName;

        final String key;

        final ResourceNotification.Kind kind;

        final List<String> created;

        final List<String> removed;

        final List<String> modified;

        public Delta(String bucketName,String key, ResourceNotification.Kind kind) {
            this.bucketName = bucketName;
            this.key = key;
            this.kind = kind;
            this.created = null;
            this.removed = null;
            this.modified = null;
        }

        public Delta(String bucketName,String key, ResourceNotification.Kind kind, List<String> created, List<String> removed,
                     List<String> modified) {
            this.bucketName = bucketName;
            this.key = key;
            this.kind = ResourceNotification.Kind.ENTRY_MODIFY;
            this.created = created != null ? created : (List<String>) Collections.EMPTY_LIST;
            this.removed = removed != null ? removed : (List<String>) Collections.EMPTY_LIST;
            this.modified = modified != null ? modified : (List<String>) Collections.EMPTY_LIST;
        }

        @Override
        public String toString() {
            return "Delta [bucket=" + bucketName + ", key="+ key + "created=" + created + ", removed=" + removed
                    + ", modified=" + modified + "]";
        }

    }


    private class Watch implements Comparable<S3Watcher.Watch> {
        /** Path to use during notification */
        final String key;

        final String bucketName;


        List<ResourceListener> listeners = new CopyOnWriteArrayList<ResourceListener>();

        /** When last notification was sent */
        long last = 0;

        /** Used to track resource creation / deletion */
        boolean exsists;

        public Watch(String bucketName, String key) {
            this.key = key;
            this.bucketName = bucketName;
            this.exsists = amazonS3.doesObjectExist(bucketName,key);
            this.last = exsists ? amazonS3.getObjectMetadata(bucketName,key).getLastModified().getTime() : 0;
        }

        public void addListener(ResourceListener listener){
            listeners.add(listener);
        }
        public void removeListener(ResourceListener listener){
            listeners.remove(listener);
        }

        /** Path used for notification */
        public String getKey() {
            return key;
        }

        public List<ResourceListener> getListeners() {
            return listeners;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((bucketName == null) ? 0 : bucketName.hashCode());
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            S3Watcher.Watch other = (S3Watcher.Watch) obj;
            if (bucketName == null) {
                if (other.bucketName != null)
                    return false;
            } else if (!bucketName.equals(other.bucketName))
                return false;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Watch [path=" + key + ", bucket=" + bucketName + ", listeners="+listeners.size()+"]";
        }

        @Override
        public int compareTo(S3Watcher.Watch other) {
            return key.compareTo(other.key);
        }

        public S3Watcher.Delta changed(long now) {
            if (!amazonS3.doesObjectExist(bucketName,key)) {
                if (exsists) {
                    exsists = false;
                    // file has been deleted!
                    this.last = now;
                    return new S3Watcher.Delta(bucketName,key, ResourceNotification.Kind.ENTRY_DELETE);

                } else {
                    return null; // no change file still deleted!
                }
            }

            long fileModified = amazonS3.getObjectMetadata(bucketName,key).getLastModified().getTime();
            if (fileModified > last || !exsists) {
                if (exsists) {
                    this.last = fileModified;
                    return new S3Watcher.Delta(bucketName,key, ResourceNotification.Kind.ENTRY_MODIFY);
                }
                else {
                    exsists = true;
                    this.last = fileModified;
                    return new S3Watcher.Delta(bucketName,key, ResourceNotification.Kind.ENTRY_CREATE);
                }
            } else {
                return null; // no change!
            }


            
        }

        public boolean isMatch(String bucketName, String key) {

            if (this.key == null) {
                if (key != null){
                    return false;
                }
            } else if (!this.key.equals(key)){
                return false;
            }

            if (this.bucketName == null) {
                if (bucketName != null){
                    return false;
                }
            } else if (!this.bucketName.equals(bucketName)){
                return false;
            }
            return true;
        }

    }



    private Runnable sync = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            for (S3Watcher.Watch watch : watchers) {
                if( watch.getListeners().isEmpty()){
                    watchers.remove(watch);
                    continue;
                }
                S3Watcher.Delta delta = watch.changed(now);
                if (delta != null) {

                    /** Created based on created/removed/modified files */
                    List<ResourceNotification.Event> events = S3ResourceNotification.delta(
                            watch.bucketName, watch.key, delta.created, delta.removed, delta.modified);

                    ResourceNotification notify = new S3ResourceNotification( watch.bucketName,watch.key,
                            delta.kind, watch.last, events);

                    for (ResourceListener listener : watch.getListeners()) {
                        try {
                            listener.changed(notify);
                        } catch (Throwable t) {
                            Logger logger = Logger.getLogger(listener.getClass().getPackage()
                                    .getName());
                            logger.log(Level.FINE,
                                    "Unable to notify " + watch + ":" + t.getMessage(), t);
                        }
                    }
                }
            }
        }
    };

    private ScheduledFuture<?> monitor;

    private TimeUnit unit = TimeUnit.SECONDS;

    private long delay = 10;

    private static CustomizableThreadFactory tFactory;
    static {
        tFactory = new CustomizableThreadFactory("FileSystemWatcher-");
        tFactory.setDaemon(true);
    }




    private S3Watcher.Watch watch(String bucketName, String key ){
        if( bucketName == null || key == null ){
            return null;
        }
        for( S3Watcher.Watch watch : watchers ){
            if( watch.isMatch(bucketName,key)){
                return watch;
            }
        }
        return null; // not found
    }
    public synchronized void addListener(String s3path, ResourceListener listener) {
        String[] bucketandkey = S3Connector.getS3PathParts(s3path);
        if( bucketandkey == null || bucketandkey.length<2){
            throw new NullPointerException("Bucket and Key for notification is required");
        }
        S3Watcher.Watch watch = watch( bucketandkey[0], bucketandkey[1] );
        if( watch == null ){
            watch = new S3Watcher.Watch(bucketandkey[0], bucketandkey[1]);
            watchers.add(watch);
            if( monitor == null){
                monitor = pool.scheduleWithFixedDelay(sync, delay, delay, unit);
            }
        }
        watch.addListener(listener);
    }

    public synchronized boolean removeListener(String s3path, ResourceListener listener) {
        String[] bucketandkey = S3Connector.getS3PathParts(s3path);
        if( bucketandkey == null || bucketandkey.length<2){
            throw new NullPointerException("Bucket and Key for notification is required");
        }
        S3Watcher.Watch watch = watch( bucketandkey[0], bucketandkey[1] );
        boolean removed = false;
        if( watch != null ){
            watch.removeListener(listener);
            if( watch.getListeners().isEmpty()){
                removed = watchers.remove(watch);
            }
        }
        if (removed && watchers.isEmpty()) {
            if (monitor != null) {
                monitor.cancel(false); // stop watching nobody is looking
                monitor = null;
            }
        }
        return removed;
    }

    /**
     * package visibility to allow test cases to set a shorter delay for testing.
     *
     * @param delay
     * @param unit
     */
    void schedule(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = unit;
        if (monitor != null) {
            monitor.cancel(false);
            monitor = pool.scheduleWithFixedDelay(sync, delay, delay, unit);
        }
    }

    @Override
    public void destroy() throws Exception {
        pool.shutdown();
    }

    @Override
    public void changed(ResourceNotification notification) {
        throw new UnsupportedOperationException();
    }
}
