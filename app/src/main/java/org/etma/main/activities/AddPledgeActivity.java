package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import org.etma.main.db.Member;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.PaymentPeriod;
import org.etma.main.db.PledgeStake;
import org.etma.main.db.Project;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddPledgeActivity extends AppCompatActivity {

    private static final int REQUEST_MEMBER_CODE = 100;
    private static final int REQUEST_STAKE_CODE = 200;
    private static final int REQUEST_PERIOD_CODE = 300;

    @BindView(R.id.stake)
    EditText stake;

    @BindView(R.id.stake_wrapper)
    TextInputLayout stakeWrapper;

    @BindView(R.id.member)
    EditText member;

    @BindView(R.id.member_wrapper)
    TextInputLayout memberWrapper;

    @BindView(R.id.payment_period)
    EditText paymentPeriod;

    @BindView(R.id.payment_period_wrapper)
    TextInputLayout paymentPeriodWrapper;

    @BindView(R.id.amount)
    EditText amount;

    @BindView(R.id.amount_wrapper)
    TextInputLayout amountWrapper;

    @BindView(R.id.first_payment)
    EditText firstPayment;

    @BindView(R.id.first_payment_wrapper)
    TextInputLayout firstPaymentWrapper;

    @BindView(R.id.start_date)
    EditText startDate;

    @BindView(R.id.end_date)
    EditText endDate;

    private Realm realm;

    private Member memberObject;
    private PledgeStake pledgeStake;
    private PaymentPeriod paymentObject;

    private UserOauth user;

    private Project project;

    private String action = "CREATE";

    private MemberPledge pledge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pledge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        user = realm.where(UserOauth.class).findFirst();

        project = realm.where(Project.class).findFirst();

        if (project != null){
            String start = project.getStartDate();
            String end = project.getEndDate();
            startDate.setText(Util.getCurrentDate());
            endDate.setText(end);
        }

        Intent intent = getIntent();

        action = intent.getExtras().getString("action");

        if (action.equals("EDIT")){

            setTitle("Edit Pledge");

            long itemId = intent.getExtras().getLong("itemId");

            setup(itemId);

        }

        amount.addTextChangedListener( new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

              @Override
              public void onTextChanged(CharSequence s, int start, int before, int count) {

                String entered = amount.getText().toString().trim();

                if (!entered.equals("") && Integer.parseInt(entered) > 0) {

                  double tenPercent = Double.parseDouble(entered) * 10 / 100;

                  firstPayment.setText(""+tenPercent);
                }
              }

              @Override
              public void afterTextChanged(Editable s) {}
            });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setup(long id){

        pledge = realm.where(MemberPledge.class).equalTo("id", id).findFirst();

        if (pledge != null){

            pledgeStake = realm.where(PledgeStake.class).equalTo("pledgeStakeId", pledge.getPledgeStakeId()).findFirst();

            stake.setText(pledgeStake.getStake() + " ( " + Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(pledgeStake.getMinimum())) + " - " + Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(pledgeStake.getMaximum())) + " )");

            memberObject = realm.where(Member.class).equalTo("memberId", pledge.getMemberId()).findFirst();

            member.setText(memberObject.getFullName());

            paymentObject = realm.where(PaymentPeriod.class).equalTo("paymentPeriodId", pledge.getPaymentPeriodId()).findFirst();

            paymentPeriod.setText(paymentObject.getPeriod());

            amount.setText("" + pledge.getAmount());

            firstPayment.setText( pledge.getInitialPayment());

            startDate.setText( pledge.getStartDate() );
            endDate.setText(pledge.getEndDate());

        }
    }

    @OnClick(R.id.stake)
    public void onStakesTouch(){
        Intent intent = new Intent(this, StakesActivity.class);
        startActivityForResult(intent, REQUEST_STAKE_CODE);
    }

    @OnClick(R.id.member)
    public void onSeletMemberTouch(){
        Intent intent = new Intent(this, ManageMembersActivity.class);
        intent.putExtra("origin", "ADDPLEDGE");
        startActivityForResult(intent, REQUEST_MEMBER_CODE);
    }

    @OnClick(R.id.payment_period)
    public void onPaymentMethodTouch(){
        Intent intent = new Intent(this, PaymentPeriodsActivity.class);
        startActivityForResult(intent, REQUEST_PERIOD_CODE);
    }

    @OnClick(R.id.done)
    public void onDoneTouch(){

        //save and proceed
        String stakeValue = stake.getText().toString().trim();
        String memberValue = member.getText().toString().trim();
        String amountValue = amount.getText().toString().trim();
        String periodValue = paymentPeriod.getText().toString().trim();
        String paymentValue = firstPayment.getText().toString().trim();

        double minimum = Double.parseDouble(pledgeStake.getMinimum());
        double maximum = Double.parseDouble(pledgeStake.getMaximum());

        if (stakeValue.equals("")){
            stakeWrapper.setError("Stake field is required!");
            stakeWrapper.setErrorEnabled(true);
        }else if (memberValue.equals("")){
            memberWrapper.setErrorEnabled(true);
            memberWrapper.setError("Member field is required!");
        }else if (amountValue.equals("")){
            amountWrapper.setError("Pledge amount is required!");
            amountWrapper.setErrorEnabled(true);
        }else if (Double.parseDouble(amountValue) < minimum){
            amountWrapper.setError("Pledge amount must be between " + Util.formatMoney(Util.DECIMAL_FORMAT, minimum) + " - " + Util.formatMoney(Util.DECIMAL_FORMAT, maximum));
            amountWrapper.setErrorEnabled(true);
        }else if (Double.parseDouble(amountValue) > maximum){
            amountWrapper.setError("Pledge amount must be between " + Util.formatMoney(Util.DECIMAL_FORMAT, maximum) + " - " + Util.formatMoney(Util.DECIMAL_FORMAT, maximum));
            amountWrapper.setErrorEnabled(true);
        }else if (periodValue.equals("")){
            paymentPeriodWrapper.setErrorEnabled(true);
            paymentPeriodWrapper.setError("Payment period is required!");
        }else if (paymentValue.equals("")){
            firstPaymentWrapper.setError("First payment field is required!");
            firstPaymentWrapper.setErrorEnabled(true);
        }else{
            String tenPercent =  ""+Double.parseDouble(paymentValue);
            save(amountValue, tenPercent);
        }
    }

    private void save(final String amount, final String payment){

        if (action.equals("EDIT")){

            realm.executeTransaction(realm -> {

                pledge.setName(memberObject.getFullName() + "/ " + amount);
                pledge.setAmount(amount);
                pledge.setDatePledged(Util.getCurrentDate());
                pledge.setInitialPayment(payment);
                pledge.setContributed("0");
                pledge.setBalance(amount);
                pledge.setActive("true");
                pledge.setMemberId(memberObject.getMemberId());
                pledge.setUserId(""+user.getUserId());
                pledge.setPledgeStakeId(pledgeStake.getPledgeStakeId());
                pledge.setPaymentPeriodId(paymentObject.getPaymentPeriodId());
                pledge.setIsDeleted("false");
                pledge.setDeleterUserId(null);
                pledge.setDeletionTime(null);
                pledge.setLastModificationTime(null);
                pledge.setLastModifierUserId(null);
                pledge.setCreationTime(Util.getCurrentDate());
                pledge.setCreatorUserId(""+user.getUserId());
                pledge.setPledgeId(null);

                pledge.setStartDate(startDate.getText().toString().trim());
                pledge.setEndDate(endDate.getText().toString().trim());

                realm.copyToRealmOrUpdate(pledge);
            });

            Intent intent = new Intent(this, PledgeHistoryActivity.class);
            intent.putExtra("origin", "MAIN");
            startActivity( intent );

        }else{
            //insert to DB

            realm.executeTransaction(realm -> {

                MemberPledge memberPledge = new MemberPledge();

                RealmResults<MemberPledge> pledges = realm.where(MemberPledge.class).findAll().sort("id", Sort.ASCENDING);

                long lastPledgeId;

                if (pledges.isEmpty()){
                    memberPledge.setId(0);
                }else{
                    lastPledgeId = pledges.last().getId();
                    memberPledge.setId( lastPledgeId + 1);
                }

                memberPledge.setLocalPledgeId(Util.generateSixDigitNumber());
                memberPledge.setName(memberObject.getFullName() + "/ " + amount);
                memberPledge.setAmount(amount);
                memberPledge.setDatePledged(Util.getCurrentDate());
                memberPledge.setInitialPayment(payment);
                memberPledge.setContributed("0");
                memberPledge.setBalance(amount);
                memberPledge.setActive("true");
                memberPledge.setMemberId(memberObject.getMemberId());
                memberPledge.setUserId(""+user.getUserId());
                memberPledge.setPledgeStakeId(pledgeStake.getPledgeStakeId());
                memberPledge.setPaymentPeriodId(paymentObject.getPaymentPeriodId());
                memberPledge.setIsDeleted("false");
                memberPledge.setDeleterUserId(null);
                memberPledge.setDeletionTime(null);
                memberPledge.setLastModificationTime(null);
                memberPledge.setLastModifierUserId(null);
                memberPledge.setCreationTime(Util.getCurrentDate());
                memberPledge.setCreatorUserId(""+user.getUserId());
                memberPledge.setPledgeId(null);

                memberPledge.setStartDate(startDate.getText().toString().trim());
                memberPledge.setEndDate(endDate.getText().toString().trim());
                memberPledge.setStatus("Draft");

                realm.copyToRealm(memberPledge);

                Intent intent = new Intent(AddPledgeActivity.this, PledgeHistoryActivity.class);
                intent.putExtra("origin", "MAIN");
                startActivity( intent );
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_STAKE_CODE){
            if (resultCode == RESULT_OK){

                long stakeId = data.getExtras().getLong("id");

                pledgeStake = realm.where(PledgeStake.class).equalTo("id", stakeId).findFirst();

                stake.setText(pledgeStake.getStake() + " ( " + Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(pledgeStake.getMinimum())) + " - " + Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(pledgeStake.getMaximum())) + " )");
            }
        }else if (requestCode == REQUEST_MEMBER_CODE){
            if (resultCode == RESULT_OK){

                long memberId = data.getExtras().getLong("id");

                memberObject = realm.where(Member.class).equalTo("id", memberId).findFirst();

                member.setText(memberObject.getFullName());
            }
        }else if (requestCode == REQUEST_PERIOD_CODE){
            if (resultCode == RESULT_OK){

                long periodId = data.getExtras().getLong("id");

                paymentObject = realm.where(PaymentPeriod.class).equalTo("id", periodId).findFirst();

                paymentPeriod.setText(paymentObject.getPeriod());
            }
        }
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
                startActivity( new Intent(this, EndTimeMessageMainActivity.class));
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
