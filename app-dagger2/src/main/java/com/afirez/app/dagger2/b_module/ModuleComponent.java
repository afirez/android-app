package com.afirez.app.dagger2.b_module;

import dagger.Component;

/**
 * Created by lenovo on 2018/6/13.
 */

@Component(modules = {ModuleModule.class})
public interface ModuleComponent {
    void inject(ModuleActivity activity);
}
