package org.etma.main.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.LipaNaMpesaExtraMileAsync;
import org.etma.main.custom.PrefixEditText;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.UserOauth;
import org.etma.main.events.LipaNaMpesaExtraMileEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.LipaNaMpesa;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DonateExtraMileActivity extends AppCompatActivity {

    private String[] campaigns = new String[]{
            "Select campaign", "Adopt a Jar", "Faith Veterans", "Faith Pioneers", "Foundation", "Young Parents", "18224"
    };

    @BindView(R.id.amount_wrapper)
    TextInputLayout amountWrapper;

    @BindView(R.id.amount)
    EditText amount;

    @BindView(R.id.member_name)
    TextView memberName;

    @BindView(R.id.cell_phone_wrapper)
    TextInputLayout cellPhoneWrapper;

    @BindView(R.id.cell_phone)
    PrefixEditText cellPhone;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.select_campaign_spinner)
    AppCompatSpinner spinner;

    private CatLoadingView mView;

    private long tenDigitNumber = Util.generateTenDigitNumber();
    private String phone;
    private String amountTopay;
    private String selectedCampaign;

    private EtmaUser etmaUser;
    private UserOauth user;

    private NetworkResolver resolver;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_extra_mile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        customSpinner(spinner, campaigns);

        realm = Realm.getDefaultInstance();

        etmaUser = realm.where(EtmaUser.class).findFirst();
        user = realm.where(UserOauth.class).findFirst();

        resolver = new NetworkResolver(this);

        mView = new CatLoadingView();

        memberName.setText(etmaUser.getFull_name());
        cellPhone.setText(etmaUser.getCell_phone().replaceFirst("254", ""));
        date.setText(Util.getCurrentDate());
    }

    @OnClick(R.id.pay)
    public void onPay(){

        phone = cellPhone.getText().toString().trim();

        amountTopay = amount.getText().toString().trim();

        selectedCampaign = spinner.getSelectedItem().toString().trim();

        if (amountTopay.equals("")){
            amountWrapper.setError("Amount field is required!");
            amountWrapper.setErrorEnabled(true);
        }else if (Integer.parseInt(amountTopay) < 1){
            amountWrapper.setError("Amount to pay cannot be less than 1.");
            amountWrapper.setErrorEnabled(true);
        }else if (Integer.parseInt(amountTopay) > 70000){
            amountWrapper.setErrorEnabled(true);
            amountWrapper.setError("Amount to pay cannot be more than 70,000.");
        }else if (selectedCampaign.equals("Select campaign")){
            setSpinnerError(spinner, "Select a campaign to continue!");
        }else if (phone.equals("")){
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Mobile number field is required: e.g 7xxxxxxxx");
        }else if ((phone.length() < 9) || phone.length() > 9){
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Mobile numbers must be 9 digits");
        }else{

            if (resolver.isConnected()){

                String json = prepareJsonForMpesaPay();

                LipaNaMpesaExtraMileAsync async = new LipaNaMpesaExtraMileAsync(this);
                async.execute(json);

                showDialog();

            }else{
                Util.showSnackBar(this, coordinatorLayout, "Please turn ON your 4G data/Wi-fi!");
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(final LipaNaMpesaExtraMileEvent event){

        mView.dismiss();

        if (event.isSuccess()) {

          // save to db and transition to payments screen;

          if (event.getPayLoad().equals("0")) {

              Realm realm = Realm.getDefaultInstance();

              if (realm.isInTransaction()){
                  realm.cancelTransaction();
              }

              UserOauth user = realm.where(UserOauth.class).findFirst();

              RealmResults<MpesaPayment> allPayments = realm.where(MpesaPayment.class).findAll().sort("id", Sort.ASCENDING);

              final MpesaPayment mpesaPayment = new MpesaPayment();

              long lastId;

              if (allPayments.isEmpty()){
                  mpesaPayment.setId(0);
              }else{
                  lastId = allPayments.last().getId();
                  mpesaPayment.setId( lastId + 1 );
              }

              mpesaPayment.setAmortizationId(0);

              mpesaPayment.setAccountRefDesc(selectedCampaign);
              mpesaPayment.setAmountToPay(amountTopay);
              mpesaPayment.setCreationTime(Util.getCurrentDate());
              mpesaPayment.setCreatorUserId(""+user.getUserId());
              mpesaPayment.setDeleterUserId(""+ user.getUserId());
              mpesaPayment.setDeletionTime(Util.getCurrentDate());
              mpesaPayment.setIsDeleted("false");
              mpesaPayment.setPaymentModeId(1);
              mpesaPayment.setLastModificationTime(Util.getCurrentDate());
              mpesaPayment.setLastModifierUserId(String.valueOf(user.getUserId()));
              mpesaPayment.setMpesaReceiptNumber("");
              mpesaPayment.setPhoneNumber("254".concat(phone));
              mpesaPayment.setRequestDate(Util.getCurrentDate());
              mpesaPayment.setRespCheckoutRequestID("");
              mpesaPayment.setRespCustMsg("");
              mpesaPayment.setRespDesc("");
              mpesaPayment.setRespMerchantRequestID("");
              mpesaPayment.setResultCode("-1");
              mpesaPayment.setCustom1("" + Util.generateTenDigitNumber());
              mpesaPayment.setUserId(String.valueOf(user.getUserId()));
              mpesaPayment.setStatus(event.getStatus());
              mpesaPayment.setRespCode(event.getPayLoad());
              mpesaPayment.setType("EXTRA");
              mpesaPayment.setResultDesc(event.getStatus());

              realm.executeTransaction(realm1 -> realm1.copyToRealm(mpesaPayment));

              startActivity(new Intent(DonateExtraMileActivity.this, ExtraMileCampaignsActivity.class));

          }else{
              Util.showSnackBar(DonateExtraMileActivity.this, coordinatorLayout, "Something went wrong, please try again!");
          }

        }else{
            mView.dismiss();
            Util.showSnackBar(DonateExtraMileActivity.this, coordinatorLayout, "Something went wrong, please try again!");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
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

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    private String prepareJsonForMpesaPay() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        LipaNaMpesa mpesaPayment = new LipaNaMpesa();

        mpesaPayment.setId(null);
        mpesaPayment.setAccountRefDesc(selectedCampaign);
        mpesaPayment.setAmountToPay(Integer.parseInt(amountTopay));
        mpesaPayment.setCreationTime(Util.getCurrentDate());
        mpesaPayment.setCreatorUserId(user.getUserId());
        mpesaPayment.setDeleterUserId(user.getUserId());
        mpesaPayment.setDeletionTime(Util.getCurrentDate());
        mpesaPayment.setIsDeleted(false);
        mpesaPayment.setPledgeAmortizationId(0);
        mpesaPayment.setLastModificationTime(Util.getCurrentDate());
        mpesaPayment.setLastModifierUserId(user.getUserId());
        mpesaPayment.setMpesaReceiptNumber("");
        mpesaPayment.setPhoneNumber("254".concat(phone));
        mpesaPayment.setRequestDate(Util.getCurrentDate());
        mpesaPayment.setRespCheckoutRequestID("");
        mpesaPayment.setRespCode("");
        mpesaPayment.setRespCustMsg("");
        mpesaPayment.setRespDesc("");
        mpesaPayment.setRespMerchantRequestID("");
        mpesaPayment.setResultCode("");
        mpesaPayment.setResultDesc("");
        mpesaPayment.setCustom1(""+ tenDigitNumber);
        mpesaPayment.setCustom2("");
        mpesaPayment.setCustom3("");
        mpesaPayment.setCustom4("");
        mpesaPayment.setStatus("Pending");
        mpesaPayment.setUserId(user.getUserId());

        return gson.toJson(mpesaPayment);

    }

    private void customSpinner(Spinner spinner, String[] resources) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                resources) {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                return textView;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);


                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

    }

    private void setSpinnerError(AppCompatSpinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error");
            selectedTextView.setTextColor(Color.RED);
            selectedTextView.setText(error);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, EndTimeMessageMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
