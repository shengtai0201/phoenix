package com.driveinto.phoenix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.driveinto.phoenix.di.PhoenixApplication;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ExtraConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SignedInActivity extends AppCompatActivity {

    static final class SignedInConfig implements Parcelable {
        int logo;
        int theme;
        List<AuthUI.IdpConfig> providerInfo;
        String tosUrl;
        boolean isCredentialSelectorEnabled;
        boolean isHintSelectorEnabled;

        SignedInConfig(int logo,
                       int theme,
                       List<AuthUI.IdpConfig> providerInfo,
                       String tosUrl,
                       boolean isCredentialSelectorEnabled,
                       boolean isHintSelectorEnabled) {
            this.logo = logo;
            this.theme = theme;
            this.providerInfo = providerInfo;
            this.tosUrl = tosUrl;
            this.isCredentialSelectorEnabled = isCredentialSelectorEnabled;
            this.isHintSelectorEnabled = isHintSelectorEnabled;
        }

        SignedInConfig(Parcel in) {
            logo = in.readInt();
            theme = in.readInt();
            providerInfo = new ArrayList<>();
            in.readList(providerInfo, AuthUI.IdpConfig.class.getClassLoader());
            tosUrl = in.readString();
            isCredentialSelectorEnabled = in.readInt() != 0;
            isHintSelectorEnabled = in.readInt() != 0;
        }

        public static final Creator<SignedInConfig> CREATOR = new Creator<SignedInConfig>() {
            @Override
            public SignedInConfig createFromParcel(Parcel in) {
                return new SignedInConfig(in);
            }

            @Override
            public SignedInConfig[] newArray(int size) {
                return new SignedInConfig[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(logo);
            dest.writeInt(theme);
            dest.writeList(providerInfo);
            dest.writeString(tosUrl);
            dest.writeInt(isCredentialSelectorEnabled ? 1 : 0);
            dest.writeInt(isHintSelectorEnabled ? 1 : 0);
        }
    }

    private static final String EXTRA_SIGNED_IN_CONFIG = "extra_signed_in_config";
    private IdpResponse idpResponse;
    private SignedInConfig signedInConfig;
    private View rootView;
    private TextView userEmail;
    private TextView userPhoneNumber;
    private TextView userDisplayName;
    private TextView enabledProviders;

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
        signedInConfig = getIntent().getParcelableExtra(EXTRA_SIGNED_IN_CONFIG);

        setContentView(R.layout.activity_signed_in);
        //rootView = findViewById(R.id.content);
        rootView = findViewById(R.id.root);
        userEmail = (TextView) findViewById(R.id.user_email);
        userPhoneNumber = (TextView) findViewById(R.id.user_phone_number);
        userDisplayName = (TextView) findViewById(R.id.user_display_name);
        enabledProviders = (TextView) findViewById(R.id.user_enabled_providers);

        populateProfile();
        populateIdpToken();

        Button sign_out = (Button) findViewById(R.id.sign_out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        Button delete_account = (Button) findViewById(R.id.delete_account);
        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccountClicked();
            }
        });
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(AuthUiActivity.createIntent(SignedInActivity.this));
                            finish();
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(AuthUiActivity.createIntent(SignedInActivity.this));
                            finish();
                        } else {
                            showSnackbar(R.string.delete_account_failed);
                        }
                    }
                });
    }

    public void deleteAccountClicked() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .create();

        dialog.show();
    }

    @MainThread
    private void populateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
//            Glide.with(this)
//                    .load(user.getPhotoUrl())
//                    .fitCenter()
//                    .into(mUserProfilePicture);
        }

        userEmail.setText(TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        userPhoneNumber.setText(TextUtils.isEmpty(user.getPhoneNumber()) ? "No phone number" : user.getPhoneNumber());
        userDisplayName.setText(TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());

        StringBuilder providerList = new StringBuilder(100);

        providerList.append("Providers used: ");

        if (user.getProviders() == null || user.getProviders().isEmpty()) {
            providerList.append("none");
        } else {
            Iterator<String> providerIter = user.getProviders().iterator();
            while (providerIter.hasNext()) {
                String provider = providerIter.next();
                if (GoogleAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Google");
                } else if (FacebookAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Facebook");
                } else if (EmailAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Password");
                } else {
                    providerList.append(provider);
                }

                if (providerIter.hasNext()) {
                    providerList.append(", ");
                }
            }
        }

        enabledProviders.setText(providerList);
    }

    private void populateIdpToken() {
        String token = null;
        String secret = null;
        if (idpResponse != null) {
            token = idpResponse.getIdpToken();
            secret = idpResponse.getIdpSecret();
        }
        View idpTokenLayout = findViewById(R.id.idp_token_layout);
        if (token == null) {
            idpTokenLayout.setVisibility(View.GONE);
        } else {
            idpTokenLayout.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.idp_token)).setText(token);
        }
        View idpSecretLayout = findViewById(R.id.idp_secret_layout);
        if (secret == null) {
            idpSecretLayout.setVisibility(View.GONE);
        } else {
            idpSecretLayout.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.idp_secret)).setText(secret);
        }
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse,
                                      SignedInConfig signedInConfig) {
        //Intent startIntent = idpResponse == null ? new Intent() : idpResponse.toIntent();
        Intent startIntent = idpResponse == null ? new Intent() :
                (new Intent().putExtra(ExtraConstants.EXTRA_IDP_RESPONSE, idpResponse));

        return startIntent.setClass(context, SignedInActivity.class)
                .putExtra(EXTRA_SIGNED_IN_CONFIG, signedInConfig);
    }
}
