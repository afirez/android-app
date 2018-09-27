package com.afirez.app.dagger2.c_deep_insight_component;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AModule.class)
public interface AComponent {
    Map<String, Long> stringToLong();
    Map<Class<?>, String> classToString();
}
