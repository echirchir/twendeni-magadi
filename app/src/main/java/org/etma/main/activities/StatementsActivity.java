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

import org.etma.main.R;
import org.etma.main.adapters.StatementsAdapter;
import org.etma.main.db.Contribution;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.Project;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.helpers.Util;
import org.etma.main.ui.StatementObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class StatementsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_statements)
    TextView mNoStatements;

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

    private List<StatementObject> statementObjects;

    private StatementsAdapter statementsAdapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        Project project = realm.where(Project.class).findFirst();

        if (project != null){

            setupStatsLabels( project );

        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, RecyclerViewItemDecorator.VERTICAL_LIST));
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
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        queryStatements();
    }

    private void queryStatements() {

        statementObjects = new ArrayList<>();

        statementObjects.add(new StatementObject(1, "PLEDGE", "78454517", "25/10/2016", "58784744", "KES" +" " + "300,580"));
        statementObjects.add(new StatementObject(1, "DONATION", "78454517", "15/10/2016", "58784744", "KES" +" " + "50,580"));
        statementObjects.add(new StatementObject(1, "OFFER", "78454517", "5/10/2016", "58784744", "KES" +" " + "10,580"));
        statementObjects.add(new StatementObject(1, "PLEDGE", "78454517", "25/10/2016", "58784744", "KES" +" " + "300,580"));
        statementObjects.add(new StatementObject(1, "CONTRIB", "78454517", "01/10/2016", "58784744", "KES" +" " + "20,580"));
        statementObjects.add(new StatementObject(1, "OFFER", "78454517", "5/10/2016", "58784744", "KES" +" " + "10,580"));

        statementsAdapter = new StatementsAdapter(statementObjects);
        mRecyclerView.setAdapter(statementsAdapter);
        statementsAdapter.notifyDataSetChanged();

        if (statementsAdapter.getItemCount() != 0){
            mNoStatements.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

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
            queryStatements();
            return true;
        }else{
            final List<StatementObject> filteredModelList = filter(statementObjects, newText);

            statementsAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(statementsAdapter);
            statementsAdapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
            return true;
        }
    }

    private List<StatementObject> filter(List<StatementObject> models, String query) {

        query = query.toLowerCase();

        final List<StatementObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return statementObjects; }

        for (StatementObject model : models) {
            final String text = model.getType().toLowerCase();
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
