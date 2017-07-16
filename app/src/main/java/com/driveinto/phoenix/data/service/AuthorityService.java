package com.driveinto.phoenix.data.service;

import com.driveinto.phoenix.IAuthorityService;
import com.driveinto.phoenix.data.Customer;
import com.driveinto.phoenix.data.Login;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    public void isRegistered(final AuthorityCallback callback, final IdpResponse response) {
        if(response == null)
            callback.isRegistered(null, true);
        else{
            String indexProviderTypeToken = response.getProviderType() + response.getIdpToken();
            Query query = this.logins.orderByChild("indexProviderTypeToken").equalTo(indexProviderTypeToken);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    Query innerQuery = dataSnapshot.getRef().orderByChild("token").equalTo(response.getIdpToken());
//
//                    Login login = dataSnapshot.getValue(Login.class);
//                    callback.isRegistered(response, login != null);
                    String test = s;
                    String test2 = dataSnapshot.toString();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean register(Login login, Customer customer) {
        return false;
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
