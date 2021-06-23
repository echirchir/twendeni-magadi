package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.roger.catloadinglibrary.CatLoadingView;

import org.etma.main.R;
import org.etma.main.adapters.AmortizationAdapter;
import org.etma.main.asyncs.RequestAmortizationsAsync;
import org.etma.main.db.Amortization;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.Project;
import org.etma.main.events.AmortizationReceivedEvent;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.helpers.Util;
import org.etma.main.ui.AmortizationObject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AmortizationDetailActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

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
    TextView currentPledges;

    @BindView(R.id.my_target)
    TextView myTarget;

    @BindView(R.id.total_contributed)
    TextView totalContributed;

    private String originatingActivity;
    private String localPledgeId;

    private List<AmortizationObject> amortizations;
    private AmortizationAdapter adapter;
    private MemberPledge pledge;

    private NetworkResolver resolver;

    private CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_amortization_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        resolver = new NetworkResolver(this);

        mView = new CatLoadingView();

        Project project = realm.where(Project.class).findFirst();

        if (project != null){

            setupStatsLabels( project );

        }

        Intent intent = getIntent();

        if (intent != null){

            originatingActivity = intent.getExtras().getString("origin");
            localPledgeId = intent.getExtras().getString("memberPledgeId");

            pledge = realm.where(MemberPledge.class).equalTo("pledgeId", localPledgeId).findFirst();


            if (originatingActivity.equals("CONTRIBUTIONS")){

                setTitle("Select pledge plan");
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
                int localPledgeId = Integer.parseInt(pledge.getPledgeId());

                Amortization amortization = realm.where(Amortization.class).equalTo("id", amortizationId).findFirst();

                if (amortization != null){

                    Intent intent = new Intent(AmortizationDetailActivity.this, PaymentModesActivity.class);

                    //proceed to payment;
                    if (originatingActivity.equals("CONTRIBUTIONS")){

                        intent.putExtra("amortizationId", amortization.getId());
                        intent.putExtra("origin", "CONTRIBUTIONS");
                        intent.putExtra("localPledgeId", localPledgeId);
                        startActivity(intent);

                    }else{

                        /*intent.putExtra("amortizationId", amortization.getId());
                        intent.putExtra("origin", "DEFAULT");
                        intent.putExtra("localPledgeId", localPledgeId);*/
                    }
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
            }

        }));

    }

    private void setupStatsLabels(Project project){

        String target = project.getProjectTarget();

        if (target != null){
            targetAmount.setText(Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(project.getProjectTarget())));
        }else{
            targetAmount.setText("0");
        }

        String totalsP = project.getTotalPledges();

        if (project.getTotalPledges() != null){

            if (!totalsP.equals("")){

                double totalPledges = Double.parseDouble(project.getTotalPledges());

                if (totalPledges > 0){

                    currentPledges.setText(Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(project.getTotalPledges())));
                }else{
                    currentPledges.setText("0");
                }
            }else{
                currentPledges.setText("0");
            }
        }else{
            currentPledges.setText("0");
        }


        RealmResults<MemberPledge> pledges = realm.where(MemberPledge.class).findAll().sort("id", Sort.ASCENDING);

        double myPledges = computePledges(pledges);

        myTarget.setText(Util.formatMoney(Util.DECIMAL_FORMAT, myPledges));

        String totalContribs = project.getTotalContributions();

        if (totalContribs != null){

            totalContributed.setText(Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(project.getTotalContributions())));
        }else{
            totalContributed.setText("0");
        }

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
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    private void loadAmortizationsForPledge(){

        amortizations = new ArrayList<>();

        RealmResults<Amortization> pledgeAmortizations;

        if (pledge.getStatus().equals("PROCESSED")){
            pledgeAmortizations = realm.where(Amortization.class)
                    .greaterThan("balance", 0)
                    .equalTo("memberPledgeId", Integer.parseInt(pledge.getPledgeId())).findAll();

        }else{
            pledgeAmortizations = realm.where(Amortization.class)
                    .greaterThan("balance", 0)
                    .equalTo("localPledgeId", localPledgeId).findAll();
        }

        if (!pledgeAmortizations.isEmpty()){

            for (Amortization amortization : pledgeAmortizations){

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

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (mView.isAdded()){
            mView.dismiss();
        }

        //check local db if amortizations exist; if not; request from server then load on UI;
        requestAmortizationsIfNeeded();

    }

    private void showDialog(){
        mView.show(getSupportFragmentManager(), "");
    }

    private void requestAmortizationsIfNeeded(){

        RequestAmortizationsAsync async = new RequestAmortizationsAsync(this);
        async.execute();

        loadAmortizationsForPledge();

    }

    @Subscribe
    public void event(AmortizationReceivedEvent event){

        if (event.isSuccess()){

            if (mView.isAdded()){

                mView.dismiss();
            }

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
                Intent intent = new Intent(this, PledgeHistoryActivity.class);
                intent.putExtra("origin", "MAIN");
                startActivity(intent);
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
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
    }
}
