package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.CreateContributionAsync;
import org.etma.main.asyncs.LipaNaMpesaAsync;
import org.etma.main.custom.PrefixEditText;
import org.etma.main.db.Amortization;
import org.etma.main.db.Contribution;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.PaymentMode;
import org.etma.main.db.UserOauth;
import org.etma.main.events.CreateContributionEvent;
import org.etma.main.events.LipaNaMpesaCompletedEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.CreateOrEditContribution;
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

public class DefaultPayActivity extends AppCompatActivity {

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

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.pay)
    AppCompatButton pay;

    private EtmaUser user;
    private UserOauth userOauth;
    private MemberPledge pledge;
    private PaymentMode paymentMode;

    private NetworkResolver resolver;
    private CatLoadingView mView;

    private MpesaPayment draft;

    private String origin;

    private long tenDigitNumber = Util.generateTenDigitNumber();
    private String phone;
    private String amountTopay;

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
        userOauth = realm.where(UserOauth.class).findFirst();

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

                if(user != null){
                    memberName.setText(user.getFull_name());
                    cellPhone.setText(user.getCell_phone().replaceFirst("254", ""));
                }else{
                    user = realm.where(EtmaUser.class).findFirst();
                    memberName.setText(user.getFull_name());
                    cellPhone.setText(user.getCell_phone().replaceFirst("254", ""));
                }

                if (paymentMode != null){

                    if (paymentMode.getName().equals("BANK TRANSFER")){
                        pay.setText(R.string.bank_transfer_pay);
                    }
                }

                amount.setText(String.valueOf(amortization.getBalance() ));

                date.setText(amortization.getContributionDate());

                balance.setText(""+amortization.getBalance());
            }
        }


    }

    @OnClick(R.id.pay)
    public void onPayTouched(){

        phone = cellPhone.getText().toString().trim();

        amountTopay = amount.getText().toString().trim();

        if (amountTopay.equals("")){
            amountWrapper.setError("Amount field is required!");
            amountWrapper.setErrorEnabled(true);
        }else if (Integer.parseInt(amountTopay) < 1){
            amountWrapper.setError("Amount to pay cannot be less than 1.");
            amountWrapper.setErrorEnabled(true);
        }else if (Integer.parseInt(amountTopay) > 70000){
            amountWrapper.setErrorEnabled(true);
            amountWrapper.setError("Amount to pay cannot be more than 70,000.");
        }else if (phone.equals("")){
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Mobile number field is required: e.g 7xxxxxxxx");
        }else if ((phone.length() < 9) || phone.length() > 9){
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Mobile numbers must be 9 digits");
        }else{

            if (resolver.isConnected()){

                if (paymentMode.getName().equals("MPESA")){

                    draft = createMpesaPayment(amountTopay, phone);

                    if (draft != null) {

                        String json = prepareJson(draft);

                        Util.prettyPrintJson(json);

                        LipaNaMpesaAsync async = new LipaNaMpesaAsync(this);
                        async.execute(json, "" + amortization.getAmortizationId(), "" + draft.getId());

                        showDialog();
                    }else{
                        Util.showSnackBar(this, coordinatorLayout, "the draft is null here ");
                    }
                }else{

                    String json = prepareBankTransferJson( amountTopay, phone );

                    CreateContributionAsync async = new CreateContributionAsync(this);
                    async.execute(json);

                    showDialog();

                }

            }else{
                Util.showSnackBar(this, coordinatorLayout, "Please turn ON your 4G data/Wi-fi!");
            }

        }

    }

    @Subscribe
    public void event(CreateContributionEvent event){
        mView.dismiss();

        if (event.isSuccess()){

            saveContributionAndTransition();
        }else{
            Util.showSnackBar(this, coordinatorLayout, "Something wrong happened. Try again!");
        }
    }

    private void saveContributionAndTransition(){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                RealmResults<Contribution> contributions = realm.where(Contribution.class).findAll().sort("id", Sort.ASCENDING);

                Contribution contribution = new Contribution();

                long lastId;

                if (contributions.isEmpty()){

                    contribution.setId(0);

                }else{
                    lastId = contributions.last().getId();
                    contribution.setId( lastId + 1);
                }

                contribution.setCustom1("" + tenDigitNumber);
                contribution.setCustom2(pledge.getName());
                contribution.setCustom3("");
                contribution.setCustom4("");
                contribution.setLastModifierUserId(""+userOauth.getUserId());
                contribution.setPaymentModeId(paymentMode.getPaymentModeId());
                contribution.setDeletionTime(Util.getCurrentDate());
                contribution.setIsDeleted("false");
                contribution.setRefNumber(user.getCell_phone());
                contribution.setMemberPledgeId(pledge.getPledgeId());
                contribution.setContributionId(null);
                contribution.setAmount(amountTopay);
                contribution.setCreatorUserId(""+userOauth.getUserId());
                contribution.setAccountNumber(phone);
                contribution.setVerified("false");
                contribution.setContributionDate(amortization.getContributionDate());
                contribution.setUserId(""+ userOauth.getUserId());
                contribution.setDeleterUserId("" + userOauth.getUserId());
                contribution.setCreationTime(Util.getCurrentDate());
                contribution.setMemberId(pledge.getMemberId());
                contribution.setPaidBy(user.getFull_name());
                contribution.setLastModificationTime(Util.getCurrentDate());
                contribution.setPledgeAmortizationId(""+amortization.getAmortizationId());
                contribution.setVerifiedBy(""+ userOauth.getUserId());
                contribution.setStatus("COMPLETED");

                realm.copyToRealm(contribution);
            }
        });

        Intent intent = new Intent(DefaultPayActivity.this, MakeContributionActivity.class);
        startActivity(intent);
    }

    private String prepareBankTransferJson( String amountTopay, String phone){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateOrEditContribution contribution = new CreateOrEditContribution();

        contribution.setCustom1("" + tenDigitNumber);
        contribution.setCustom2(pledge.getName());
        contribution.setCustom3("");
        contribution.setCustom4("");
        contribution.setLastModifierUserId(""+userOauth.getUserId());
        contribution.setPaymentModeId(paymentMode.getPaymentModeId());
        contribution.setDeletionTime(Util.getCurrentDate());
        contribution.setIsDeleted("false");
        contribution.setRefNumber(user.getCell_phone());
        contribution.setMemberPledgeId(pledge.getPledgeId());
        contribution.setId(null);
        contribution.setAmount(amountTopay);
        contribution.setCreatorUserId(""+userOauth.getUserId());
        contribution.setAccountNumber(phone);
        contribution.setVerified("false");
        contribution.setContributionDate(amortization.getContributionDate());
        contribution.setUserId(""+ userOauth.getUserId());
        contribution.setDeleterUserId("" + userOauth.getUserId());
        contribution.setCreationTime(Util.getCurrentDate());
        contribution.setMemberId(pledge.getMemberId());
        contribution.setPaidBy(user.getFull_name());
        contribution.setLastModificationTime(Util.getCurrentDate());
        contribution.setPledgeAmortizationId(""+amortization.getAmortizationId());
        contribution.setVerifiedBy(""+ userOauth.getUserId());

        return gson.toJson(contribution);
    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    private String prepareJson(MpesaPayment draft) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        LipaNaMpesa mpesaPayment = new LipaNaMpesa();

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
        mpesaPayment.setCustom1(""+ tenDigitNumber);
        mpesaPayment.setCustom2("");
        mpesaPayment.setCustom3("");
        mpesaPayment.setCustom4("");
        mpesaPayment.setUserId(Integer.parseInt(draft.getUserId()));

        return gson.toJson(mpesaPayment);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Subscribe
    public void onEvent(final LipaNaMpesaCompletedEvent event){

        if (event.isSuccess()){

            runOnUiThread(() -> {

                mView.dismiss();

                //save to db and transition to payments screen;

                if (event.getPayLoad().equals("0")){

                    realm.executeTransaction(realm -> {
                        draft.setResultCode(event.getPayLoad());
                        draft.setStatus(event.getMessage());
                        realm.copyToRealm(draft);
                    });

                    if (origin.equals("CONTRIBUTIONS")){

                        realm.executeTransaction(realm -> {

                            RealmResults<Contribution> contributions = realm.where(Contribution.class).findAll().sort("id", Sort.ASCENDING);

                            Contribution contribution = new Contribution();

                            long lastId;

                            if (contributions.isEmpty()){

                                contribution.setId(0);

                            }else{
                                lastId = contributions.last().getId();
                                contribution.setId( lastId + 1);
                            }

                            contribution.setCustom1("" + tenDigitNumber);
                            contribution.setCustom2(pledge.getName());
                            contribution.setCustom3("");
                            contribution.setCustom4("");
                            contribution.setLastModifierUserId(""+draft.getUserId());
                            contribution.setPaymentModeId(paymentMode.getPaymentModeId());
                            contribution.setDeletionTime(Util.getCurrentDate());
                            contribution.setIsDeleted("false");
                            contribution.setRefNumber(draft.getPhoneNumber());
                            contribution.setMemberPledgeId(pledge.getPledgeId());
                            contribution.setContributionId(null);
                            contribution.setAmount(""+ draft.getAmountToPay());
                            contribution.setCreatorUserId(""+draft.getUserId());
                            contribution.setAccountNumber(draft.getPhoneNumber());
                            contribution.setVerified("false");
                            contribution.setContributionDate(amortization.getContributionDate());
                            contribution.setUserId(""+ draft.getUserId());
                            contribution.setDeleterUserId("" + draft.getUserId());
                            contribution.setCreationTime(Util.getCurrentDate());
                            contribution.setMemberId(pledge.getMemberId());
                            contribution.setPaidBy(user.getFull_name());
                            contribution.setLastModificationTime(Util.getCurrentDate());
                            contribution.setPledgeAmortizationId(""+amortization.getAmortizationId());
                            contribution.setVerifiedBy(""+ draft.getUserId());
                            contribution.setStatus("Draft");

                            realm.copyToRealm(contribution);
                        });

                        Intent intent = new Intent(DefaultPayActivity.this, MakeContributionActivity.class);
                        startActivity(intent);
                    }else{

                        Intent intent = new Intent(DefaultPayActivity.this, MpesaPaymentActivity.class);
                        startActivity(intent);
                    }

                }else{
                    mView.dismiss();
                    //try again;
                    Util.showSnackBar(DefaultPayActivity.this, coordinatorLayout, "Something went wrong, please try again!");
                }

            });

        }else{

            runOnUiThread(() -> {

                mView.dismiss();

                Util.showSnackBar(DefaultPayActivity.this, coordinatorLayout, "Something went wrong. Please contact support!");
            });

        }
    }

    private MpesaPayment createMpesaPayment(String amount, String phone){

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
        mpesaPayment.setCustom1("" + tenDigitNumber);
        mpesaPayment.setUserId(String.valueOf(amortization.getUserId()));

        return mpesaPayment;

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
