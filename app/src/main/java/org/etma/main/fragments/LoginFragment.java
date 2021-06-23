package org.etma.main.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.activities.EndTimeMessageMainActivity;
import org.etma.main.activities.EnterEmailAddressActivity;
import org.etma.main.asyncs.AuthenticateAsync;
import org.etma.main.asyncs.RequestUserInformation;
import org.etma.main.asyncs.SendSupportEmailAsync;
import org.etma.main.custom.PrefixEditText;
import org.etma.main.db.Amortization;
import org.etma.main.db.Contribution;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.FlexiAmortization;
import org.etma.main.db.Member;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.MemberRelationship;
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.PaymentMode;
import org.etma.main.db.PaymentPeriod;
import org.etma.main.db.PledgeAmortization;
import org.etma.main.db.PledgeStake;
import org.etma.main.db.Project;
import org.etma.main.events.LoginSuccessEvent;
import org.etma.main.events.SupportEmailSentEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.Authenticate;
import org.etma.main.services.InitialSetupService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.os.Build.SERIAL;

public class LoginFragment extends Fragment {

    @BindView(R.id.user_name_wrapper)
    TextInputLayout userNameWrapper;

    @BindView(R.id.user_name)
    PrefixEditText userName;

    @BindView(R.id.password_wrapper)
    TextInputLayout passwordWrapper;

    @BindView(R.id.password)
    EditText password;

    private CatLoadingView mView;

    private Realm realm;

    private NetworkResolver resolver;

    private EditText emailAddress;
    private TextInputLayout emailAddressWrapper;
    private EditText cellPhone;
    private TextInputLayout cellPhoneWrapper;
    private EditText description;
    private TextInputLayout descriptionWrapper;

    private String osVersion;
    private int sdkVersion;
    private String osDevice;
    private String osModel;
    private String osProduct;
    private String serviceName;
    private String IMSI;
    private String productBoard;
    private String productBrand;
    private String deviceId;
    private String productManufacturer;
    private String productModel;
    private String serialNumber;
    private String lcdDisplay;
    private String productCpu;
    private int openGLVersion;

    private final int PHONE_STATE_PERMISSION_REQUEST_CODE = 100;
    private String IMEI;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        resolver = new NetworkResolver(getActivity());

