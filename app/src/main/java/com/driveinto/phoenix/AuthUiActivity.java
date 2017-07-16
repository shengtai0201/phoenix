package com.driveinto.phoenix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.driveinto.phoenix.di.PhoenixApplication;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class AuthUiActivity extends AppCompatActivity implements IAuthorityService.AuthorityCallback {
    @Inject
    IAuthorityService service;

    private static final String UNCHANGED_CONFIG_VALUE = "CHANGE-ME";
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String GOOGLE_PRIVACY_POLICY_URL = "https://www.google.com/policies/privacy/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";
    private static final int RC_SIGN_IN = 100;
    private CheckBox useGoogleProvider;
    private TextView googleScopesLabel;
    private CheckBox googleScopeDriveFile;
    private CheckBox googleScopeYoutubeData;
    private CheckBox useFacebookProvider;
    private TextView facebookScopesLabel;
    private CheckBox facebookScopeFriends;
    private CheckBox facebookScopePhotos;
    private CheckBox useTwitterProvider;
    private View rootView;
    private RadioButton firebaseLogo;
    private RadioButton googleLogo;
    private RadioButton useDefaultTheme;
    private RadioButton usePurpleTheme;
    private RadioButton useDarkTheme;
    private CheckBox useEmailProvider;
    private CheckBox usePhoneProvider;
    private RadioButton useGoogleTos;
    private CheckBox enableCredentialSelector;
    private CheckBox enableHintSelector;
    private RadioButton useGooglePrivacyPolicy;
    private CheckBox allowNewEmailAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((PhoenixApplication) getApplication()).getAuthComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_ui);

        useGoogleProvider = (CheckBox) findViewById(R.id.google_provider);
        googleScopesLabel = (TextView) findViewById(R.id.google_scopes_label);
        googleScopeDriveFile = (CheckBox) findViewById(R.id.google_scope_drive_file);
        googleScopeYoutubeData = (CheckBox) findViewById(R.id.google_scope_youtube_data);
        useFacebookProvider = (CheckBox) findViewById(R.id.facebook_provider);
        facebookScopesLabel = (TextView) findViewById(R.id.facebook_scopes_label);
        facebookScopeFriends = (CheckBox) findViewById(R.id.facebook_scope_friends);
        facebookScopePhotos = (CheckBox) findViewById(R.id.facebook_scope_photos);
        useTwitterProvider = (CheckBox) findViewById(R.id.twitter_provider);
        rootView = findViewById(R.id.root);
        firebaseLogo = (RadioButton) findViewById(R.id.firebase_logo);
        googleLogo = (RadioButton) findViewById(R.id.google_logo);
        useDefaultTheme = (RadioButton) findViewById(R.id.default_theme);
        usePurpleTheme = (RadioButton) findViewById(R.id.purple_theme);
        useDarkTheme = (RadioButton) findViewById(R.id.dark_theme);
        useEmailProvider = (CheckBox) findViewById(R.id.email_provider);
        usePhoneProvider = (CheckBox) findViewById(R.id.phone_provider);
        useGoogleTos = (RadioButton) findViewById(R.id.google_tos);
        enableCredentialSelector = (CheckBox) findViewById(R.id.credential_selector_enabled);
        enableHintSelector = (CheckBox) findViewById(R.id.hint_selector_enabled);
        useGooglePrivacyPolicy = (RadioButton) findViewById(R.id.google_privacy);
        allowNewEmailAccounts = (CheckBox) findViewById(R.id.allow_new_email_accounts);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
//            startSignedInActivity(null);
//            startRegisterActivity(null);
            this.service.isRegistered(this, null);
            finish();
            return;
        }

        if (!isGoogleConfigured()) {
            useGoogleProvider.setChecked(false);
            useGoogleProvider.setEnabled(false);
            useGoogleProvider.setText(R.string.google_label_missing_config);
            setGoogleScopesEnabled(false);
        } else {
            setGoogleScopesEnabled(useGoogleProvider.isChecked());
            useGoogleProvider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    setGoogleScopesEnabled(checked);
                }
            });
        }

        if (!isFacebookConfigured()) {
            useFacebookProvider.setChecked(false);
            useFacebookProvider.setEnabled(false);
            useFacebookProvider.setText(R.string.facebook_label_missing_config);
            setFacebookScopesEnabled(false);
        } else {
            setFacebookScopesEnabled(useFacebookProvider.isChecked());
            useFacebookProvider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    setFacebookScopesEnabled(checked);
                }
            });
        }

        if (!isTwitterConfigured()) {
            useTwitterProvider.setChecked(false);
            useTwitterProvider.setEnabled(false);
            useTwitterProvider.setText(R.string.twitter_label_missing_config);
        }

        if (!isGoogleConfigured() || !isFacebookConfigured() || !isTwitterConfigured()) {
            showSnackbar(R.string.configuration_required);
        }

        Button sign_in = (Button) findViewById(R.id.sign_in);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(view);
            }
        });
    }

    @MainThread
    private String getSelectedPrivacyPolicyUrl() {
        if (useGooglePrivacyPolicy.isChecked()) {
            return GOOGLE_PRIVACY_POLICY_URL;
        }

        return FIREBASE_PRIVACY_POLICY_URL;
    }

    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setLogo(getSelectedLogo())
                        .setAvailableProviders(getSelectedProviders())
                        .setTosUrl(getSelectedTosUrl())
                        .setPrivacyPolicyUrl(getSelectedPrivacyPolicyUrl())
                        .setIsSmartLockEnabled(enableCredentialSelector.isChecked(),
                                enableHintSelector.isChecked())
                        .setAllowNewEmailAccounts(allowNewEmailAccounts.isChecked())
                        .build(),
                RC_SIGN_IN);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
