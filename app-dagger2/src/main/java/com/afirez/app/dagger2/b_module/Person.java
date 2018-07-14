package com.afirez.app.dagger2.b_module;

import javax.inject.Inject;

/**
 * Created by lenovo on 2018/6/13.
 */

public class Person {
    @Inject
    Cloth mCloth;
    @Inject
    Fruit mFruit;

    @Inject
    public Person() {

    }
}
