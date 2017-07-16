package com.driveinto.phoenix;

import com.driveinto.phoenix.data.Customer;
import com.driveinto.phoenix.data.Login;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collection;

public interface IAuthorityService {
    void isRegistered(AuthorityCallback callback, IdpResponse response);

    boolean register(Login login, Customer customer);

    Collection<Customer> getRegistrationNoMentor(IdpResponse response);

    boolean setStudent(IdpResponse response, Customer customer);

    interface AuthorityCallback {
        void isRegistered(IdpResponse response, boolean result);
    }
}
