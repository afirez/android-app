package com.afirez.app.dagger2.b_module;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by lenovo on 2018/6/13.
 */
@Singleton
@Component(modules = {ModuleModule.class})
public interface ModuleComponent {
    void inject(ModuleActivity activity);
}