//            startSignedInActivity(response);
//            startRegisterActivity(response);
            this.service.isRegistered(this, response);
            finish();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    //    private void startSignedInActivity(IdpResponse response) {
//        startActivity(
//                SignedInActivity.createIntent(this, response, new SignedInActivity.SignedInConfig(
//                        getSelectedLogo(),
//                        getSelectedTheme(),
//                        getSelectedProviders(),
//                        getSelectedTosUrl(),
//                        enableCredentialSelector.isChecked(),
//                        enableHintSelector.isChecked())));
//    }

    @Override
    public void isRegistered(IdpResponse response, boolean result) {
        SignedInActivity.SignedInConfig config = new SignedInActivity.SignedInConfig(
                getSelectedLogo(),
                getSelectedTheme(),
                getSelectedProviders(),
                getSelectedTosUrl(),
                enableCredentialSelector.isChecked(),
                enableHintSelector.isChecked());

        if(result)
            startActivity(SignedInActivity.createIntent(this, response, config));
        else
            startActivity(RegisterActivity.createIntent(this, response, config));
    }

//    private void startRegisterActivity(IdpResponse response) {
//        this.service.isRegistered(this, response);
//    }

    @MainThread
    private boolean isGoogleConfigured() {
        return !UNCHANGED_CONFIG_VALUE.equals(getString(R.string.default_web_client_id));
    }

    @MainThread
    private void setGoogleScopesEnabled(boolean enabled) {
        googleScopesLabel.setEnabled(enabled);
        googleScopeDriveFile.setEnabled(enabled);
        googleScopeYoutubeData.setEnabled(enabled);
    }

    @MainThread
    private boolean isFacebookConfigured() {
        return !UNCHANGED_CONFIG_VALUE.equals(getString(R.string.facebook_application_id));
    }

    @MainThread
    private void setFacebookScopesEnabled(boolean enabled) {
        facebookScopesLabel.setEnabled(enabled);
        facebookScopeFriends.setEnabled(enabled);
        facebookScopePhotos.setEnabled(enabled);
    }

    @MainThread
    private boolean isTwitterConfigured() {
        List<String> twitterConfigs = Arrays.asList(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret)
        );

        return !twitterConfigs.contains(UNCHANGED_CONFIG_VALUE);
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @MainThread
    @DrawableRes
    private int getSelectedLogo() {
        if (firebaseLogo.isChecked()) {
            return R.drawable.firebase_auth_120dp;
        } else if (googleLogo.isChecked()) {
            return R.drawable.logo_googleg_color_144dp;
        }
        return AuthUI.NO_LOGO;
    }

    @MainThread
    @StyleRes
    private int getSelectedTheme() {
        if (useDefaultTheme.isChecked()) {
            return AuthUI.getDefaultTheme();
        }

        if (usePurpleTheme.isChecked()) {
            return R.style.PurpleTheme;
        }

        if (useDarkTheme.isChecked()) {
            return R.style.DarkTheme;
        }

        return R.style.GreenTheme;
    }

    @MainThread
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        if (useGoogleProvider.isChecked()) {
            selectedProviders.add(
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                            .setPermissions(getGooglePermissions())
                            .build());
        }

        if (useFacebookProvider.isChecked()) {
            selectedProviders.add(
                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                            .setPermissions(getFacebookPermissions())
                            .build());
        }

        if (useTwitterProvider.isChecked()) {
            selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
        }

        if (useEmailProvider.isChecked()) {
            selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
        }

        if (usePhoneProvider.isChecked()) {
            selectedProviders.add(
                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build());
        }

        return selectedProviders;
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
        if (googleScopeYoutubeData.isChecked()) {
            result.add("https://www.googleapis.com/auth/youtube.readonly");
        }
        if (googleScopeDriveFile.isChecked()) {
            result.add(Scopes.DRIVE_FILE);
        }
        return result;
    }

    @MainThread
    private List<String> getFacebookPermissions() {
        List<String> result = new ArrayList<>();
        if (facebookScopeFriends.isChecked()) {
            result.add("user_friends");
        }
        if (facebookScopePhotos.isChecked()) {
            result.add("user_photos");
        }
        return result;
    }

    @MainThread
    private String getSelectedTosUrl() {
        if (useGoogleTos.isChecked()) {
            return GOOGLE_TOS_URL;
        }

        return FIREBASE_TOS_URL;
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthUiActivity.class);
        return in;
    }
}
