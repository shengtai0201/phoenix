package com.driveinto.phoenix.data.service;

import com.driveinto.phoenix.IAuthorityService;
import com.driveinto.phoenix.data.Customer;
import com.driveinto.phoenix.data.Login;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;

public class AuthorityService implements IAuthorityService {
    private DatabaseReference logins;
    private DatabaseReference customers;
    private DatabaseReference records;

    public AuthorityService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.logins = database.getReference("logins");
        this.customers = database.getReference("customers");
        this.records = database.getReference("records");
    }

    @Override
    public void isRegistered(final AuthUiCallback callback, final IdpResponse response) {
        if(response == null)
            callback.isRegistered(null, true);
        else{
            String indexProviderTypeToken = response.getProviderType() + response.getIdpToken();
            Query query = this.logins.orderByChild("indexProviderTypeToken").equalTo(indexProviderTypeToken);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Login login = dataSnapshot.getValue(Login.class);
                    callback.isRegistered(response, login != null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void register(final RegisterCallback callback, final Login login, Customer customer) {
        final String customerId = this.customers.push().getKey();
        this.customers.child(customerId).setValue(customer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    String loginId = logins.push().getKey();
                    login.setCustomerId(customerId);
                    logins.child(loginId).setValue(login, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            callback.register(databaseError == null);
                        }
                    });
                }
            }
        });
    }

    @Override
    public Collection<Customer> getRegistrationNoMentor(IdpResponse idpResponse) {
        return null;
    }

    @Override
    public boolean setStudent(IdpResponse idpResponse, Customer customer) {
        return false;
    }
}
