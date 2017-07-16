package com.driveinto.phoenix.di;

import android.app.Application;

import com.driveinto.phoenix.IAuthorityService;
import com.driveinto.phoenix.data.service.AuthorityService;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {
    private Application application;

//    public AuthModule(Application application) {
//        this.application = application;
//    }

    @Provides
    @Inject
    IAuthorityService providesIAuthorityService() {
        return new AuthorityService();
    }
}
