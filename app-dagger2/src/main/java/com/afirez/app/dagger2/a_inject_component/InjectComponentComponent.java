package com.afirez.app.dagger2.a_inject_component;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Lazy;

@Singleton
@Component
public interface InjectComponentComponent {

    Lazy<User> userByLazy();

    User user();

    void inject(InjectComponentActivity activity);

}
