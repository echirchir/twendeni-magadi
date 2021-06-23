package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import org.etma.main.R;
import org.etma.main.db.Amortization;
import org.etma.main.db.Contribution;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.PaymentMode;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddContributionActivity extends AppCompatActivity {


    @BindView(R.id.pledge)
    EditText pledge;

    @BindView(R.id.pledge_wrapper)
    TextInputLayout pledgeWrapper;

    @BindView(R.id.amount)
    EditText amount;

    @BindView(R.id.amount_wrapper)
    TextInputLayout amountWrapper;

    @BindView(R.id.pledges_amount)
    EditText pledgesAmount;

    @BindView(R.id.pledges_wrapper)
    TextInputLayout pledgesWrapper;

    @BindView(R.id.paid_by)
    EditText paidBy;

    @BindView(R.id.paid_by_wrapper)
    TextInputLayout paidByWrapper;

    @BindView(R.id.reference_number)
    EditText referenceNumber;

    @BindView(R.id.reference_number_wrapper)
    TextInputLayout referenceNumberWrapper;

    @BindView(R.id.payment_date)
    EditText paymentDate;

    @BindView(R.id.payment_date_wrapper)
    TextInputLayout paymentDateWrapper;

    @BindView(R.id.payment_mode)
    EditText mode;

    @BindView(R.id.payment_mode_wrapper)
    TextInputLayout paymentModeWrapper;

    private Realm realm;

    private MemberPledge memberPledge;
    private PaymentMode paymentMode;
    private Amortization amortization;

    private long amortizationId;
    private int localPledgeId;
    private long paymentModeId;

    private EtmaUser user;
    private UserOauth oauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contribution);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        user = realm.where(EtmaUser.class).findFirst();
        oauth = realm.where(UserOauth.class).findFirst();

        Intent intent = getIntent();

        if (intent != null){

            amortizationId = intent.getExtras().getLong("amortizationId");
            localPledgeId = intent.getExtras().getInt("localPledgeId");
            paymentModeId = intent.getExtras().getLong("paymentModeId");

            memberPledge = realm.where(MemberPledge.class).equalTo("memberPledgeId", ""+localPledgeId).findFirst();
            amortization = realm.where(Amortization.class).equalTo("id", amortizationId).findFirst();

            paymentMode = realm.where(PaymentMode.class).equalTo("id", paymentModeId).findFirst();

            pledge.setText(memberPledge.getName() + " / " + memberPledge.getAmount());
            mode.setText( paymentMode.getName() );
            amount.setText(String.valueOf(amortization.getAmount()));
            pledgesAmount.setText(String.valueOf(amortization.getAmount()));
        }

        paymentDate.setText(Util.getCurrentDate());

        paidBy.setText(user.getFull_name());
        referenceNumber.setText(user.getCell_phone());

        amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // check amount entered for validation
                String enteredAmount = amount.getText().toString().trim();

                if (!enteredAmount.equals("")){

                    int toPay = Integer.parseInt(enteredAmount);

                    if (toPay > amortization.getAmount()){
                        amountWrapper.setError("You cannot overpay for this installment!");
                        amountWrapper.setErrorEnabled(true);
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @OnClick(R.id.done)
    public void onDoneTouch(){

        //validate and proceed
        String pledgeName = pledge.getText().toString().trim();
        String amountEntered = amount.getText().toString().trim();

        int actualAmount = Integer.parseInt(amountEntered);

        String pledges = pledgesAmount.getText().toString().trim();
        String paidByWho = paidBy.getText().toString().trim();
        String referenceNo = referenceNumber.getText().toString().trim();
        String payDate = paymentDate.getText().toString().trim();
        String payMode = mode.getText().toString().trim();

        if (pledgeName.equals("")){
            pledgeWrapper.setErrorEnabled(true);
            pledgeWrapper.setError("Pledge name is required!");
        }else if (amountEntered.equals("")){
            amountWrapper.setErrorEnabled(true);
            amountWrapper.setError("Pledge name is required!");
        }else if (actualAmount > amortization.getAmount()){
            amountWrapper.setErrorEnabled(true);
            amountWrapper.setError("Amount field is required!");
        }else if (payMode.equals("")){
            paymentModeWrapper.setErrorEnabled(true);
            paymentModeWrapper.setError("Payment mode is required!");
        }else if (pledges.equals("")){
            pledgesWrapper.setErrorEnabled(true);
            pledgesWrapper.setError("Pledges amount is required!");
        }else if (paidByWho.equals("")){
            paidByWrapper.setErrorEnabled(true);
            paidByWrapper.setError("Paid by field is required!");
        }else if (referenceNo.equals("")){
            referenceNumberWrapper.setErrorEnabled(true);
            referenceNumberWrapper.setError("Reference number is required!");
        }else if (payDate.equals("")){
            paymentDateWrapper.setErrorEnabled(true);
            paymentDateWrapper.setError("Payment date is required!");
        }else{
            save(actualAmount, referenceNo, payDate, paidByWho);
        }


    }

    private void save(int actualAmount, String reference, String contributionDate, String paidby){

        RealmResults<Contribution> contributions = realm.where(Contribution.class).findAll().sort("id", Sort.ASCENDING);

        Contribution contribution = new Contribution();

        long lastId;

        if (contributions.isEmpty()){

            contribution.setId(0);

        }else{
            lastId = contributions.last().getId();
            contribution.setId( lastId + 1);
        }

        contribution.setCustom1("");
        contribution.setCustom2("");
        contribution.setCustom3("");
        contribution.setCustom4("");
        contribution.setLastModifierUserId(""+oauth.getUserId());
        contribution.setPaymentModeId(paymentMode.getPaymentModeId());
        contribution.setDeletionTime(Util.getCurrentDate());
        contribution.setIsDeleted("false");
        contribution.setRefNumber(reference);
        contribution.setMemberPledgeId(memberPledge.getPledgeId());
        contribution.setContributionId(null);
        contribution.setAmount(""+ actualAmount);
        contribution.setCreatorUserId(""+oauth.getUserId());
        contribution.setAccountNumber(reference);
        contribution.setVerified("true");
        contribution.setContributionDate(contributionDate);
        contribution.setUserId(""+ oauth.getUserId());
        contribution.setDeleterUserId("" + oauth.getUserId());
        contribution.setCreationTime(Util.getCurrentDate());
        contribution.setMemberId(memberPledge.getMemberId());
        contribution.setPaidBy(paidby);
        contribution.setLastModificationTime(Util.getCurrentDate());
        contribution.setPledgeAmortizationId(""+amortization.getAmortizationId());
        contribution.setVerifiedBy(""+ oauth.getUserId());
        contribution.setStatus("Draft");

        realm.beginTransaction();

        realm.copyToRealm(contribution);

        realm.commitTransaction();

        startActivity( new Intent(this, MakeContributionActivity.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
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
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
    }

}
