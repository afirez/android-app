package com.afirez.app.dagger2.a_inject_component;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class User {

    String name;

    @Inject
    public User() {
        this.name = "afirez";
    }
}
