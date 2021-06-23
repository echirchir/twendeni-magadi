package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.ChangePasswordAsync;
import org.etma.main.asyncs.SendPasswordResetAsync;
import org.etma.main.events.PasswordChangedEvent;
import org.etma.main.events.PasswordResetEmailSentEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.CreateChangePassword;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.old_password_wrapper)
    TextInputLayout oldPasswordWrapper;

    @BindView(R.id.old_password)
    EditText oldPassword;

    @BindView(R.id.new_password_wrapper)
    TextInputLayout newPasswordWrapper;

    @BindView(R.id.new_password)
    EditText newPassword;

    @BindView(R.id.confirm_password_wrapper)
    TextInputLayout confirmPasswordWrapper;

    @BindView(R.id.confirm_password)
    EditText confirmPassword;

    private CatLoadingView mView;

    private NetworkResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        mView = new CatLoadingView();

        resolver = new NetworkResolver(this);

    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    @Subscribe
    public void onEvent(PasswordChangedEvent event){

        if (mView.isVisible()){
            mView.dismiss();
        }

        if (event.isSuccess()){
            startActivity( new Intent(this, CreateAccountActivity.class));
        }else{
            Util.showSnackBar(this, mCoordinatorLayout, "Something went wrong, please try again!");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.change)
    public void onChangePassword(){

        String old = oldPassword.getText().toString().trim();
        String pass1 = newPassword.getText().toString().trim();
        String pass2 = confirmPassword.getText().toString().trim();

        if (old.equals("")){
            oldPasswordWrapper.setErrorEnabled(true);
            oldPasswordWrapper.setError("Old password field is required");
        }else if (pass1.equals("") || !Util.isValidPassword(pass1)){
            newPasswordWrapper.setErrorEnabled(true);
            newPasswordWrapper.setError("Enter a valid password (at least 6 characters.");
        }else if (!pass1.equals(pass2)){
            confirmPasswordWrapper.setErrorEnabled(true);
            confirmPasswordWrapper.setError("The two passwords do not match!");
        }else{
            if (resolver.isConnected()){
                String json = prepareJson(old, pass1);


                ChangePasswordAsync async = new ChangePasswordAsync(this);

                async.execute( json );

                if (!mView.isAdded()){
                    showDialog();
                }
            }else{
                Util.showSnackBar(this, mCoordinatorLayout, "Please switch ON your mobile data or use Wi-Fi");
            }

        }
    }

    private String prepareJson (String old, String newPassword){

        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();

        Gson gson = builder.create();

        CreateChangePassword changePassword = new CreateChangePassword();

        changePassword.setCurrentPassword(old);

        changePassword.setNewPassword(newPassword);

        return gson.toJson(changePassword);

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

}
