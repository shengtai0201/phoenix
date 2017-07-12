package com.driveinto.phoenix.di;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class PhoenixApplication extends Application {
    private AuthComponent authComponent;
    private CustomerComponent customerComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);

        ApplicationModule applicationModule = new ApplicationModule(this);

        authComponent = DaggerAuthComponent.builder()
                .applicationModule(applicationModule)
                .authModule(new AuthModule())
                .build();

        customerComponent = DaggerCustomerComponent.builder()
                .applicationModule(applicationModule)
                .customerModule(new CustomerModule())
                .build();
    }

    public AuthComponent getAuthComponent() {
        return authComponent;
    }

    public CustomerComponent getCustomerComponent() {
        return customerComponent;
    }
}
