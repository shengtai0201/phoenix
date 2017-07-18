package com.driveinto.phoenix;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.driveinto.phoenix.data.Customer;
import com.driveinto.phoenix.data.Login;
import com.driveinto.phoenix.di.PhoenixApplication;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ExtraConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class RegisterActivity extends AppCompatActivity implements IAuthorityService.RegisterCallback {
    @Inject
    IAuthorityService service;

    private IdpResponse idpResponse;
    private SignedInActivity.SignedInConfig signedInConfig;

    private EditText name;
    private EditText officePhone;
    private EditText homePhone;
    private EditText mobilePhone;
    private EditText address;
    private EditText email;
    private EditText birthday;
    private EditText height;
    private EditText weight;
    private EditText career;
    private Button ok;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((PhoenixApplication) getApplication()).getAuthComponent().inject(this);
        super.onCreate(savedInstanceState);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthUiActivity.createIntent(this));
            finish();
            return;
        }

        idpResponse = IdpResponse.fromResultIntent(getIntent());
        signedInConfig = getIntent().getParcelableExtra(SignedInActivity.EXTRA_SIGNED_IN_CONFIG);

        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name);
        officePhone = (EditText) findViewById(R.id.officePhone);
        homePhone = (EditText) findViewById(R.id.homePhone);
        mobilePhone = (EditText) findViewById(R.id.mobilePhone);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        birthday = (EditText) findViewById(R.id.birthday);
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        career = (EditText) findViewById(R.id.career);
        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login login = new Login();
                login.setProviderType(idpResponse.getProviderType());
                login.setToken(idpResponse.getIdpToken());
                login.setIndexProviderTypeToken(idpResponse.getProviderType() + idpResponse.getIdpToken());

                Customer customer = new Customer();
                if (!name.getText().toString().isEmpty())
                    customer.setName(name.getText().toString());
                if (!officePhone.getText().toString().isEmpty())
                    customer.setOfficePhone(officePhone.getText().toString());
                if (!homePhone.getText().toString().isEmpty())
                    customer.setHomePhone(homePhone.getText().toString());
                if (!mobilePhone.getText().toString().isEmpty())
                    customer.setMobilePhone(mobilePhone.getText().toString());
                if (!address.getText().toString().isEmpty())
                    customer.setAddress(address.getText().toString());
                if (!email.getText().toString().isEmpty())
                    customer.seteMail(email.getText().toString());

                if (!birthday.getText().toString().isEmpty()) {
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date = format.parse(birthday.getText().toString());
                        customer.setBirthday(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (!height.getText().toString().isEmpty())
                    customer.setHeight(Integer.parseInt(height.getText().toString()));
                if (!weight.getText().toString().isEmpty())
                    customer.setWeight(Integer.parseInt(weight.getText().toString()));
                if (!career.getText().toString().isEmpty())
                    customer.setCareer(career.getText().toString());

                dialog = ProgressDialog.show(RegisterActivity.this, "title", "message", true);
                service.register(RegisterActivity.this, login, customer);
            }
        });
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse,
                                      SignedInActivity.SignedInConfig signedInConfig) {
        Intent startIntent = idpResponse == null ? new Intent() :
                (new Intent().putExtra(ExtraConstants.EXTRA_IDP_RESPONSE, idpResponse));

        return startIntent.setClass(context, RegisterActivity.class)
                .putExtra(SignedInActivity.EXTRA_SIGNED_IN_CONFIG, signedInConfig);
    }

    @Override
    public void register(boolean result) {
        dialog.dismiss();

        if (result) {
            Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show();
            startActivity(SignedInActivity.createIntent(this, this.idpResponse, this.signedInConfig));
            finish();
        } else
            Toast.makeText(this, "註冊失敗", Toast.LENGTH_LONG).show();
    }
}