        acquireDeviceInformation();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, view);

        mView = new CatLoadingView();

        EtmaUser userOauth = realm.where(EtmaUser.class).findFirst();

        if (userOauth != null) {
            //set username
            userName.setText(userOauth.getCell_phone().replaceFirst("254", ""));
        }

        return view;

    }

    @OnClick(R.id.login)
    public void onLoginTouchEvent() {

        String username = userName.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();

        if (username.equals("")) {
            userNameWrapper.setErrorEnabled(true);
            userNameWrapper.setError("Enter a valid phone number!");
        } else if (passwordValue.equals("")) {
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError("Enter correct password!");
        } else {

            if (resolver.isConnected()) {
                doRemoteLogin(username, passwordValue);
            } else {
                Util.showSnackBar(getActivity(), getView(), "Please switch ON your mobile data or use Wi-Fi");
            }
        }
    }

    @OnClick(R.id.forgot_password)
    public void onForgotPasswordTouch() {

        if (resolver.isConnected()) {
            startActivity(new Intent(getActivity(), EnterEmailAddressActivity.class));
        } else {
            Util.showSnackBar(getActivity(), getView(), "Please switch ON your mobile data or use Wi-Fi");
        }

    }

    @OnClick(R.id.support)
    public void onSupportAction() {
        //pop up a dialog form;
        if (resolver.isConnected()){
            showSupportDialog();
        }else{
            Util.showSnackBar(getActivity(), getView(), "Turn on your 4G data or connect to a Wi-Fi!");
        }
    }

    @OnClick(R.id.donate)
    public void onDonateAction() {

        //pop up mpesa app;
    }

    private void doRemoteLogin(String username, String password) {

        Authenticate authenticate = new Authenticate();

        authenticate.setUserNameOrEmailAddress("254" + username);
        authenticate.setPassword(password);
        authenticate.setTwoFactorVerificationCode("");
        authenticate.setRememberClient("true");
        authenticate.setTwoFactorRememberClientToken("");
        authenticate.setSingleSignIn("");
        authenticate.setReturnUrl("something");

        Gson gson = new Gson();

        String json = gson.toJson(authenticate);

        AuthenticateAsync authenticateAsync = new AuthenticateAsync(getActivity());

        authenticateAsync.execute(json);

        mView = new CatLoadingView();

        showDialog();

    }

    private void showSupportDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setCancelable(true);

        View customTitle = getLayoutInflater().inflate(R.layout.custom_title, null);
        TextView title = (TextView) customTitle.findViewById(R.id.title);
        title.setText(getString(R.string.get_support_title));

        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.contact_support_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCustomTitle(customTitle);

        emailAddressWrapper = (TextInputLayout) dialogView.findViewById(R.id.email_address_wrapper);
        cellPhoneWrapper = (TextInputLayout) dialogView.findViewById(R.id.cell_phone_wrapper);
        descriptionWrapper = (TextInputLayout) dialogView.findViewById(R.id.description_wrapper);

        emailAddress = (EditText) dialogView.findViewById(R.id.email_address);
        cellPhone = (EditText) dialogView.findViewById(R.id.cell_phone);
        description = (EditText) dialogView.findViewById(R.id.description);

        //handle events here;

        dialogBuilder.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle this event using a custom event

            }
        });

        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Button verify = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        verify.setOnClickListener(new CustomClickListener(alertDialog));
    }

    private class CustomClickListener implements View.OnClickListener {

        private final Dialog mDialog;

        CustomClickListener(Dialog dialog) {
            this.mDialog = dialog;
        }

        @Override
        public void onClick(View v) {

            String email = emailAddress.getText().toString().trim();
            String phone = cellPhone.getText().toString().trim();
            String desc = description.getText().toString().trim();

            //validation here;

            if (email.equals("") || !Util.isValidEmail(email)) {
                emailAddressWrapper.setErrorEnabled(true);
                emailAddressWrapper.setError("Enter a valid email");
            } else if (phone.equals("") || !Util.isValidMobile(phone)) {
                cellPhoneWrapper.setErrorEnabled(true);
                cellPhoneWrapper.setError("Enter a valid phone number!");
            } else if (desc.equals("") || desc.length() < 5) {
                descriptionWrapper.setErrorEnabled(true);
                descriptionWrapper.setError("Your description is too short!");
            } else {
                mDialog.dismiss();
                //initiate the email sending code
                String emailContent = prepareEmailContent(email, phone, desc);

                new SendSupportEmailAsync(getActivity()).execute(emailContent, "tech-support@cfministry.church");

                showDialog();
            }

        }
    }

    private String prepareEmailContent(String email, String phone, String desc){

        return "Phone # : ".concat(phone) +
                "\n" +
                "Email : ".concat(email) +
                "\n" +
                " Description : ".concat(desc) +
                "\n" +
                "Device Information: ---\n" +
                "OS Version: " + osVersion + "\n" +
                "SDK Version: " + sdkVersion + "\n" +
                "OS Device : " + osDevice + "\n" +
                "Product Brand : " + productBrand + "\n" +
                "Device ID : " + deviceId + "\n" +
                "Product Manufacturer : " + productManufacturer + "\n" +
                "Product Model : " + productModel + "\n" +
                "SERIAL NUMBER : " + serialNumber + "\n";
    }

    private void acquireDeviceInformation() {

        osVersion = System.getProperty("os.version");
        productCpu = System.getProperty("os.arch");
        sdkVersion = Build.VERSION.SDK_INT;
        osDevice = android.os.Build.DEVICE;
        productBoard = android.os.Build.BOARD;
        osProduct = android.os.Build.PRODUCT;
        productBrand = android.os.Build.BRAND;
        resolvePhoneStatePermissions();
        deviceId = Build.ID;
        productManufacturer = Build.MANUFACTURER;
        productModel = Build.MODEL;
        serialNumber = SERIAL;
        lcdDisplay = Build.DISPLAY;
        openGLVersion = Util.getVersionFromPackageManager(getActivity());
    }

    private void resolvePhoneStatePermissions() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE},
                    PHONE_STATE_PERMISSION_REQUEST_CODE);
        } else {
            serviceName = Context.TELEPHONY_SERVICE;
            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(serviceName);
            IMEI = telephonyManager.getDeviceId();
            IMSI = telephonyManager.getSubscriberId();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PHONE_STATE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    serviceName = Context.TELEPHONY_SERVICE;
                    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(serviceName);
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    IMEI = telephonyManager.getDeviceId();
                    IMSI = telephonyManager.getSubscriberId();

                }
                return;
            }

        }
    }

    private void showDialog(){
        mView.show(getActivity().getSupportFragmentManager(), "");
    }

    @Subscribe
    public void onSuccessfulLogin(LoginSuccessEvent event){

        mView.dismiss();

        if (event.isSuccess()){

            if (resolver.isConnected()){

                Intent initialSetupService =  new Intent(getActivity(), InitialSetupService.class);
                getActivity().startService(initialSetupService);

                startActivity( new Intent(getActivity(), EndTimeMessageMainActivity.class));

                RequestUserInformation async = new RequestUserInformation(getActivity());
                async.execute();
            }else{
                Util.showSnackBar(getActivity(), getView(), "Please check your network connection!");
            }

        }else{
            Util.showSnackBar(getActivity(), getView(), "Login failed. Please try again!");
        }
    }

    @Subscribe
    public void onEvent(SupportEmailSentEvent event){

        mView.dismiss();

        Util.showSnackBar(getActivity(), getView(), "Thank you for contacting us. Our team will get back to you shortly!");
    }

    private void resetLocalDatabase(){

        final RealmResults<Project> allProjects = realm.where(Project.class).findAll();
        final RealmResults<Amortization> amortizations = realm.where(Amortization.class).findAll();
        final RealmResults<MemberPledge> memberPledges = realm.where(MemberPledge.class).findAll();
        final RealmResults<Member> members = realm.where(Member.class).findAll();
        final RealmResults<MemberRelationship> memberRelationships = realm.where(MemberRelationship.class).findAll();
        final RealmResults<MpesaPayment> mpesaPayments = realm.where(MpesaPayment.class).findAll();
        final RealmResults<PaymentMode> paymentModes = realm.where(PaymentMode.class).findAll();
        final RealmResults<PaymentPeriod> paymentPeriods = realm.where(PaymentPeriod.class).findAll();
        final RealmResults<PledgeAmortization> pledgeAmortizations = realm.where(PledgeAmortization.class).findAll();
        final RealmResults<PledgeStake> pledgeStakes = realm.where(PledgeStake.class).findAll();
        final RealmResults<Contribution> contributions = realm.where(Contribution.class).findAll();
        final RealmResults<FlexiAmortization> flexiAmortizations = realm.where(FlexiAmortization.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                allProjects.deleteAllFromRealm();
                amortizations.deleteAllFromRealm();
                memberPledges.deleteAllFromRealm();
                members.deleteAllFromRealm();
                memberRelationships.deleteAllFromRealm();
                mpesaPayments.deleteAllFromRealm();
                paymentModes.deleteAllFromRealm();
                paymentPeriods.deleteAllFromRealm();
                pledgeAmortizations.deleteAllFromRealm();
                pledgeStakes.deleteAllFromRealm();
                contributions.deleteAllFromRealm();
                flexiAmortizations.deleteAllFromRealm();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
