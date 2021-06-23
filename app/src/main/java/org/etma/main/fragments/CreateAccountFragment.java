package org.etma.main.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.RegisterAccountAsync;
import org.etma.main.asyncs.SendSupportEmailAsync;
import org.etma.main.db.EtmaUser;
import org.etma.main.events.RegisterUserEvent;
import org.etma.main.events.SupportEmailSentEvent;
import org.etma.main.events.SwitchToLoginFragmentEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.RegisterAccount;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.os.Build.SERIAL;

public class CreateAccountFragment extends Fragment {

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

    @BindView(R.id.password_wrapper)
    TextInputLayout passwordWrapper;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.password_confirm_wrapper)
    TextInputLayout confirmPasswordWrapper;

    @BindView(R.id.confirm_password)
    EditText confirmPassword;

    private CatLoadingView mView;

    private Realm realm;

    private NetworkResolver resolver;

    private EditText supportEmailAddress;
    private TextInputLayout supportEmailAddressWrapper;
    private EditText supportCellPhone;
    private TextInputLayout supportCellPhoneWrapper;
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

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreateAccountFragment newInstance() {

        return new CreateAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        realm = Realm.getDefaultInstance();

        resolver = new NetworkResolver(getActivity());

        mView = new CatLoadingView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        ButterKnife.bind(this, view);

        acquireDeviceInformation();

        return view;
    }

    @OnClick(R.id.register)
    public void onRegisterTouchEvent(){

        String name = fullName.getText().toString().trim();
        String phone = cellPhone.getText().toString().trim();

        String email = emailAddress.getText().toString().trim();
        String userPassword = password.getText().toString();
        String confirm = confirmPassword.getText().toString();

        if (name.equals("")){
            fullNameWrapper.setErrorEnabled(true);
            fullNameWrapper.setError("Enter a valid full name ");
        }else if (phone.equals("") || !Util.isValidMobile(phone)){
            cellPhoneWrapper.setError("Enter a valid phone number (254)");
        }else if (email.equals("") || !Util.isValidEmail(email)){
            emailAddressWrapper.setErrorEnabled(true);
            emailAddressWrapper.setError("Invalid email address (e.g john@gmai.com)");
        }else if (userPassword.equals("") || !Util.isValidPassword(userPassword)){
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError("Enter a valid password (at least 5 characters)");
        }else if (!confirm.equals(userPassword)){
            confirmPasswordWrapper.setErrorEnabled(true);
            confirmPasswordWrapper.setError("Entered passwords do not match!");
        }else{

            RealmResults<EtmaUser> users = realm.where(EtmaUser.class).findAll();

            if (!users.isEmpty()){
                Util.showSnackBar(getActivity(), getView(), "You are already registered. Please login!");

            }else{
                NetworkResolver resolver = new NetworkResolver(getActivity());

                if (resolver.isConnected()){
                    doRegistration(name, phone, email, userPassword);
                }else{
                    Util.showSnackBar(getActivity(), getView(), "Please switch ON your mobile data or use Wi-Fi");
                }
            }

        }

    }

    private void doRegistration(String name, String phone, String email, String password){

        Gson gson = new GsonBuilder().serializeNulls().create();

        RegisterAccount register = new RegisterAccount();
        register.setName(name);
        register.setSurname(name.split(" ")[0]);
        register.setUserName("254"+phone);
        register.setEmailAddress(email);
        register.setPassword(password);
        register.setCaptchaResponse(null);

        String json = gson.toJson(register);

        Util.prettyPrintJson(json);

        RegisterAccountAsync registerAccountAsync = new RegisterAccountAsync(getActivity());

        registerAccountAsync.execute(json);

        mView = new CatLoadingView();

        showDialog();

    }

    private void showDialog(){
        mView.show(getActivity().getSupportFragmentManager(), "");
    }

    private void save(){

        String name = fullName.getText().toString().trim();
        String phone = cellPhone.getText().toString().trim();
        String email = emailAddress.getText().toString().trim();
        String userPassword = password.getText().toString();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<EtmaUser> users = realm.where(EtmaUser.class).findAll();

        if (!users.isEmpty()){
            realm.beginTransaction();
            users.deleteAllFromRealm();
            realm.commitTransaction();
        }

        //STORE NEWLY REGISTERED USER
        EtmaUser user = new EtmaUser();
        user.setFull_name(name);
        user.setSurname("");
        user.setCell_phone("254"+phone);
        user.setEmail_address(email);
        user.setPassword(userPassword);
        user.setCan_login(true);

        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();

        //notify containing activity to load login fragment
        EventBus.getDefault().post(new SwitchToLoginFragmentEvent(true));
    }

    @Subscribe
    public void onRegistrationCompletion(RegisterUserEvent event){

        mView.dismiss();
        if (event.isCanLogin()){
            save();
        }else{
            Util.showSnackBar(getActivity(), getView(), event.getMessage());
        }
    }

    @OnClick(R.id.support)
    public void onSupportAction(){

        //pop up a dialog form;
        if (resolver.isConnected()){
            showSupportDialog();
        }else{
            Util.showSnackBar(getActivity(), getView(), "Turn on your 4G data or connect to a Wi-Fi!");
        }
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

        supportEmailAddressWrapper = (TextInputLayout) dialogView.findViewById(R.id.email_address_wrapper);
        supportCellPhoneWrapper = (TextInputLayout) dialogView.findViewById(R.id.cell_phone_wrapper);
        descriptionWrapper = (TextInputLayout) dialogView.findViewById(R.id.description_wrapper);

        supportEmailAddress = (EditText) dialogView.findViewById(R.id.email_address);
        supportCellPhone = (EditText) dialogView.findViewById(R.id.cell_phone);
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

            String email = supportEmailAddress.getText().toString().trim();
            String phone = supportCellPhone.getText().toString().trim();
            String desc = description.getText().toString().trim();

            //validation here;

            if (email.equals("") || !Util.isValidEmail(email)) {
                supportEmailAddressWrapper.setErrorEnabled(true);
                supportEmailAddressWrapper.setError("Enter a valid email");
            } else if (phone.equals("") || !Util.isValidMobile(phone)) {
                supportCellPhoneWrapper.setErrorEnabled(true);
                supportCellPhoneWrapper.setError("Enter a valid phone number!");
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


    @Subscribe
    public void onEvent(SupportEmailSentEvent event){

        mView.dismiss();

        Util.showSnackBar(getActivity(), getView(), "Thank you for contacting us. Our team will get back to you shortly!");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
