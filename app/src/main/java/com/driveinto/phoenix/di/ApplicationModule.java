package com.driveinto.phoenix.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Application application;

    public ApplicationModule(Application application){
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication(){
        return this.application;
    }

    @Provides
    @Inject
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}
