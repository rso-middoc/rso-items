package src.main.java.si.fri.rso.middoc.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("configuration-properties")
@ApplicationScoped
public class IntegrationProperties {

    @ConfigValue(value = "collections-service.enabled", watch = true)
    private boolean integrateWithCollectionsService;

    @ConfigValue(value = "compression.ready", watch = true)
    private boolean compressionReady;

    public boolean isIntegrateWithCollectionsService() {
        return integrateWithCollectionsService;
    }

    public void setIntegrateWithCollectionsService(boolean integrateWithCollectionsService) {
        this.integrateWithCollectionsService = integrateWithCollectionsService;
    }

    public boolean isCompressionReady() {
        return compressionReady;
    }

    public void setCompressionReady(boolean compressionReady) {
        this.compressionReady = compressionReady;
    }
}
