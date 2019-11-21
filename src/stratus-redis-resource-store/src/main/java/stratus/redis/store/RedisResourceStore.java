/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.store;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.geoserver.platform.resource.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author joshfix, Niels Charlier
 */

@Slf4j
@Service("resourceStoreImpl")
@Lazy
public class RedisResourceStore implements ResourceStore {

    protected File cacheDir;
    protected LockProvider lockProvider = new NullLockProvider();

    private ResourceDataService dataService;
    
    private final RedisNotificationDispatcher resourceNotificationDispatcher;

    public RedisResourceStore(ResourceDataService dataService,
                              RedisNotificationDispatcher resourceNotificationDispatcher,
                              RedisResourceInitializer initializer) {
        this.dataService = dataService;
        this.resourceNotificationDispatcher = resourceNotificationDispatcher;
        try {
            cacheDir = Files.createTempDirectory("redis.resourcestore.cache").toFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        initializer.setRedisResourceStore(this);
        initializer.init();
    }

    public void setDataService(ResourceDataService dataService) {
        this.dataService = dataService;
    }

	public ResourceDataService getDataService() {
		return dataService;
	}

    private File cache(RedisResource res, boolean dir, ResourceDataService.TypeAndContent info) {
        InputStream is = null;
    	File file = new File(cacheDir, res.path());
    	file.getParentFile().mkdirs();
    	if (dir) {
    		file.mkdir();
    		for (Resource child : res.list()) {
                ResourceDataService.TypeAndContent childInfo = dataService.getTypeAndContent(child.path());
    			cache((RedisResource) child, childInfo.getType() == Resource.Type.DIRECTORY, childInfo);
    		}
    	} else {
    		try {
    			file.createNewFile();
    			if (info.getType() == Resource.Type.RESOURCE) {

    			    try {
                        is = res.in(info);
                        OutputStream os = new FileOutputStream(file);
                        IOUtils.copy(is, os);
                    }
                    catch(IOException e)
                    {
                        log.error("Error copying file from Redis to temp location: {}", e.getMessage());
                        throw new IllegalStateException(e);
                    }
                    //make sure its closed so we don't have connections hang
                    finally { IOUtils.closeQuietly(is); }

    			}
	    	} catch (IOException e) {
                log.error("Error copying file from Redis to temp location: {}", e.getMessage());
    		}

    	}
    	return file;
    }

    @Override
    public Resource get(String path) {
        log.debug("Getting resource path \'" + path + "\'.");
        return new RedisResource(path);
    }

    @Override
    public boolean remove(String path) {
        log.debug("Removing resource path \'" + path + "\'.");
        return new RedisResource(path).delete();
    }

    @Override
    public boolean move(String path, String target) {
        // TODO this is a hack!  need to find out why GeoServerPersister is moving styles to root directory after
        // removing workspace assignment from style
        /*
        if (!target.contains("/") && target.endsWith(".sld")) {
            target = "styles/" + target;
        }*/

        log.debug("Moving resource path \'" + path + "\' to target \'" + target + "\'.");
        return new RedisResource(path).renameTo(new RedisResource(target));
    }

    @Override
    public ResourceNotificationDispatcher getResourceNotificationDispatcher() {
        return resourceNotificationDispatcher;
    }

    public LockProvider getLockProvider() {
        return lockProvider;
    }

    protected class RedisResource implements Resource {

        private final String path;

        public RedisResource(String path) {
            this.path = stratus.redis.store.Paths.path(path);
        }

        @Override
        public String path() {
        	return path;
        }

        @Override
        public String name() {
            return stratus.redis.store.Paths.name(path);
        }

        @Override
        public Lock lock() {
            return lockProvider.acquire(toString());
        }

        @Override
        public void addListener(ResourceListener listener) {
            resourceNotificationDispatcher.addListener(path(), listener);
        }

        @Override
        public void removeListener(ResourceListener listener) {
            resourceNotificationDispatcher.removeListener(path(), listener);
        }

        @Override
        public InputStream in() {
            return in(dataService.getTypeAndContent(path));
        }
        private InputStream in(ResourceDataService.TypeAndContent info) {
            log.debug("Getting input stream for path " + path);
        	if (info.getType() == Type.DIRECTORY) {
                throw new IllegalStateException("Directory (not a file)");
            }
        	final Lock lock = lock();
        	try {
	            byte[] content = info.getContent();
	    	    if (content == null) {
	    	    	content = new byte[]{};
	    	    	dataService.createResource(path, content);
	    	    }

	    	    return new ByteArrayInputStream(content);
        	} finally {
        		lock.release();
        	}
        }

        @Override
        public OutputStream out() {
            log.debug("Getting output stream for path " + path);
        	if (getType() == Type.DIRECTORY) {
                throw new IllegalStateException("Directory (not a file)");
            }
            List<ResourceNotification.Event> events = SimpleResourceNotificationDispatcher.createEvents(this, ResourceNotification.Kind.ENTRY_CREATE);
            if (dataService.createResource(path, new byte[]{})) {
                resourceNotificationDispatcher.changed(new ResourceNotification(path(), ResourceNotification.Kind.ENTRY_CREATE, System.currentTimeMillis(), events));
            }
            return new OutputStream() {
                ByteArrayOutputStream delegate = new ByteArrayOutputStream();

                @Override
                public void close() throws IOException {
                	final Lock lock = lock();
                	try {
	                	List<ResourceNotification.Event> events = SimpleResourceNotificationDispatcher.createEvents(RedisResource.this,
	                            ResourceNotification.Kind.ENTRY_MODIFY);

	                    dataService.saveResource(path, delegate.toByteArray());

	                    resourceNotificationDispatcher.changed(new ResourceNotification(path(), ResourceNotification.Kind.ENTRY_MODIFY,
	                            System.currentTimeMillis(), events));
	                    delegate.close();
                	} finally {
                		lock.release();
                	}
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    delegate.write(b, off, len);
                }

                @Override
                public void flush() throws IOException {
                    delegate.flush();
                }

                @Override
                public void write(byte[] b) throws IOException {
                    delegate.write(b);
                }

                @Override
                public void write(int b) throws IOException {
                    delegate.write(b);
                }
            };
        }

        @Override
        public File file() {
            return file(dataService.getTypeAndContent(path));
        }

        private File file(ResourceDataService.TypeAndContent info) {
            log.debug("Getting file for resource path " + path);
            if (info.getType() == Type.DIRECTORY) {
                throw new IllegalStateException("Directory (not a file)");
            }
            final Lock lock = lock();
            try {
            	return cache(this, false, info);
            } finally {
            	lock.release();
            }
        }

        @Override
        public File dir() {
            return dir(dataService.getTypeAndContent(path));
        }
        private File dir(ResourceDataService.TypeAndContent info) {
            if (info.getType() == Type.RESOURCE) {
                throw new IllegalStateException("File (not a directory)");
            }
            final Lock lock = lock();
            try {
            	return cache(this, true, info);
            } finally {
            	lock.release();
            }
        }

        @Override
        public long lastmodified() {
            Date lastModified = dataService.getLastModified(path);
            if (null == lastModified) {
                return 0L;
            }
            return lastModified.getTime();
        }

        @Override
        public Resource parent() {
        	if (path.equals(stratus.redis.store.Paths.BASE)) {
        		return null;
        	}
            return new RedisResource(stratus.redis.store.Paths.parent(path));
        }

        @Override
        public Resource get(String resourcePath) {
            return new RedisResource(stratus.redis.store.Paths.path(path, resourcePath));
        }

        @Override
        public List<Resource> list() {
            if (getType() != Type.DIRECTORY) {
                return Collections.emptyList();
            }
            List<Resource> resources = new ArrayList<>();
            for (String child : dataService.getChildren(path)) {
                resources.add(get(child));
            }
            return resources;
        }

        @Override
        public Type getType() {
            if (dataService == null) {
                log.warn("ResourceDataService has not been initialized.");
                return null;
            }
            return dataService.getType(path);
        }

        @Override
        public boolean delete() {
        	if (getType() != Type.UNDEFINED) {
        		List<Lock> locks = new ArrayList<>();
                lockRecursively(locks);
                try {
	        		List<ResourceNotification.Event> events = SimpleResourceNotificationDispatcher.createEvents(this,
	                        ResourceNotification.Kind.ENTRY_DELETE);
	        		dataService.delete(path);
	        		resourceNotificationDispatcher.changed(new ResourceNotification(path(), ResourceNotification.Kind.ENTRY_DELETE, 
	                        System.currentTimeMillis(), events));        		
	        		return true;
                } finally {
                	for (Lock lock : locks) {
                        lock.release();
                    }
                }
        	} else {
        		return false;
        	}
        }

        @Override
        public boolean renameTo(Resource dest) {
        	List<ResourceNotification.Event> eventsDelete = SimpleResourceNotificationDispatcher.createEvents(this,
                    ResourceNotification.Kind.ENTRY_DELETE);
            List<ResourceNotification.Event> eventsRename = SimpleResourceNotificationDispatcher.createRenameEvents(this, dest);
            if (dest.getType() == Type.DIRECTORY && getType() != Type.DIRECTORY) {
            	//if we overwrite a directory with a resource, all children of that directory must be deleted as well.
            	if (!dest.delete()) {
            		return false;
            	}
            }
            dataService.move(path, dest.path());
        	resourceNotificationDispatcher.changed(new ResourceNotification(path(), ResourceNotification.Kind.ENTRY_DELETE, 
                    System.currentTimeMillis(), eventsDelete));
                resourceNotificationDispatcher.changed(new ResourceNotification(path(), eventsRename.get(0).getKind(), 
                    System.currentTimeMillis(), eventsRename));
        	return true;
        }
        
        @Override
        public boolean equals(Object o) {
        	return o instanceof RedisResource && path.equals(((RedisResource) o).path);
        }
        
        @Override
        public int hashCode() {
        	return path.hashCode();
        }
        
        private void lockRecursively(List<Lock> locks) {
            for (Resource child : list()) {
                ((RedisResource)child).lockRecursively(locks); 
            }
            locks.add(lock());            
        }

    }

}
