package org.etma.main.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.SendPasswordResetCodeAsync;
import org.etma.main.db.EtmaUser;
import org.etma.main.events.PasswordResetEmailSentEvent;
import org.etma.main.fragments.CreateAccountFragment;
import org.etma.main.fragments.LoginFragment;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.GetPasswordResetCode;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class EnterEmailAddressActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 20;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @BindView(R.id.email_address_wrapper)
    TextInputLayout emailAddressWrapper;

    @BindView(R.id.email_address)
    EditText emailAddress;

    private NetworkResolver resolver;

    @BindView(R.id.fullscreen_content)
    View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private Realm realm;

    private CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_enter_email_address);

        ButterKnife.bind(this);

        changeStatusBarColor();

        realm = Realm.getDefaultInstance();

        mView = new CatLoadingView();

        resolver = new NetworkResolver(this);


        ButterKnife.bind(this);
    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    @OnClick(R.id.send)
    public void onSendAction(){

        //validate email and submit to server;

        String email = emailAddress.getText().toString().trim();

        EtmaUser user = realm.where(EtmaUser.class).findFirst();

        if (email.equals("")){
            emailAddressWrapper.setErrorEnabled(true);
            emailAddressWrapper.setError("Email field is required!");
        }else if (!Util.isValidEmail(email)){
            emailAddressWrapper.setErrorEnabled(true);
            emailAddressWrapper.setError("Enter a valid email address!");
        }else if (!user.getEmail_address().equalsIgnoreCase(email)){
            emailAddressWrapper.setErrorEnabled(true);
            emailAddressWrapper.setError("You entered the wrong email address!");
        }else{

            if (resolver.isConnected()){
                String payload = prepareJson(email);

                SendPasswordResetCodeAsync async = new SendPasswordResetCodeAsync(this);

                async.execute( payload );

                if (!mView.isAdded()){
                    showDialog();
                }
            }else{
                Util.showSnackBar(this, mContentView, "Please switch ON your mobile data or use Wi-Fi");
            }


        }
    }

    private String prepareJson(String email){

        GetPasswordResetCode passwordResetCode = new GetPasswordResetCode();
        passwordResetCode.setEmailAddress(email);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();

        Gson gson = builder.create();

        String json = gson.toJson(passwordResetCode);

        Util.prettyPrintJson(json);

        return json;
    }

    @OnClick(R.id.login)
    public void onLogin(){

        startActivity( new Intent(this, CreateAccountActivity.class));
    }

    @OnClick(R.id.have_reset_code)
    public void onHaveResetCode(){

        startActivity( new Intent(this, PasswordResetActivity.class));
    }

    @Subscribe
    public void onEvent(PasswordResetEmailSentEvent event){

        if (mView.isVisible()){
            mView.dismiss();
        }

        if (event.isSuccess()){
            startActivity( new Intent(this, PasswordResetActivity.class));
        }else{
            Util.showSnackBar(this, mContentView, "Something went wrong, please try again!");
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

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(1);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
