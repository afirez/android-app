package com.afirez.app.dagger2.c_deep_insight_component;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.Multibinds;
import dagger.multibindings.StringKey;

@Module
public abstract class AModule {

    @Provides
    @IntoMap
    @StringKey("foo")
    static Long provideFooValue() {
        return 100L;
    }

    @Provides
    @IntoMap
    @ClassKey(A.class)
    static String provideAValue(){
        return A.class.getName();
    }

    @Provides
    @IntoMap
    @ClassKey(B.class)
    static String provideBValue(){
        return B.class.getName();
    }

    @Multibinds
    abstract Map<Class<?>, String> bindingClassToString();
}
