package com.driveinto.phoenix;

import com.driveinto.phoenix.data.Customer;
import com.driveinto.phoenix.data.Login;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collection;

public interface IAuthorityService {
    void isRegistered(AuthUiCallback callback, IdpResponse response);

    void register(RegisterCallback callback, Login login, Customer customer);

    Collection<Customer> getRegistrationNoMentor(IdpResponse response);

    boolean setStudent(IdpResponse response, Customer customer);

    interface AuthUiCallback {
        void isRegistered(IdpResponse response, boolean result);
    }

    interface RegisterCallback {
        void register(boolean result);
    }
}
