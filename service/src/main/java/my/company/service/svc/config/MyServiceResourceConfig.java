package my.company.service.svc.config;

import my.company.service.svc.filter.FieldFilteringResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyServiceResourceConfig extends ResourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyServiceResourceConfig.class);

    public MyServiceResourceConfig() {
        GuiceFeature guiceFeature = new GuiceFeature();

        register(guiceFeature);
        register(new JsonProvider());
        register(FieldFilteringResponseFilter.class);
        packages("my.company.service.svc");
        LOGGER.info("MyServiceResourceConfig loaded");
    }
}
