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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.asyncs.CreateAmortizationAsync;
import org.etma.main.asyncs.CreateMemberPledgeAsync;
import org.etma.main.asyncs.RequestAmortizationsAsync;
import org.etma.main.db.Amortization;
import org.etma.main.db.Contribution;
import org.etma.main.db.Member;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.PaymentPeriod;
import org.etma.main.db.PledgeStake;
import org.etma.main.db.Project;
import org.etma.main.db.UserOauth;
import org.etma.main.events.AmortizationCreatedEvent;
import org.etma.main.events.AmortizationReceivedEvent;
import org.etma.main.events.MemberPledgeCreatedEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.AmortizationList;
import org.etma.main.pojos.CreateBatchAmortizations;
import org.etma.main.pojos.CreateMemberPledge;
import org.etma.main.services.RequestAmortizationsService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class PledgeDetailsActivity extends AppCompatActivity {

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

    private List<Amortization> amortizationArrayList;
    private NetworkResolver resolver;

    private CatLoadingView mView;

    private int globalPledgeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pledge_details);
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

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
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

        startDate.setText(Util.getCurrentDate());

        endDate.setText(pledge.getEndDate());
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

        if (amortizationArrayList != null){
            amortizationArrayList.clear();
        }

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
    public void onAmortizeTouchEvent(){

        if (pledge.getStatus().equals("PROCESSING") || pledge.getStatus().equals("PROCESSED")) {

            Intent intent = new Intent(PledgeDetailsActivity.this, AmortizationDetailActivity.class);
            intent.putExtra("memberPledgeId", pledge.getPledgeId());
            intent.putExtra("origin", "MAIN");
            startActivity(intent);

        }else{

            DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
            LocalDate start = formatter.parseLocalDate(startDate.getText().toString());
            LocalDate end = formatter.parseLocalDate(endDate.getText().toString());

            LocalDate expiry = formatter.parseLocalDate(project.getEndDate());

            LocalDate today = new LocalDate();

            boolean isBeforeToday = start.isBefore(today);
            boolean isAfterDeadline = end.isAfter(expiry);
            boolean isSameAsEnd = end.isEqual(expiry);

            if (isBeforeToday){
                Util.showSnackBar(this, coordinatorLayout, "Start date cannot be earlier than today!");
            }else if(isAfterDeadline){
                Util.showSnackBar(this, coordinatorLayout, "End date cannot be after " + expiry.toString("MM/dd/yyyy"));
            }else{

                amortize( start, end );

                updatePledgeForSyncing(pledge);

            }

        }

    }

    private void updatePledgeForSyncing(final org.etma.main.db.MemberPledge pledgeToUpdate){

        realm.executeTransaction(realm -> {

            pledgeToUpdate.setStartDate(startDate.getText().toString().trim());
            pledgeToUpdate.setEndDate(endDate.getText().toString().trim());
            pledgeToUpdate.setStatus("PROCESSING");
            realm.copyToRealmOrUpdate(pledgeToUpdate);

        });

        if (resolver.isConnected()){

            if (!mView.isAdded()){
                showDialog();
            }

            String pledgeJson = generateMemberPledgeApiJson(pledgeToUpdate);

            CreateMemberPledgeAsync async = new CreateMemberPledgeAsync(this);
            async.execute(pledgeJson);
        }else{
            Util.showSnackBar(this, coordinatorLayout, "Turn ON your 4G data to connect!");
        }

    }

    private void amortize( LocalDate start, LocalDate end){

        int months = Months.monthsBetween( start, end ).getMonths();

        int years = Years.yearsBetween(start, end).getYears();

        String paymentPeriodId = pledge.getPaymentPeriodId();

        PaymentPeriod period = realm.where(PaymentPeriod.class).equalTo("paymentPeriodId", paymentPeriodId).findFirst();

        if ( null != period ){

            switch (period.getPeriod()){

                case "Monthly":
                    saveMonthly( months, start );
                    break;
                case "Quarterly":
                    saveQuarterly( months, start );
                    break;
                case "One off":
                    saveOneOff( start );
                case "Flexi Plan":
                    saveFlexiPlan( start );
                    break;
                case "Yearly":
                    saveYearly( years, start );
                    break;
                default:
                    break;
            }

        }

    }

    private void saveMonthly(int months, LocalDate start){

        amortizationArrayList = new ArrayList<>();

        int totalPledged = Integer.parseInt(pledge.getAmount());

        int monthlyInstallments = Integer.parseInt(pledge.getAmount()) / months;

        int balance = totalPledged;

        for (int i = 1; i <= months; i ++){

            Amortization amortization = new Amortization();

            amortization.setId(i);

            balance -= monthlyInstallments;

            amortization.setAmortizationId(0);
            amortization.setAmount(monthlyInstallments);
            amortization.setBalance(balance);
            amortization.setContributed(0);
            String nextPayDate = start.plusMonths(i).toString("MM/dd/yyyy");
            amortization.setContributionDate(nextPayDate);
            amortization.setCreationTime(Util.getCurrentDate());
            amortization.setCreatorUserId(user.getUserId());
            amortization.setDateContributed(start.toString("MM/dd/yyyy"));
            amortization.setDeleted(false);
            amortization.setDeleterUserId(0);
            amortization.setDeletionTime(Util.getCurrentDate());
            amortization.setFullyContributed(false);
            amortization.setLastModificationTime(Util.getCurrentDate());
            amortization.setLastModifierUserId(user.getUserId());
            amortization.setLocalPledgeId(pledge.getLocalPledgeId());
            amortization.setMemberId(Integer.parseInt(pledge.getMemberId()));
            amortization.setStatus("Draft");
            amortization.setUserId(user.getUserId());

            amortizationArrayList.add(amortization);

        }

    }

    private void saveQuarterly(int months, LocalDate start){

        amortizationArrayList = new ArrayList<>();

        int totalPledged = Integer.parseInt(pledge.getAmount());

        int quarters = months / 3;

        int balance = totalPledged;

        int monthlyInstallments = Integer.parseInt(pledge.getAmount()) / quarters;

        for (int i = 3; i <= months; i+=3){

            Amortization amortization = new Amortization();

            amortization.setId(i);

            balance -= monthlyInstallments;

            amortization.setAmortizationId(0);
            amortization.setAmount(monthlyInstallments);
            amortization.setBalance(balance);
            amortization.setContributed(0);
            String nextPayDate = start.plusMonths(i).toString("MM/dd/yyyy");
            amortization.setContributionDate(nextPayDate);
            amortization.setCreationTime(Util.getCurrentDate());
            amortization.setCreatorUserId(user.getUserId());
            amortization.setDateContributed(start.toString("MM/dd/yyyy"));
            amortization.setDeleted(false);
            amortization.setDeleterUserId(0);
            amortization.setDeletionTime(Util.getCurrentDate());
            amortization.setFullyContributed(false);
            amortization.setLastModificationTime(Util.getCurrentDate());
            amortization.setLastModifierUserId(user.getUserId());
            amortization.setLocalPledgeId(pledge.getLocalPledgeId());
            amortization.setMemberId(Integer.parseInt(pledge.getMemberId()));
            amortization.setStatus("Draft");
            amortization.setUserId(user.getUserId());

            amortizationArrayList.add(amortization);

        }


    }

    private void saveOneOff( LocalDate start ){

        amortizationArrayList = new ArrayList<>();

        int totalPledged = Integer.parseInt(pledge.getAmount());

        Amortization amortization = new Amortization();

        amortization.setId(0);

        amortization.setAmortizationId(0);
        amortization.setAmount(totalPledged);
        amortization.setBalance(0);
        amortization.setContributed(0);
        amortization.setContributionDate(start.toString("MM/dd/yyyy"));
        amortization.setCreationTime(Util.getCurrentDate());
        amortization.setCreatorUserId(user.getUserId());
        amortization.setDateContributed(start.toString("MM/dd/yyyy"));
        amortization.setDeleted(false);
        amortization.setDeleterUserId(0);
        amortization.setDeletionTime(Util.getCurrentDate());
        amortization.setFullyContributed(false);
        amortization.setLastModificationTime(Util.getCurrentDate());
        amortization.setLastModifierUserId(user.getUserId());
        amortization.setLocalPledgeId(pledge.getLocalPledgeId());
        amortization.setMemberId(Integer.parseInt(pledge.getMemberId()));
        amortization.setStatus("Draft");
        amortization.setUserId(user.getUserId());

        amortizationArrayList.add(amortization);

        Log.d("ONEOFFPLAN", "The one off plan selected: " + amortizationArrayList.size());

    }

    private void saveFlexiPlan( LocalDate start ){

        amortizationArrayList = new ArrayList<>();

        int totalPledged = Integer.parseInt(pledge.getAmount());

        Amortization amortization = new Amortization();

        amortization.setId(0);

        amortization.setAmortizationId(0);
        amortization.setAmount(totalPledged);
        amortization.setBalance(0);
        amortization.setContributed(0);
        amortization.setContributionDate(start.toString("MM/dd/yyyy"));
        amortization.setCreationTime(Util.getCurrentDate());
        amortization.setCreatorUserId(user.getUserId());
        amortization.setDateContributed(start.toString("MM/dd/yyyy"));
        amortization.setDeleted(false);
        amortization.setDeleterUserId(0);
        amortization.setDeletionTime(Util.getCurrentDate());
        amortization.setFullyContributed(false);
        amortization.setLastModificationTime(Util.getCurrentDate());
        amortization.setLastModifierUserId(user.getUserId());
        amortization.setLocalPledgeId(pledge.getLocalPledgeId());
        amortization.setMemberId(Integer.parseInt(pledge.getMemberId()));
        amortization.setStatus("Draft");
        amortization.setUserId(user.getUserId());

        amortizationArrayList.add(amortization);

    }

    private void saveYearly(int years, LocalDate start){

        amortizationArrayList = new ArrayList<>();

        int totalPledged = Integer.parseInt(pledge.getAmount());

        int balance = totalPledged;

        int yearlyInstallments = Integer.parseInt(pledge.getAmount()) / years;

        for (int i = 1; i <= years; i ++){

            Amortization amortization = new Amortization();

            amortization.setId(i);

            balance -= yearlyInstallments;

            amortization.setAmortizationId(0);
            amortization.setAmount(yearlyInstallments);
            amortization.setBalance(balance);
            amortization.setContributed(0);
            amortization.setContributionDate(start.plusYears(i).toString("MM/dd/yyyy"));
            amortization.setCreationTime(Util.getCurrentDate());
            amortization.setCreatorUserId(user.getUserId());
            amortization.setDateContributed(start.toString("MM/dd/yyyy"));
            amortization.setDeleted(false);
            amortization.setDeleterUserId(0);
            amortization.setDeletionTime(Util.getCurrentDate());
            amortization.setFullyContributed(false);
            amortization.setLastModificationTime(Util.getCurrentDate());
            amortization.setLastModifierUserId(user.getUserId());
            amortization.setLocalPledgeId(pledge.getLocalPledgeId());
            amortization.setMemberId(Integer.parseInt(pledge.getMemberId()));
            amortization.setStatus("Draft");
            amortization.setUserId(user.getUserId());

            amortizationArrayList.add(amortization);

        }


    }

    private String generateMemberPledgeApiJson(MemberPledge draft) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateMemberPledge memberPledge = new CreateMemberPledge();

        memberPledge.setName(draft.getName());
        memberPledge.setAmount(draft.getAmount());
        memberPledge.setDatePledged(draft.getDatePledged());
        memberPledge.setInitialPayment(draft.getInitialPayment());
        memberPledge.setContributed(draft.getContributed());
        memberPledge.setBalance(draft.getBalance());
        memberPledge.setActive(draft.getActive());
        memberPledge.setMemberId(draft.getMemberId());
        memberPledge.setUserId(draft.getUserId());
        memberPledge.setPledgeStakeId(draft.getPledgeStakeId());
        memberPledge.setPaymentPeriodId(draft.getPaymentPeriodId());
        memberPledge.setIsDeleted(draft.getIsDeleted());
        memberPledge.setDeleterUserId(draft.getDeleterUserId());
        memberPledge.setDeletionTime(draft.getDeletionTime());
        memberPledge.setLastModificationTime(draft.getLastModificationTime());
        memberPledge.setLastModifierUserId(draft.getLastModifierUserId());
        memberPledge.setCreationTime(draft.getCreationTime());
        memberPledge.setCreatorUserId(draft.getCreatorUserId());
        memberPledge.setId(null);

        String json = gson.toJson(memberPledge);

        Util.prettyPrintJson(json);

        return json;

    }

    @Subscribe
    public void onEvent(final MemberPledgeCreatedEvent event){

        //dismiss dialog here if any

        runOnUiThread(() -> mView.dismiss());

        if (event.isSuccess()){

            globalPledgeId = event.getPledgeId();

            runOnUiThread(() -> realm.executeTransaction(realm -> {

                pledge.setStatus("PROCESSED");
                pledge.setPledgeId(String.valueOf(event.getPledgeId()));
                realm.copyToRealmOrUpdate(pledge);

            }));

            //make api call to server;
            if (resolver.isConnected()){

                //get new amortizations from server
                RequestAmortizationsAsync async = new RequestAmortizationsAsync(PledgeDetailsActivity.this);
                async.execute();

                if (!mView.isAdded()){
                    showDialog();
                }

            }

        }else{

            //something went wrong syncing pledge;
            Util.showSnackBar(this, coordinatorLayout, "Pledge creation failed. Try again later!");
        }
    }

    @Subscribe
    public void onEvent(AmortizationReceivedEvent event){

        if (event.isSuccess()){

            if (event.getTotal() == 0){

                if (resolver.isConnected()){

                    String json = prepareAmortizationJson(Integer.parseInt(pledge.getPledgeId()));

                    CreateAmortizationAsync async = new CreateAmortizationAsync(this);

                    async.execute( json );

                    if (!mView.isAdded()){
                        showDialog();
                    }

                }else{
                    Util.showSnackBar(this, coordinatorLayout, "Turn ON your 4G data to connect!");
                }

            }else{

                if (resolver.isConnected()){

                    RealmResults<Amortization> pledgeAmortizations = realm.where(Amortization.class)
                                .equalTo("memberPledgeId", Integer.parseInt(pledge.getPledgeId())).findAll();

                    if (pledgeAmortizations.isEmpty()){

                        String json = prepareAmortizationJson(Integer.parseInt(pledge.getPledgeId()));

                        CreateAmortizationAsync async = new CreateAmortizationAsync(this);

                        async.execute( json );

                        if (!mView.isAdded()){
                            showDialog();
                        }

                    }

                }else{
                    Util.showSnackBar(this, coordinatorLayout, "Turn ON your 4G data to connect!");
                }

            }

        }
    }

    @Subscribe
    public void onEvent(final AmortizationCreatedEvent event){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mView.dismiss();

                if (event.isSuccess()){

                    Intent amortizationService = new Intent(PledgeDetailsActivity.this, RequestAmortizationsService.class);
                    startService(amortizationService);

                    Intent intent = new Intent(PledgeDetailsActivity.this, AmortizationDetailActivity.class);
                    intent.putExtra("memberPledgeId", pledge.getPledgeId());
                    intent.putExtra("origin", "MAIN");
                    startActivity(intent);
                }else{
                    Util.showSnackBar(PledgeDetailsActivity.this, coordinatorLayout, "Pledge plan creation failed. Try again later!");
                }

            }
        });

    }

    private String prepareAmortizationJson(int pledgeId){

        Set<Amortization> set = new HashSet<>(amortizationArrayList);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateBatchAmortizations batchAmortizations = new CreateBatchAmortizations();

        List<AmortizationList> amortizationList = new ArrayList<>();

        for (Amortization draft : set){

            AmortizationList amortization = new AmortizationList();

            amortization.setAmount(draft.getAmount());
            amortization.setContributionDate(draft.getContributionDate());
            amortization.setDateContributed(draft.getDateContributed());
            amortization.setFullyContributed(draft.isFullyContributed());
            amortization.setContributed(draft.getContributed());
            amortization.setBalance(draft.getBalance());
            amortization.setUserId(draft.getUserId());
            amortization.setMemberPledgeId(pledgeId);
            amortization.setMemberId(draft.getMemberId());
            amortization.setIsDeleted(draft.isDeleted());
            amortization.setDeleterUserId(draft.getDeleterUserId());
            amortization.setDeletionTime(draft.getDeletionTime());
            amortization.setLastModificationTime(draft.getLastModificationTime());
            amortization.setLastModifierUserId(draft.getLastModifierUserId());
            amortization.setCreationTime(draft.getCreationTime());
            amortization.setCreatorUserId(draft.getCreatorUserId());
            amortization.setId(null);

            amortizationList.add(amortization);
        }

        batchAmortizations.setList(amortizationList);

        return gson.toJson(batchAmortizations);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mView.isAdded()){
            mView.dismiss();
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

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        public static boolean isStartDate;

        public static DatePickerFragment getInstance(boolean type){

            isStartDate = type;

            return new DatePickerFragment();
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
