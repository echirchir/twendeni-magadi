package org.etma.main.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.adapters.AmortizationAdapter;
import org.etma.main.asyncs.CreateFlexiAmortizationAsync;
import org.etma.main.asyncs.CreateFlexiPledgeAsync;
import org.etma.main.asyncs.RequestFlexiAmortizationsAsync;
import org.etma.main.db.Amortization;
import org.etma.main.db.Contribution;
import org.etma.main.db.FlexiAmortization;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.Project;
import org.etma.main.db.UserOauth;
import org.etma.main.events.FlexiAmortizationCreatedEvent;
import org.etma.main.events.FlexiAmortizationReceivedEvent;
import org.etma.main.events.FlexiPledgeCreatedEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.AmortizationList;
import org.etma.main.pojos.CreateBatchAmortizations;
import org.etma.main.pojos.CreateMemberPledge;
import org.etma.main.services.RequestAmortizationsService;
import org.etma.main.ui.AmortizationObject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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

public class FlexPlanPledgeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Realm realm;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_amortizations)
    TextView noAmortizations;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.target_amount)
    TextView targetAmount;

    @BindView(R.id.my_contributions)
    TextView my_contributions;

    @BindView(R.id.my_target)
    TextView myTarget;

    @BindView(R.id.total_contributed)
    TextView all_contributions;

    private static EditText startDate;

    @BindView(R.id.date_wrapper)
    TextInputLayout startDateWrapper;

    @BindView(R.id.amount_wrapper)
    TextInputLayout amountWrapper;

    @BindView(R.id.amount)
    EditText amount;

    private int totalPledgeAmount;

    private String originatingActivity;

    private List<AmortizationObject> amortizations;
    private AmortizationAdapter adapter;
    private MemberPledge pledge;

    private UserOauth user;

    private NetworkResolver resolver;

    private CatLoadingView mView;

    private long pledgeId;

    private LinearLayout selectedView;
    private int selectedItemPosition = -1;
    private ActionMode currentActionMode;

    @BindView(R.id.addAmortization)
    AppCompatButton addAmortization;

    private boolean isInEditMode = false;

    private Project project;

    private int globalPledgeId;

    private List<Amortization> amortizationArrayList;

    private RealmResults<FlexiAmortization> pledgeAmortizations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flex_plan_pledge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        project = realm.where(Project.class).findFirst();

        resolver = new NetworkResolver(this);

        user = realm.where(UserOauth.class).findFirst();

        mView = new CatLoadingView();

        Project project = realm.where(Project.class).findFirst();

        if (project != null){

            setupStatsLabels( project );

        }

        startDate = (EditText) findViewById(R.id.date);

        startDate.setText(Util.getCurrentDate());

        Intent intent = getIntent();

        if (intent != null){
            pledgeId = intent.getExtras().getLong("id");
            pledge = realm.where(MemberPledge.class).equalTo("id", pledgeId).findFirst();

            if (pledge.getStatus().equals("Draft")){

                RealmResults<FlexiAmortization> flexiAmortizations = realm.where(FlexiAmortization.class)
                        .equalTo("localPledgeId", pledge.getLocalPledgeId())
                        .findAll();

                //insert initial amortization item at index [0];
                if (flexiAmortizations.isEmpty()){

                    saveInitialFlexiPlanIfMissing();

                    loadAmortizationsForPledge();
                }
            }else{
                RealmResults<Amortization> existingAmos = realm.where(Amortization.class)
                        .greaterThan("balance", 0)
                        .equalTo("memberPledgeId", Integer.parseInt(pledge.getPledgeId())).findAll();

                if (existingAmos.isEmpty()){

                    saveInitialFlexiPlanIfMissing();

                    loadAmortizationsForPledge();
                }
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, RecyclerViewItemDecorator.VERTICAL_LIST));

        mRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                long amortizationId = adapter.getItemId(position);
                long localPledgeId = pledge.getLocalPledgeId();

                Amortization amortization = realm.where(Amortization.class).equalTo("id", amortizationId).findFirst();

                Intent intent = new Intent(FlexPlanPledgeActivity.this, PaymentModesActivity.class);

                //proceed to payment;
                if (originatingActivity.equals("CONTRIBUTIONS")){

                    intent.putExtra("amortizationId", amortization.getId());
                    intent.putExtra("origin", "CONTRIBUTIONS");
                    intent.putExtra("localPledgeId", localPledgeId);

                }else{

                    //non-contributions
                    intent.putExtra("amortizationId", amortization.getId());
                    intent.putExtra("origin", "DEFAULT");
                    intent.putExtra("localPledgeId", localPledgeId);
                }

                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {

                selectedItemPosition = position;

                if (position == 0){
                    Util.showSnackBar(FlexPlanPledgeActivity.this, mCoordinatorLayout, "You cannot edit the default plan!");
                }else{
                    if (currentActionMode != null) {
                        return;
                    }

                    selectedView = (LinearLayout) view;

                    selectedView.setBackgroundColor(ContextCompat.getColor(FlexPlanPledgeActivity.this, R.color.blue));
                    currentActionMode = startSupportActionMode(modeCallBack);
                    view.setSelected(true);

                    isInEditMode = true;
                }


            }

        }));

    }

    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(getResources().getString(R.string.actions));
            mode.getMenuInflater().inflate(R.menu.action_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                case R.id.action_edit:
                    handleEdit(selectedItemPosition);
                    mode.finish();
                    return true;
                case R.id.action_delete:
                    handleDelete(selectedItemPosition);
                    mode.finish();
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            currentActionMode = null;

            if (selectedView != null){
                selectedView.setBackgroundColor(ContextCompat.getColor(FlexPlanPledgeActivity.this, R.color.white));
            }

        }
    };

    private void handleEdit(int selectedItemPosition) {

        long id = adapter.getItemId(selectedItemPosition);

        FlexiAmortization amortization = realm.where(FlexiAmortization.class).equalTo("id", id).findFirst();

        if (amortization != null){

            startDate.setText(amortization.getContributionDate());
            amount.setText(""+amortization.getBalance());

            addAmortization.setText("UPDATE");

            isInEditMode = true;
        }


    }

    private void handleDelete(int selectedItemPosition) {

        long id = adapter.getItemId(selectedItemPosition);

        RealmResults<FlexiAmortization> amortizations = realm.where(FlexiAmortization.class).equalTo("id", id).findAll();

        confirmDeleteAction(amortizations, selectedItemPosition);

    }

    private void confirmDeleteAction(final RealmResults<FlexiAmortization> flexiAmortizations, final int position){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete_flex_pledge))
                .setMessage(getString(R.string.delete_flex_pledge_desc))
                .setCancelable(false)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        realm.beginTransaction();
                        flexiAmortizations.deleteAllFromRealm();

                        realm.commitTransaction();

                        adapter.remove(position);
                        adapter.notifyItemRemoved(position);

                        harmonizeInitialAmortizationAmount();

                        if (adapter.getItemCount() == 1){

                            final FlexiAmortization amortization = realm.where(FlexiAmortization.class)
                                    .equalTo("localPledgeId", pledge.getLocalPledgeId())
                                    .findFirst();

                            realm.beginTransaction();

                            amortization.setAmount(Integer.parseInt(pledge.getBalance()));
                            amortization.setBalance(Integer.parseInt(pledge.getBalance()));

                            realm.copyToRealmOrUpdate(amortization);
                            realm.commitTransaction();

                            harmonizeInitialAmortizationAmount();

                            loadAmortizationsForPledge();

                        }

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.date)
    public void onDateEventTouched(){
        boolean isStart = true;

        DialogFragment newFragment = DatePickerFragment.getInstance(isStart);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
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

    private void loadAmortizationsForPledge(){

        amortizations = new ArrayList<>();

        if (pledge.getStatus().equals("PROCESSED")){
            pledgeAmortizations = realm.where(FlexiAmortization.class)
                    .greaterThan("balance", 0)
                    .equalTo("memberPledgeId", Integer.parseInt(pledge.getPledgeId())).findAll();
        }else{
            pledgeAmortizations  = realm.where(FlexiAmortization.class)
                    .greaterThan("balance", 0)
                    .equalTo("localPledgeId", pledge.getLocalPledgeId()).findAll();
        }

        if (!pledgeAmortizations.isEmpty()){

            for (FlexiAmortization amortization : pledgeAmortizations){

                String contribDate;

                if (pledge.getStatus().equals("PROCESSED")){

                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

                    DateTime preliminaryEnd = formatter.parseDateTime(amortization.getContributionDate());
                    LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                    LocalDate prelimDue = new LocalDate(formattedEnd);

                    String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                    LocalDate contributionDate = formatter.parseLocalDate(actualDueDate);

                    contribDate = contributionDate.toString("MM/dd/yyyy");
                }else{
                    contribDate = amortization.getContributionDate();
                }

                totalPledgeAmount += amortization.getBalance();

                amortizations.add( new AmortizationObject(amortization.getId(), amortization.getLocalPledgeId(), contribDate, amortization.getBalance(), amortization.getContributed(), amortization.getBalance(), amortization.getAmortizationId()));
            }
        }

        adapter = new AmortizationAdapter(amortizations, this, realm);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            noAmortizations.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        addAmortization.setText("ADD");

    }

    private void saveInitialFlexiPlanIfMissing(){

        RealmResults<FlexiAmortization> flexiAmortizations = realm.where(FlexiAmortization.class).findAll().sort("id", Sort.ASCENDING);

        FlexiAmortization amortization = new FlexiAmortization();

        long lastId;

        if (flexiAmortizations.isEmpty()){
            amortization.setId(0);
        }else{
            lastId = flexiAmortizations.last().getId();
            amortization.setId( lastId + 1);
        }

        int totalPledged = Integer.parseInt(pledge.getAmount());

        amortization.setAmortizationId(0);
        amortization.setAmount(totalPledged);
        amortization.setBalance(totalPledged);
        amortization.setContributed(0);
        amortization.setContributionDate(Util.getCurrentDate());
        amortization.setCreationTime(Util.getCurrentDate());
        amortization.setCreatorUserId(user.getUserId());
        amortization.setDateContributed(pledge.getStartDate());
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

        realm.beginTransaction();
        realm.copyToRealm(amortization);
        realm.commitTransaction();

    }

    @OnClick(R.id.addAmortization)
    public void onAddAmortization(){

        //validate and save to DB; then start syncing to server;

        String date = startDate.getText().toString().trim();
        final String totalAmortization = amount.getText().toString().trim();

        if (date.equals("")){
            startDateWrapper.setErrorEnabled(true);
            startDateWrapper.setError("Date field is required!");
        }else if (totalAmortization.equals("") || Integer.parseInt(totalAmortization) < 1){
            amountWrapper.setErrorEnabled(true);
            amountWrapper.setError("Enter a valid amount!");
        }else if (Integer.parseInt(totalAmortization) > totalPledgeAmount){
            amountWrapper.setErrorEnabled(true);
            amountWrapper.setError("Amount entered exceeds pledge amount!");
        }else{

            if (isInEditMode){

                long id = adapter.getItemId(selectedItemPosition);

                final FlexiAmortization amortization = realm.where(FlexiAmortization.class).equalTo("id", id).findFirst();

                updateAmortization(amortization, date, Integer.parseInt(totalAmortization));

                harmonizeInitialAmortizationAmount();

                loadAmortizationsForPledge();

                startDate.setText(Util.getCurrentDate());
                amount.setText("");
                isInEditMode = false;

            }else{

                RealmResults<FlexiAmortization> flexiAmortizations = realm.where(FlexiAmortization.class).equalTo("contributionDate", date).findAll();

                if (!flexiAmortizations.isEmpty()){
                    Util.showSnackBar(this, mCoordinatorLayout, "The selected date is already added!");
                }else{
                    saveAmortization(date, Integer.parseInt(totalAmortization));

                    harmonizeInitialAmortizationAmount();

                    loadAmortizationsForPledge();

                    startDate.setText(Util.getCurrentDate());
                    amount.setText("");
                    isInEditMode = false;
                }

            }

        }

    }

    private void saveAmortization(String date, int amount){

        //validate here;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        LocalDate start = formatter.parseLocalDate(date);

        DateTime preliminaryEnd = formatter.parseDateTime(project.getEndDate());
        LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
        LocalDate prelimDue = new LocalDate(formattedEnd);

        String actualDueDate = prelimDue.toString("MM/dd/yyyy");

        LocalDate expiry = formatter.parseLocalDate(actualDueDate);

        if (start.isAfter(expiry)){
            startDateWrapper.setError("Date cannot be after " + actualDueDate);
            startDateWrapper.setErrorEnabled(true);
        }else{
            RealmResults<FlexiAmortization> all = realm.where(FlexiAmortization.class).findAll().sort("id", Sort.ASCENDING);

            final FlexiAmortization first = all.first();

            if( amount > first.getBalance() ){
                amountWrapper.setError("Amount entered exceeds the balance : " + first.getBalance());
                amountWrapper.setErrorEnabled(true);
            }else {
                RealmResults<FlexiAmortization> flexiAmortizations = realm.where(FlexiAmortization.class).findAll().sort("id", Sort.ASCENDING);

                FlexiAmortization amortization = new FlexiAmortization();

                long lastId;

                if (flexiAmortizations.isEmpty()) {
                    amortization.setId(0);
                } else {
                    lastId = flexiAmortizations.last().getId();
                    amortization.setId(lastId + 1);
                }

                amortization.setAmortizationId(0);
                amortization.setAmount(amount);
                amortization.setBalance(amount);
                amortization.setContributed(0);
                amortization.setContributionDate(date);
                amortization.setCreationTime(Util.getCurrentDate());
                amortization.setCreatorUserId(user.getUserId());
                amortization.setDateContributed(date);
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

                realm.beginTransaction();
                realm.copyToRealm(amortization);
                realm.commitTransaction();
            }
        }

    }

    private void updateAmortization(FlexiAmortization amortization,  String date, int amount){

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        LocalDate start = formatter.parseLocalDate(date);

        LocalDate preliminaryEnd = formatter.parseLocalDate(project.getEndDate());
        LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
        LocalDate prelimDue = new LocalDate(formattedEnd);

        String actualDueDate = prelimDue.toString("MM/dd/yyyy");

        LocalDate expiry = formatter.parseLocalDate(actualDueDate);

        if (start.isAfter(expiry)){
            startDateWrapper.setError("Date cannot be after " + actualDueDate);
            startDateWrapper.setErrorEnabled(true);
        }else{
            RealmResults<FlexiAmortization> all = realm.where(FlexiAmortization.class).equalTo("localPledgeId", pledge.getLocalPledgeId())
                    .findAll().sort("id", Sort.ASCENDING);

            final FlexiAmortization first = all.first();

            //get all amortizations whose id is not being edited;
            long id = adapter.getItemId(selectedItemPosition);

            RealmResults<FlexiAmortization> notBeingEdited = realm.where(FlexiAmortization.class).notEqualTo("id", id).findAll();

            int total = 0;

            if (!notBeingEdited.isEmpty()){

                for (FlexiAmortization flexiAmortization : notBeingEdited){

                    total += flexiAmortization.getBalance();

                }
            }

            int pledgeAmount = Integer.parseInt(pledge.getBalance());

            int difference = pledgeAmount - total;

            if (( pledgeAmount - total) < amount){
                amountWrapper.setErrorEnabled(true);
                amountWrapper.setError(" The amount entered is higher by " + (amount - difference));
            }else{
                realm.beginTransaction();

                amortization.setAmount(amount);
                amortization.setBalance(amount);
                amortization.setContributionDate(date);

                realm.copyToRealmOrUpdate(amortization);
                realm.commitTransaction();
            }
        }

    }

    private void harmonizeInitialAmortizationAmount(){

        RealmResults<FlexiAmortization> all = realm.where(FlexiAmortization.class).findAll().sort("id", Sort.ASCENDING);

        final FlexiAmortization first = all.first();

        RealmResults<FlexiAmortization> allFlexis = realm.where(FlexiAmortization.class).greaterThan("id", 0).findAll().sort("id", Sort.ASCENDING);

        //loop through amortizations [start with index 1] ; sum up; subtract from pledge amount; update index 0 pledge balance;
        final int pledgeTotal = Integer.parseInt(pledge.getBalance());

        int totals = 0;

        if (!allFlexis.isEmpty()){

            for ( FlexiAmortization amortization : allFlexis ){

                totals += amortization.getBalance();

            }

            if (totals > 0){

                realm.beginTransaction();

                first.setAmount( pledgeTotal - totals );
                first.setBalance( pledgeTotal - totals );
                realm.copyToRealmOrUpdate( first );

                realm.commitTransaction();

            }
        }

        loadAmortizationsForPledge();

    }

    @OnClick(R.id.cancel)
    public void onCancelEvent(){

        //clear table and redirect to pledges history
        Intent intent = new Intent(this, PledgeHistoryActivity.class);
        intent.putExtra("origin", "MAIN");
        startActivity(intent);

        // TODO: 8/29/18 RESET DATABASE TABLE FOR PLEDGE
    }

    @OnClick(R.id.confirm)
    public void onConfirmationEvent(){
        //confirm dialog

        confirm();

    }

    private void confirm(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_amortize))
                .setMessage(getString(R.string.confirm_amortize_desc))
                .setCancelable(false)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                updatePledgeForSyncing( pledge );

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void updatePledgeForSyncing(final MemberPledge pledgeToUpdate){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                pledgeToUpdate.setStatus("PROCESSING");
                realm.copyToRealmOrUpdate(pledgeToUpdate);
            }
        });

        if (resolver.isConnected()){

            if (!mView.isAdded()){
                showDialog();
            }

            String pledgeJson = generateMemberPledgeApiJson(pledgeToUpdate);

            CreateFlexiPledgeAsync async = new CreateFlexiPledgeAsync(this);
            async.execute(pledgeJson);
        }else{
            Util.showSnackBar(this, mCoordinatorLayout, "Turn ON your 4G data to connect!");
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
    public void onEvent(FlexiPledgeCreatedEvent event){

        //dismiss dialog here if any

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mView.dismiss();

            }
        });

        if (event.isSuccess()){

            globalPledgeId = event.getPledgeId();

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            pledge.setStatus("PROCESSED");
                            pledge.setPledgeId(""+globalPledgeId);
                            realm.copyToRealmOrUpdate(pledge);
                        }
                    });

                }
            });

            //make api call to server;
            if (resolver.isConnected()){

                //get new amortizations from server
                RequestFlexiAmortizationsAsync async = new RequestFlexiAmortizationsAsync(FlexPlanPledgeActivity.this);
                async.execute();

                if (!mView.isAdded()){
                    showDialog();
                }

            }

        }else{

            //something went wrong syncing pledge;
            Util.showSnackBar(this, mCoordinatorLayout, "Pledge creation error. Try again later!");
        }
    }


    @Subscribe
    public void onEvent(FlexiAmortizationReceivedEvent event){

        if (event.isSuccess()){

            if (event.getTotal() == 0){

                if (resolver.isConnected()){

                    //prepare array list of flexi amortizations
                    prepareAmortizationList();

                    String json = prepareAmortizationJson(Integer.parseInt(pledge.getPledgeId()));

                    Util.prettyPrintJson(json);

                    CreateFlexiAmortizationAsync async = new CreateFlexiAmortizationAsync(this);

                    async.execute( json );

                    if (!mView.isAdded()){
                        showDialog();
                    }

                }else{
                    Util.showSnackBar(this, mCoordinatorLayout, "Turn ON your 4G data to connect!");
                }

            }else{

                if (resolver.isConnected()){

                    RealmResults<Amortization> pledgeAmortizations = realm.where(Amortization.class)
                            .equalTo("memberPledgeId", Integer.parseInt(pledge.getPledgeId())).findAll();

                    if (pledgeAmortizations.isEmpty()){

                        prepareAmortizationList();

                        String json = prepareAmortizationJson(Integer.parseInt(pledge.getPledgeId()));

                        Util.prettyPrintJson(json);

                        CreateFlexiAmortizationAsync async = new CreateFlexiAmortizationAsync(this);

                        async.execute( json );

                        if (!mView.isAdded()){
                            showDialog();
                        }

                    }else{
                        Intent intent = new Intent(FlexPlanPledgeActivity.this, AmortizationDetailActivity.class);
                        intent.putExtra("memberPledgeId", pledge.getPledgeId());
                        intent.putExtra("origin", "MAIN");
                        startActivity(intent);
                    }


                }else{
                    Util.showSnackBar(this, mCoordinatorLayout, "Turn ON your 4G data to connect!");
                }

            }

        }
    }

    private void prepareAmortizationList(){

        amortizationArrayList = new ArrayList<>();

        if (!pledgeAmortizations.isEmpty()){

            for (FlexiAmortization draft : pledgeAmortizations){

                Amortization amortization = new Amortization();

                amortization.setId(draft.getId());

                amortization.setAmortizationId(0);
                amortization.setAmount(draft.getAmount());
                amortization.setBalance(draft.getBalance());
                amortization.setContributed(0);
                amortization.setContributionDate(draft.getContributionDate());
                amortization.setCreationTime(Util.getCurrentDate());
                amortization.setCreatorUserId(user.getUserId());
                amortization.setDateContributed(draft.getContributionDate());
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

    }

    private String prepareAmortizationJson(int pledgeId){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        Set<Amortization> set = new HashSet<>(amortizationArrayList);

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

        String json = gson.toJson(batchAmortizations);

        Util.prettyPrintJson(json);

        return json;
    }

    @Subscribe
    public void onEvent(final FlexiAmortizationCreatedEvent event){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mView.dismiss();

                if (event.isSuccess()){

                    RealmResults<FlexiAmortization> toDelete = realm.where(FlexiAmortization.class).findAll().sort("id");

                    if (!toDelete.isEmpty()){

                        realm.beginTransaction();
                        toDelete.deleteAllFromRealm();
                        realm.commitTransaction();
                    }

                    Intent amortizationService = new Intent(FlexPlanPledgeActivity.this, RequestAmortizationsService.class);
                    startService(amortizationService);

                    Intent intent = new Intent(FlexPlanPledgeActivity.this, PledgeHistoryActivity.class);
                    intent.putExtra("origin", "MAIN");
                    startActivity(intent);

                }else{
                    Util.showSnackBar(FlexPlanPledgeActivity.this, mCoordinatorLayout, "Plan creation failed. Try again later!");
                }

            }
        });

    }


    @Override
    protected void onResume() {

        super.onResume();

        Intent intent = getIntent();

        if (intent != null){

            originatingActivity = intent.getExtras().getString("origin");
            pledgeId = intent.getExtras().getLong("id");

            loadAmortizationsForPledge();

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
        inflater.inflate(R.menu.global_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);

        item.expandActionView();

        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextListener(this);

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
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.equals("")){
            loadAmortizationsForPledge();
            return true;
        }else{
            final List<AmortizationObject> filteredModelList = filter(amortizations, newText);

            adapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
            return true;
        }

    }

    private List<AmortizationObject> filter(List<AmortizationObject> models, String query) {

        query = query.toLowerCase();

        final List<AmortizationObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return amortizations; }

        for (AmortizationObject model : models) {
            final String text = model.getContributionDate().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    protected void onStart(){

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
            updateView(datePicked);
        }
    }

    private static void updateView(String date) {
        startDate.setText(date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
