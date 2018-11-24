package my.company.service.svc.config;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.InjectionManagerProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.jvnet.hk2.guice.bridge.api.HK2IntoGuiceBridge;

public class GuiceFeature implements Feature {
    private final Module[] modules;
    private Injector injector;

    public GuiceFeature(Module... modules) {
        this.modules = modules;
    }

    @Override
    public boolean configure(FeatureContext context) {
        ServiceLocator locator = InjectionManagerProvider.getInjectionManager(context).getInstance(ServiceLocator.class);
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);
        GuiceIntoHK2Bridge guiceBridge = locator.getService(GuiceIntoHK2Bridge.class);

        injector = Guice.createInjector(Stage.PRODUCTION, ObjectArrays.concat(modules, new HK2IntoGuiceBridge(locator)));

        guiceBridge.bridgeGuiceInjector(injector);
        return true;
    }
}
