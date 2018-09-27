package com.afirez.app.dagger2.b_module;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by lenovo on 2018/6/13.
 */
@Singleton
public class Person {
    @Inject
    Cloth mCloth;
    @Inject
    Fruit mFruit;

    @Inject
    public Person() {

    }
}
