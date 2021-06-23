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
import org.etma.main.db.Amortization;
import org.etma.main.db.Contribution;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.Project;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.helpers.Util;
import org.etma.main.ui.AmortizationObject;
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

public class DueContributionsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Realm realm;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_due_contributions)
    TextView noDueContributions;

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

    private List<AmortizationObject> amortizations;
    private AmortizationAdapter adapter;

    private NetworkResolver resolver;

    private CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_contributions);
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, RecyclerViewItemDecorator.VERTICAL_LIST));

        mRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                long amortizationId = adapter.getItemId(position);

                Amortization amortization = realm.where(Amortization.class).equalTo("id", amortizationId).findFirst();

                Intent intent = new Intent(DueContributionsActivity.this, PaymentModesActivity.class);

                intent.putExtra("amortizationId", amortization.getId());
                intent.putExtra("origin", "CONTRIBUTIONS");
                intent.putExtra("localPledgeId", amortization.getMemberPledgeId());
                startActivity(intent);

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

        //filter based on due date;
        RealmResults<Amortization> pledgeAmortizations = realm.where(Amortization.class).findAll();

        if (!pledgeAmortizations.isEmpty()){

            for (Amortization amortization : pledgeAmortizations){

                String contribDate;

                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

                DateTime preliminaryEnd = formatter.parseDateTime(amortization.getContributionDate());
                LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                LocalDate prelimDue = new LocalDate(formattedEnd);

                String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                LocalDate contributionDate = formatter.parseLocalDate(actualDueDate);

                contribDate = contributionDate.toString("MM/dd/yyyy");

                LocalDate today = new LocalDate();

                if (contributionDate.isBefore(today)){
                    amortizations.add( new AmortizationObject(amortization.getId(), amortization.getLocalPledgeId(), contribDate, amortization.getAmount(), amortization.getContributed(), amortization.getBalance(), amortization.getAmortizationId()));
                }

            }
        }

        adapter = new AmortizationAdapter(amortizations, this, realm);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            noDueContributions.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {

        super.onResume();

        loadAmortizationsForPledge();
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
    }
}
