package org.etma.main.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.etma.main.R;
import org.etma.main.db.EtmaUser;
import org.etma.main.events.SwitchToLoginFragmentEvent;
import org.etma.main.fragments.CreateAccountFragment;
import org.etma.main.fragments.LoginFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class CreateAccountActivity extends AppCompatActivity {

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

    private LoginFragment loginFragment;
    private CreateAccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_create_account);

        ButterKnife.bind(this);

        changeStatusBarColor();

        loginFragment = LoginFragment.newInstance();
        accountFragment = CreateAccountFragment.newInstance();

        setFragment(loginFragment, "login");
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

    @Subscribe
    public void onSwitchToLoginEvent(SwitchToLoginFragmentEvent event){

        if (event.isSwitchToLogin()){

            loginFragment = LoginFragment.newInstance();

            setFragment(loginFragment, "login");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setFragment(loginFragment, "login");
    }

    @Override
    public void onBackPressed() {

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            finish();
            System.exit(0);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.login)
    public void onLoginTouch(){
        setFragment(loginFragment, "login");
    }

    @OnClick(R.id.register)
    public void onRegisterTouch(){

        accountFragment = CreateAccountFragment.newInstance();

        setFragment(accountFragment, "register");
    }

    private void setFragment(android.support.v4.app.Fragment frag, String tag)
    {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(fragment == null)
        {
            ft.add(R.id.container, frag, tag);
        } else {
            ft.replace(R.id.container, frag, tag);
        }
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
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
