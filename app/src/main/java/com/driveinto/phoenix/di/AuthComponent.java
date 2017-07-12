package com.driveinto.phoenix.di;

import com.driveinto.phoenix.AuthUiActivity;
import com.driveinto.phoenix.SignedInActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, AuthModule.class})
public interface AuthComponent {
    void inject(SignedInActivity activity);

    void inject(AuthUiActivity activity);
}
