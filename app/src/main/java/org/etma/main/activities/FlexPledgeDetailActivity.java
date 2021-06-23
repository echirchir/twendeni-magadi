package org.etma.main.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.db.Contribution;
import org.etma.main.db.Member;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.PaymentPeriod;
import org.etma.main.db.PledgeStake;
import org.etma.main.db.Project;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FlexPledgeDetailActivity extends AppCompatActivity {

    private Realm realm;

    @BindView(R.id.target_amount)
    TextView targetAmount;

    @BindView(R.id.my_contributions)
    TextView my_contributions;

    @BindView(R.id.my_target)
    TextView myTarget;

    @BindView(R.id.total_contributed)
    TextView all_contributions;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.member_name)
    TextView memberName;

    @BindView(R.id.pledge_name)
    TextView pledgeName;

    @BindView(R.id.stake)
    TextView pledgeStake;

    @BindView(R.id.pledge_amount)
    TextView pledgeAmount;

    @BindView(R.id.contributed)
    TextView contributed;

    @BindView(R.id.payment_period)
    TextView paymentPeriod;

    private static EditText startDate;

    @BindView(R.id.start_date_wrapper)
    TextInputLayout startDateWrapper;

    private static EditText endDate;

    @BindView(R.id.end_date_wrapper)
    TextInputLayout endDateWrapper;

    @BindView(R.id.amortize)
    AppCompatButton amortize;

    private org.etma.main.db.MemberPledge pledge;
    private long pledgeId;
    private Project project;
    private UserOauth user;
    private NetworkResolver resolver;

    private CatLoadingView mView;

    private int globalPledgeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flex_pledge_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        user = realm.where(UserOauth.class).findFirst();

        project = realm.where(Project.class).findFirst();

        resolver = new NetworkResolver(this);

        if (project != null){

            setupStatsLabels( project );

        }

        mView = new CatLoadingView();

        startDate = (EditText) findViewById(R.id.start_date);
        endDate = (EditText) findViewById(R.id.end_date);

        Intent intent = getIntent();

        if (intent != null){
            pledgeId = intent.getExtras().getLong("id");
            pledge = realm.where(org.etma.main.db.MemberPledge.class).equalTo("id", pledgeId).findFirst();

            if (pledge != null){

                setup(pledge);

                if (pledge.getStatus().equals("PROCESSING") || pledge.getStatus().equals("PROCESSED")){
                    amortize.setText(R.string.view_amortizations);
                }
            }

        }


    }

    private void setup(org.etma.main.db.MemberPledge pledge){

        Member member = realm.where(Member.class).equalTo("memberId", pledge.getMemberId()).findFirst();

        if (member != null){
            memberName.setText(member.getFullName());
        }

        pledgeName.setText(pledge.getName());
        PledgeStake stake = realm.where(PledgeStake.class).equalTo("pledgeStakeId", pledge.getPledgeStakeId()).findFirst();
        pledgeStake.setText(stake.getStake() + " ( " + Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(stake.getMinimum())) + " - " + Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(stake.getMaximum())) + " )");
        pledgeAmount.setText(Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(pledge.getAmount())));
        PaymentPeriod period = realm.where(PaymentPeriod.class).equalTo("paymentPeriodId", pledge.getPaymentPeriodId()).findFirst();
        paymentPeriod.setText(period.getPeriod());
        contributed.setText( Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(pledge.getBalance() )));

        startDate.setText(pledge.getStartDate());

        endDate.setText(project.getEndDate());
    }

    private void setupStatsLabels(Project project){

        String target = project.getProjectTarget();

        if (target != null){
            targetAmount.setText(Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(project.getProjectTarget())));
        }else{
            targetAmount.setText("0");
        }

        String totalContributions = project.getTotalContributions();

        if (project.getTotalContributions() != null){

            if (!totalContributions.equals("")){

                double contributions = Double.parseDouble(project.getTotalContributions());

                if (contributions > 0){

                    all_contributions.setText(Util.formatMoney(Util.DECIMAL_FORMAT, contributions));
                }else{
                    all_contributions.setText("0");
                }
            }else{
                all_contributions.setText("0");
            }
        }else{
            all_contributions.setText("0");
        }

        RealmResults<MemberPledge> pledges = realm.where(MemberPledge.class).findAll().sort("id", Sort.ASCENDING);

        double myPledges = computePledges(pledges);

        myTarget.setText(Util.formatMoney(Util.DECIMAL_FORMAT, myPledges));

        double totalContribs = computeContributions();

        my_contributions.setText(Util.formatMoney(Util.DECIMAL_FORMAT, totalContribs));

    }

    private double computeContributions(){

        double sum = 0;

        RealmResults<Contribution> all = realm.where(Contribution.class).equalTo("verified", "true").findAll().sort("id", Sort.ASCENDING);

        if (!all.isEmpty()){

            for (Contribution contribution  : all){

                sum += Double.parseDouble(contribution.getAmount());
            }
        }

        return sum;
    }

    private double computePledges(RealmResults<MemberPledge> pledges){

        double total = 0.0;

        if (!pledges.isEmpty()){

            for (MemberPledge pledge : pledges){

                total += Double.parseDouble(pledge.getAmount());
            }

            return total;
        }

        return total;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mView.isVisible()){

            mView.dismiss();
        }

        if (pledge != null){

            setup(pledge);

            if (pledge.getStatus().equals("PROCESSING") || pledge.getStatus().equals("PROCESSED")){
                amortize.setText(R.string.view_amortizations);
                startDate.setClickable(false);
                endDate.setClickable(false);
            }
        }
    }

    @OnClick(R.id.start_date)
    public void onStartDateTouched(){

        if (pledge.getStatus().equals("PROCESSING") || pledge.getStatus().equals("PROCESSED")){
            Util.showSnackBar(this, coordinatorLayout, "This pledge has already been completed!");
        }else{
            boolean isStart = true;

            DialogFragment newFragment = DatePickerFragment.getInstance(isStart);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        }

    }

    @OnClick(R.id.end_date)
    public void onEndDateTouched(){

        if (pledge.getStatus().equals("PROCESSING") || pledge.getStatus().equals("PROCESSED")){
            Util.showSnackBar(this, coordinatorLayout, "This pledge has already been completed!");
        }else{
            boolean isStart = false;
            DialogFragment newFragment = DatePickerFragment.getInstance(isStart);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        }

    }

    @OnClick(R.id.amortize)
    public void onViewMyPlan(){

        if (pledge.getStatus().equals("PROCESSED")){
            Intent intent = new Intent(this, AmortizationDetailActivity.class);
            intent.putExtra("memberPledgeId", pledge.getPledgeId());
            intent.putExtra("origin", "MAIN");
            startActivity(intent);
        }else{

            updatePledgeForSyncing( pledge );

            //transition to details activity for flexi plan;
            Intent intent = new Intent(this, FlexPlanPledgeActivity.class);
            intent.putExtra("origin", "MAIN");
            intent.putExtra("id", pledge.getId());
            startActivity(intent);
        }

    }

    private void updatePledgeForSyncing(final MemberPledge pledgeToUpdate){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {

                pledgeToUpdate.setStartDate(startDate.getText().toString().trim());
                pledgeToUpdate.setEndDate(endDate.getText().toString().trim());
                realm.copyToRealmOrUpdate(pledgeToUpdate);
            }
        });

    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        public static boolean isStartDate;

        public static PledgeDetailsActivity.DatePickerFragment getInstance(boolean type){

            isStartDate = type;

            return new PledgeDetailsActivity.DatePickerFragment();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {

            String datePicked = (month + 1) + "/" + day + "/" + year;
            updateView(datePicked, isStartDate);
        }
    }

    private static void updateView(String date, boolean isStart) {

        if (isStart)
            startDate.setText(date);
        else
            endDate.setText(date);
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
