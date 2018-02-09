package com.afirez.dagger2;

import com.afirez.dagger2.component.DaggerComponent;
import com.afirez.dagger2.demo.ActivityModule;
import com.afirez.dagger2.scope.AppScope;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by afirez on 18-1-4.
 */
@AppScope
@Component(dependencies = DaggerComponent.class, modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityModule.class
})
public interface AppComponent extends AndroidInjector<App> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<App>  {
        abstract Builder daggerComponent(DaggerComponent component);
    }
}
