/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus;

import it.geosolutions.jaiext.JAIExt;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInitializer;
import org.geoserver.platform.ContextLoadedEvent;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geotools.image.ImageWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import stratus.commons.PostGeoServerInitializer;
import stratus.commons.event.GeoServerInitializedEvent;
import stratus.gwc.GwcLoader;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Slf4j
@Component
public class StratusInitializer implements ApplicationRunner {

    @Autowired
	private ConfigurableApplicationContext app;

    @Autowired
	private GeoServer geoServer;

    @Autowired(required = false)
	private GwcLoader gwcLoader;

	@Autowired(required=false)
	private Collection<GeoServerInitializer> initializers;

	@Autowired(required=false)
	private Collection<PostGeoServerInitializer> postInitializers;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Finalizing Stratus initialization.");

		log.debug("Initializing GWC.");
		if (gwcLoader != null) {
			try {
				gwcLoader.initialize();
			} catch (Throwable t) {
				log.error("Failed to initialize GWC", t);
				throw (t);
			}
		}

		if (initializers!=null) {
            for (GeoServerInitializer initializer : initializers) {
                try {
                    initializer.initialize(geoServer);
                } catch (Throwable t) {
                    log.error("Failed to run initializer " + initializer, t);
                }
            }
        }

		if (postInitializers!=null) {
			for (PostGeoServerInitializer initializer : postInitializers) {
				try {
					initializer.initialize();
				} catch (Throwable t) {
					log.error("Failed to run initializer " + initializer, t);
				}
			}
		}

		log.debug("Configuring JAI.");
	    configureJai();
	    log.debug("JAI successfully configured.");

	    log.debug("Setting data directory.");
        setDataDirectory();
        log.debug("Data directory successfully configured.");

        log.debug("Publishing init events.");
        publishInitEvents();
	}

	//re-init jai
	private void configureJai() {
		JAIExt.initJAIEXT(ImageWorker.isJaiExtEnabled(), true);
	}

	/**
	 * This is needed to work around a really ornery bug whereby GeoServerResourceLoader gets initialized
	 * without its ServletContext being set due to Spring Boot machinery interfering.
	 */
	private void setDataDirectory() {

		//Set the base directory to a temp directory. A few things rely on base directory to work properly.
		//most likely in the future this will need a more robust implementation.
		try {
			log.debug("Attempting to create temp directory.");
			Path baseDir = Files.createTempDirectory("baseDir");
			log.debug("Temp directory successfully created.");
			ServletContext servletContext = app.getBean(ServletContext.class);
			System.setProperty("GEOSERVER_DATA_DIR", baseDir.toString());
			GeoServerResourceLoader geoServerResourceLoader = app.getBean(GeoServerResourceLoader.class);
			geoServerResourceLoader.setServletContext(servletContext);

		} catch (IOException e) {
			//Not really anything we can do here. Too early in the load to have any logging available
			e.printStackTrace();
		}
	}

	private void publishInitEvents() {
		app.publishEvent(new ContextLoadedEvent(app));
		app.publishEvent(new GeoServerInitializedEvent(app));
	}

}
