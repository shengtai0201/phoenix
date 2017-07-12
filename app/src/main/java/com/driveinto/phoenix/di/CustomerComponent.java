package com.driveinto.phoenix.di;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={ApplicationModule.class, CustomerModule.class})
public interface CustomerComponent {

}
