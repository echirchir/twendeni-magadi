package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.UpdateUserProfileAsync;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.UserOauth;
import org.etma.main.events.UpdateUserProfileEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.UpdateUserProfile;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.full_name_wrapper)
    TextInputLayout fullNameWrapper;

    @BindView(R.id.full_name)
    EditText fullName;

    @BindView(R.id.cell_phone_wrapper)
    TextInputLayout cellPhoneWrapper;

    @BindView(R.id.cell_phone)
    org.etma.main.custom.PrefixEditText cellPhone;

    @BindView(R.id.email_address_wrapper)
    TextInputLayout emailAddressWrapper;

    @BindView(R.id.email_address)
    EditText emailAddress;

    private EtmaUser user;

    private NetworkResolver resolver;
    private CatLoadingView mView;

    private Realm realm;

    private UserOauth userOauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        userOauth = realm.where(UserOauth.class).findFirst();

        user = realm.where(EtmaUser.class).findFirst();

        resolver = new NetworkResolver(this);

        mView = new CatLoadingView();

        fullName.setText( user.getFull_name() );
        emailAddress.setText( user.getEmail_address() );
        cellPhone.setText(user.getCell_phone().replaceFirst("254", ""));

    }

    @OnClick(R.id.update)
    public void onUpdateProfile(){

        String name = fullName.getText().toString().trim();
        String phone = cellPhone.getText().toString().trim();
        String email = emailAddress.getText().toString().trim();

        if (name.equals("")){
            fullNameWrapper.setErrorEnabled(true);
            fullNameWrapper.setError("Enter a valid full name ");
        }else if (phone.equals("") || !Util.isValidMobile(phone)){
            cellPhoneWrapper.setError("Enter a valid phone number (254)");
        }else if (email.equals("") || !Util.isValidEmail(email)){
            emailAddressWrapper.setErrorEnabled(true);
            emailAddressWrapper.setError("Invalid email address (e.g john@gmai.com)");
        }else{

            NetworkResolver resolver = new NetworkResolver(this);

            if (resolver.isConnected()){

                String json = prepareJsonForUserUpdate(name, phone, email);

                Util.prettyPrintJson(json);

                UpdateUserProfileAsync async = new UpdateUserProfileAsync(this);
                async.execute(json);

                showDialog();

                }else{

                Util.showSnackBar(this, coordinatorLayout, "Please switch ON your mobile data or use Wi-Fi");
            }

        }

    }

    private String prepareJsonForUserUpdate(String name, String phone, String email){

        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();

        Gson gson = builder.create();

        UpdateUserProfile updateUserProfile = new UpdateUserProfile();

        updateUserProfile.setEmailAddress(email);
        updateUserProfile.setName(name);
        updateUserProfile.setPhoneNumber("254" + phone);
        updateUserProfile.setUserId(String.valueOf(userOauth.getUserId()));
        updateUserProfile.setUserName("254" + phone);
        updateUserProfile.setSurname(name);

        return gson.toJson(updateUserProfile);

    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    @Subscribe
    public void event(UpdateUserProfileEvent event){

        mView.dismiss();

        if (event.isSuccess()){
            //update local user profile;

            final String name = fullName.getText().toString().trim();
            final String phone = cellPhone.getText().toString().trim();
            final String email = emailAddress.getText().toString().trim();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {

                    user.setFull_name(name);
                    user.setEmail_address(email);
                    user.setCell_phone(phone);
                    user.setCan_login(true);
                    realm.copyToRealmOrUpdate(user);
                }
            });

            startActivity( new Intent(this, ProfileActivity.class));

        }else{
            Util.showSnackBar(this, coordinatorLayout, "Failed to update profile. Try again later!");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.chatting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }


}
