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
import android.support.v4.app.NotificationCompat;
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
import org.etma.main.asyncs.SendPasswordResetAsync;
import org.etma.main.asyncs.SendPasswordResetCodeAsync;
import org.etma.main.events.PasswordResetCompletedEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.ResetPassword;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class PasswordResetActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 20;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

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

    @BindView(R.id.user_id_wrapper)
    TextInputLayout userIdWrapper;

    @BindView(R.id.user_id)
    EditText userId;

    @BindView(R.id.reset_code_wrapper)
    TextInputLayout resetCodeWrapper;

    @BindView(R.id.reset_code)
    EditText resetCode;

    @BindView(R.id.password_wrapper)
    TextInputLayout passwordWrapper;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.confirm_password_wrapper)
    TextInputLayout confirmPassWrapper;

    @BindView(R.id.confirm_password)
    EditText confirmPassword;

    private NetworkResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_password_reset);

        ButterKnife.bind(this);

        changeStatusBarColor();

        realm = Realm.getDefaultInstance();

        mView = new CatLoadingView();

        ButterKnife.bind(this);

        resolver = new NetworkResolver(this);

    }

    @OnClick(R.id.reset)
    public void onResetClicked(){

        //validation step;

        String id = userId.getText().toString().trim();
        String code = resetCode.getText().toString().trim();
        String pass1 = password.getText().toString().trim();
        String confirm = confirmPassword.getText().toString().trim();

        if (id.equals("")){
            userIdWrapper.setErrorEnabled(true);
            userIdWrapper.setError("User id field is required (check email)");
        }else if (code.equals("")){
            resetCodeWrapper.setErrorEnabled(true);
            resetCodeWrapper.setError("Reset code is required (check email)");
        }else if (pass1.equals("") || !Util.isValidPassword(pass1)){
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError("Enter a valid password. (at least 5 characters)");
        }else if (!pass1.equals(confirm)){
            confirmPassWrapper.setErrorEnabled(true);
            confirmPassWrapper.setError("Password does not match the first!");
        }else{

            if (resolver.isConnected()){
                String json = prepareJson(id, code, pass1);

                SendPasswordResetAsync async = new SendPasswordResetAsync(this);

                async.execute( json );

                if (!mView.isAdded()){
                    showDialog();
                }
            }else{
                Util.showSnackBar(this, mContentView, "Please switch ON your mobile data or use Wi-Fi");
            }

        }
    }

    @OnClick(R.id.login)
    public void onLogin(){

        startActivity( new Intent(this, CreateAccountActivity.class));
    }

    private String prepareJson(String id, String code, String password){

        GsonBuilder builder = new GsonBuilder();

        builder.serializeNulls();

        Gson gson = builder.create();

        ResetPassword resetPassword = new ResetPassword();

        resetPassword.setC("");
        resetPassword.setUserId(Integer.parseInt(id));
        resetPassword.setPassword(password);
        resetPassword.setResetCode(code);
        resetPassword.setReturnUrl("");
        resetPassword.setSingleSignIn("true");

        String json = gson.toJson(resetPassword);

        return json;

    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    @Subscribe
    public void onEvent(PasswordResetCompletedEvent event){

        if (event.isSuccess()){

            //go to login screen;
            startActivity( new Intent(this, CreateAccountActivity.class));
        }else{
            Util.showSnackBar(this, mContentView, "Password reset failed. Try again!");
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
