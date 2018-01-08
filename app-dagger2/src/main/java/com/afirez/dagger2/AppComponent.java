package com.afirez.dagger2;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by afirez on 18-1-4.
 */
@Component(modules = {
        AppModule.class,
        UiModule.class,
        AndroidSupportInjectionModule.class
})
public interface AppComponent extends AndroidInjector<App> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<App>  {}
}
