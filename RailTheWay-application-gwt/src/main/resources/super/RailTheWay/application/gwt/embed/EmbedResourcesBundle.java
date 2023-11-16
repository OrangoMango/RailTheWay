// File managed by WebFX (DO NOT EDIT MANUALLY)
package RailTheWay.application.gwt.embed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import dev.webfx.platform.resource.spi.impl.gwt.GwtResourceBundleBase;

public interface EmbedResourcesBundle extends ClientBundle {

    EmbedResourcesBundle R = GWT.create(EmbedResourcesBundle.class);
    @Source("dev/webfx/platform/meta/exe/exe.properties")
    TextResource r1();

    @Source("worlds/world1.wld")
    TextResource r2();

    @Source("worlds/world2.wld")
    TextResource r3();

    @Source("worlds/world3.wld")
    TextResource r4();

    @Source("worlds/world4.wld")
    TextResource r5();

    @Source("worlds/world5.wld")
    TextResource r6();

    @Source("worlds/world6.wld")
    TextResource r7();

    @Source("worlds/world7.wld")
    TextResource r8();

    @Source("worlds/world8.wld")
    TextResource r9();

    @Source("worlds/world9.wld")
    TextResource r10();



    final class ProvidedGwtResourceBundle extends GwtResourceBundleBase {
        public ProvidedGwtResourceBundle() {
            registerResource("dev/webfx/platform/meta/exe/exe.properties", R.r1());
            registerResource("worlds/world1.wld", R.r2());
            registerResource("worlds/world2.wld", R.r3());
            registerResource("worlds/world3.wld", R.r4());
            registerResource("worlds/world4.wld", R.r5());
            registerResource("worlds/world5.wld", R.r6());
            registerResource("worlds/world6.wld", R.r7());
            registerResource("worlds/world7.wld", R.r8());
            registerResource("worlds/world8.wld", R.r9());
            registerResource("worlds/world9.wld", R.r10());

        }
    }
}
