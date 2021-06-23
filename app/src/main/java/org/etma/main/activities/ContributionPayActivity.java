package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.LipaNaMpesaAsync;
import org.etma.main.custom.PrefixEditText;
import org.etma.main.db.Amortization;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.PaymentMode;
import org.etma.main.events.LipaNaMpesaCompletedEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.CreateMpesaPayment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ContributionPayActivity extends AppCompatActivity {

    private Realm realm;

    private Amortization amortization;

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

    @BindView(R.id.balance)
    TextView balance;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.pay)
    AppCompatButton pay;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private EtmaUser user;
    private MemberPledge pledge;
    private PaymentMode paymentMode;

    private NetworkResolver resolver;
    private CatLoadingView mView;

    private MpesaPayment draft;

    private String origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        user = realm.where(EtmaUser.class).findFirst();

        resolver = new NetworkResolver(this);

        mView = new CatLoadingView();

        Intent intent = getIntent();

        if (intent != null){

            origin = intent.getExtras().getString("origin");

            long amortizationId = intent.getExtras().getLong("amortizationId");
            int localPledgeId = intent.getExtras().getInt("localPledgeId");
            long modeId = intent.getExtras().getLong("paymentModeId");

            amortization = realm.where(Amortization.class).equalTo("id", amortizationId).findFirst();
            pledge = realm.where(MemberPledge.class).equalTo("pledgeId", ""+localPledgeId).findFirst();
            paymentMode = realm.where(PaymentMode.class).equalTo("id", modeId).findFirst();

            if (amortization != null){

                memberName.setText(user.getFull_name());
                amount.setText(String.valueOf(amortization.getBalance() ));
                cellPhone.setText(user.getCell_phone().replaceFirst("254", ""));
                date.setText(amortization.getContributionDate());

                if (paymentMode != null){

                    if (paymentMode.getName().equals("BANK TRANSFER")){
                        pay.setText(R.string.bank_transfer_pay);
                    }
                }

                balance.setText(amortization.getBalance());
            }
        }
    }

    @OnClick(R.id.pay)
    public void onPayTouched(){

        //validate cellphone field;

        String phone = cellPhone.getText().toString().trim();

        String amountTopay = amount.getText().toString().trim();

        if (amountTopay.equals("")){

            amountWrapper.setError("Amount field is required!");
            amountWrapper.setErrorEnabled(true);
        }else if (Integer.parseInt(amountTopay) < 1){
            amountWrapper.setError("Amount to pay cannot be less than 1.");
            amountWrapper.setErrorEnabled(true);
        }else if (Integer.parseInt(amountTopay) > 70000){
            amountWrapper.setError("Amount to pay cannot be more than 70,000.");
            amountWrapper.setErrorEnabled(true);
        }else if (phone.equals("")){
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Mobile number field is required: e.g 7xxxxxxxx");
        }else if ((phone.length() < 9) || phone.length() > 9){
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Mobile numbers must be 9 digits");
        }else{

            if (resolver.isConnected()){

                draft = createMpesaPayment(amountTopay, phone);

                if (draft != null) {

                    String json = prepareJson(draft);

                    LipaNaMpesaAsync async = new LipaNaMpesaAsync(this);
                    async.execute(json, "" + amortization.getAmortizationId(), "" + draft.getId());

                    showDialog();
                }
            }else{
                Util.showSnackBar(this, coordinatorLayout, "Please turn ON your 4G data/Wi-fi!");
            }

        }

    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    private String prepareJson(MpesaPayment draft) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateMpesaPayment mpesaPayment = new CreateMpesaPayment();

        mpesaPayment.setId(null);
        mpesaPayment.setAccountRefDesc(draft.getAccountRefDesc());
        mpesaPayment.setAmountToPay(Integer.parseInt(draft.getAmountToPay()));
        mpesaPayment.setCreationTime(draft.getCreationTime());
        mpesaPayment.setCreatorUserId(Integer.parseInt(draft.getCreatorUserId()));
        mpesaPayment.setDeleterUserId(Integer.parseInt(draft.getDeleterUserId()));
        mpesaPayment.setDeletionTime(draft.getDeletionTime());
        mpesaPayment.setIsDeleted(false);
        mpesaPayment.setPledgeAmortizationId(draft.getAmortizationId());
        mpesaPayment.setLastModificationTime(draft.getLastModificationTime());
        mpesaPayment.setLastModifierUserId(Integer.parseInt(draft.getLastModifierUserId()));
        mpesaPayment.setMpesaReceiptNumber("");
        mpesaPayment.setPhoneNumber(draft.getPhoneNumber());
        mpesaPayment.setRequestDate(Util.getCurrentDate());
        mpesaPayment.setRespCheckoutRequestID("");
        mpesaPayment.setRespCode("");
        mpesaPayment.setRespCustMsg("");
        mpesaPayment.setRespDesc("");
        mpesaPayment.setRespMerchantRequestID("");
        mpesaPayment.setResultCode("");
        mpesaPayment.setResultDesc("");
        mpesaPayment.setStatus("Pending");
        mpesaPayment.setUserId(Integer.parseInt(draft.getUserId()));

        String json = gson.toJson(mpesaPayment);

        Util.prettyPrintJson(json);

        return json;

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Subscribe
    public void onEvent(final LipaNaMpesaCompletedEvent event){

        if (event.isSuccess()){

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    mView.dismiss();

                    //save to db and transition to payments screen;

                    if (event.getPayLoad().equals("0")){

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                draft.setResultCode(event.getPayLoad());
                                realm.copyToRealm(draft);
                            }
                        });

                        Intent intent = new Intent(ContributionPayActivity.this, MpesaPaymentActivity.class);
                        startActivity(intent);
                    }else{
                        //try again;
                        Util.showSnackBar(ContributionPayActivity.this, coordinatorLayout, "Something went wrong, please try again!");
                    }

                }
            });

        }else{

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    mView.dismiss();

                    Util.showSnackBar(ContributionPayActivity.this, coordinatorLayout, "Something went wrong, check amount then try again later!");
                }
            });

        }
    }

    private MpesaPayment createMpesaPayment(String amount, String phone){

        Realm thisInstance = Realm.getDefaultInstance();

        MpesaPayment existing = thisInstance.where(MpesaPayment.class).equalTo("amortizationId", amortization.getAmortizationId()).findFirst();

        if (existing == null){

            RealmResults<MpesaPayment> allPayments = realm.where(MpesaPayment.class).findAll().sort("id", Sort.ASCENDING);

            MpesaPayment mpesaPayment = new MpesaPayment();

            long lastId;

            if (allPayments.isEmpty()){
                mpesaPayment.setId(0);
            }else{
                lastId = allPayments.last().getId();
                mpesaPayment.setId( lastId + 1 );
            }

            mpesaPayment.setAmortizationId(amortization.getAmortizationId());

            String prefix = "TM0";
            String joint = prefix.concat(phone);

            mpesaPayment.setAccountRefDesc(joint);
            mpesaPayment.setAmountToPay(amount);
            mpesaPayment.setCreationTime(amortization.getCreationTime());
            mpesaPayment.setCreatorUserId(String.valueOf(amortization.getCreatorUserId()));
            mpesaPayment.setDeleterUserId(String.valueOf(amortization.getDeleterUserId()));
            mpesaPayment.setDeletionTime(amortization.getDeletionTime());
            mpesaPayment.setIsDeleted("false");
            mpesaPayment.setPaymentModeId(Integer.parseInt(paymentMode.getPaymentModeId()));
            mpesaPayment.setLastModificationTime(amortization.getLastModificationTime());
            mpesaPayment.setLastModifierUserId(String.valueOf(amortization.getLastModifierUserId()));
            mpesaPayment.setMpesaReceiptNumber("");
            mpesaPayment.setPhoneNumber("254".concat(phone));
            mpesaPayment.setRequestDate(Util.getCurrentDate());
            mpesaPayment.setRespCheckoutRequestID("");
            mpesaPayment.setRespCode("-1");
            mpesaPayment.setRespCustMsg("");
            mpesaPayment.setRespDesc("");
            mpesaPayment.setRespMerchantRequestID("");
            mpesaPayment.setResultCode("-1");
            mpesaPayment.setResultDesc("Processing");
            mpesaPayment.setStatus("Draft");
            mpesaPayment.setUserId(String.valueOf(amortization.getUserId()));

            return mpesaPayment;
        }

        return null;

    }


    @Override
    protected void onResume() {
        super.onResume();
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
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
    }
}
