package com.afirez.app.dagger2.b_module;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lenovo on 2018/6/13.
 */
@Module
public class ModuleModule {

    @Provides
    Cloth provideCloth(){
        return new Cloth();
    }

    @Provides
    Fruit provideFruit(){
        return new Fruit();
    }
}
