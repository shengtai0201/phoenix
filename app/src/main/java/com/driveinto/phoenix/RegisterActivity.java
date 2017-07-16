package com.driveinto.phoenix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.driveinto.phoenix.di.PhoenixApplication;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ExtraConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private IdpResponse idpResponse;
    private SignedInActivity.SignedInConfig signedInConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((PhoenixApplication) getApplication()).getAuthComponent().inject(this);
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthUiActivity.createIntent(this));
            finish();
            return;
        }

        idpResponse = IdpResponse.fromResultIntent(getIntent());
        signedInConfig = getIntent().getParcelableExtra(SignedInActivity.EXTRA_SIGNED_IN_CONFIG);

        setContentView(R.layout.activity_register);
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse,
                                      SignedInActivity.SignedInConfig signedInConfig) {
        Intent startIntent = idpResponse == null ? new Intent() :
                (new Intent().putExtra(ExtraConstants.EXTRA_IDP_RESPONSE, idpResponse));

        return startIntent.setClass(context, RegisterActivity.class)
                .putExtra(SignedInActivity.EXTRA_SIGNED_IN_CONFIG, signedInConfig);
    }
}
