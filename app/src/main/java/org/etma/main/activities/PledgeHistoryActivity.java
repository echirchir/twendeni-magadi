package org.etma.main.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.etma.main.R;
import org.etma.main.adapters.PledgeHistoryAdapter;
import org.etma.main.db.Contribution;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.PaymentPeriod;
import org.etma.main.db.Project;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.helpers.Util;
import org.etma.main.services.RequestAmortizationsService;
import org.etma.main.ui.MemberPledgeObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class PledgeHistoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.target_amount)
    TextView targetAmount;

    @BindView(R.id.my_contributions)
    TextView my_contributions;

    @BindView(R.id.my_target)
    TextView myTarget;

    @BindView(R.id.total_contributed)
    TextView all_contributions;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    @BindView(R.id.no_history)
    TextView mNoHistory;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private PledgeHistoryAdapter adapter;
    private List<MemberPledgeObject> pledges;

    private Realm realm;
    private String originatingActivity;

    private LinearLayout selectedView;
    private int selectedItemPosition;
    private ActionMode currentActionMode;

    private NetworkResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pledge_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PledgeHistoryActivity.this, AddPledgeActivity.class);
                intent.putExtra("action", "CREATE");
                startActivity(intent);
            }
        });

        realm = Realm.getDefaultInstance();

        resolver = new NetworkResolver(this);

        final Project project = realm.where(Project.class).findFirst();

        if (project != null){

            setupStatsLabels( project );

        }

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, RecyclerViewItemDecorator.VERTICAL_LIST));

        init();

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                MemberPledge pledge =  realm.where(MemberPledge.class).equalTo("id", adapter.getItem(position).getId()).findFirst();

                if(originatingActivity.equals("CONTRIBUTIONS")){

                    Intent intent = new Intent(PledgeHistoryActivity.this, AmortizationDetailActivity.class);
                    intent.putExtra("memberPledgeId", pledge.getPledgeId());
                    intent.putExtra("origin", "CONTRIBUTIONS");
                    startActivity(intent);
                }else{
                    PaymentPeriod period = realm.where(PaymentPeriod.class).equalTo("paymentPeriodId", pledge.getPaymentPeriodId()).findFirst();

                    if ( period.getPeriod().equals("Flexi Plan") ){

                        Intent intent = new Intent(PledgeHistoryActivity.this, FlexPledgeDetailActivity.class);
                        intent.putExtra("id", adapter.getItem(position).getId());
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(PledgeHistoryActivity.this, PledgeDetailsActivity.class);
                        intent.putExtra("id", adapter.getItem(position).getId());
                        startActivity(intent);
                    }

                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

                selectedItemPosition = position;

                long memberId = adapter.getItem(selectedItemPosition).getId();

                MemberPledge member = realm.where(MemberPledge.class).equalTo("id", memberId).findFirst();

                if (member.getStatus().equals("PROCESSED") || member.getStatus().equals("PROCESSING")){
                    Util.showSnackBar(PledgeHistoryActivity.this, coordinatorLayout, "You cannot modify a completed pledge");
                }else{
                    if (currentActionMode != null) {
                        return;
                    }

                    selectedView = (LinearLayout) view;

                    selectedView.setBackgroundColor(ContextCompat.getColor(PledgeHistoryActivity.this, R.color.blue));
                    currentActionMode = startSupportActionMode(modeCallBack);
                    view.setSelected(true);
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
                selectedView.setBackgroundColor(ContextCompat.getColor(PledgeHistoryActivity.this, R.color.white));
            }

        }
    };

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

    private void handleDelete(int selectedItemPosition) {

        long memberId = adapter.getItem(selectedItemPosition).getId();

        RealmResults<MemberPledge> members = realm.where(MemberPledge.class).equalTo("id", memberId).findAll();

        confirmDeleteAction(members, selectedItemPosition);

    }

    private void confirmDeleteAction(final RealmResults<MemberPledge> member, final int position){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete_member_pledge))
                .setMessage(getString(R.string.delete_member_pledge_desc))
                .setCancelable(false)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        realm.beginTransaction();
                        member.deleteAllFromRealm();

                        realm.commitTransaction();

                        adapter.remove(position);
                        adapter.notifyItemRemoved(position);

                        if (adapter.getItemCount() == 0){
                            finish();
                            startActivity(getIntent());
                        }

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void handleEdit(int selectedItemPosition) {
        Intent intent = new Intent(this, AddPledgeActivity.class);
        intent.putExtra("itemId", adapter.getItem(selectedItemPosition).getId());
        intent.putExtra("action", "EDIT");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if (intent != null){

            originatingActivity = intent.getExtras().getString("origin");

            if (originatingActivity.equals("CONTRIBUTIONS")){
                setTitle("Select Pledge");
                fab.setVisibility(View.GONE);

            }

        }

        if (resolver.isConnected()){



            Intent requestAmortizations = new Intent(this, RequestAmortizationsService.class);
            startService(requestAmortizations);

        }
    }

    private void init(){

        pledges = new ArrayList<>();

        RealmResults<MemberPledge> memberPledges = realm.where(MemberPledge.class).findAll().sort("id", Sort.DESCENDING);

        if (!memberPledges.isEmpty()){

            for (MemberPledge pledge : memberPledges){

                String combined = "Pledged on ".concat(pledge.getDatePledged());

                pledges.add( new MemberPledgeObject(pledge.getId(), pledge.getName(), Double.parseDouble(pledge.getAmount()), combined, pledge.getLocalPledgeId(), Double.parseDouble(pledge.getBalance())) );
            }
        }

        adapter = new PledgeHistoryAdapter(pledges);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            mNoHistory.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.equals("")){
            init();
            return true;
        }else{
            final List<MemberPledgeObject> filteredModelList = filter(pledges, newText);

            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
            return true;
        }
    }

    private List<MemberPledgeObject> filter(List<MemberPledgeObject> models, String query) {

        query = query.toLowerCase();

        final List<MemberPledgeObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return pledges; }

        for (MemberPledgeObject model : models) {
            final String text = model.getMemberName().toLowerCase();
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
