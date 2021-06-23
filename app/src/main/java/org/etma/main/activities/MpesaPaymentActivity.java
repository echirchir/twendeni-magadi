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
import org.etma.main.adapters.MpesaPaymentsAdapter;
import org.etma.main.db.Contribution;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.PaymentMode;
import org.etma.main.db.Project;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.helpers.Util;
import org.etma.main.ui.MpesaPaymentObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MpesaPaymentActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_payments)
    TextView noPayments;

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

    private List<MpesaPaymentObject> payments;

    private MpesaPaymentsAdapter adapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mpesa_payment);
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

        mRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        loadPayments();
    }

    private void loadPayments(){

        payments = new ArrayList<>();

        RealmResults<MpesaPayment> mpesaPayments = realm.where(MpesaPayment.class).findAll().sort("id", Sort.DESCENDING);

        if (!mpesaPayments.isEmpty()){

            for (MpesaPayment payment : mpesaPayments) {

                PaymentMode mode = realm.where(PaymentMode.class).equalTo("paymentModeId", "" + payment.getPaymentModeId()).findFirst();

                String formattedAmount = "KES " + Util.formatMoney(Util.DECIMAL_FORMAT, (Integer.parseInt(payment.getAmountToPay()) * 1.0));

                // TODO: 8/25/18 FIX PAYMENT MODE ISSUE; 

                payments.add(new MpesaPaymentObject(payment.getId(), payment.getRequestDate(), formattedAmount, payment.getStatus(), "MPESA", payment.getResultCode()));
            }
        }

        adapter = new MpesaPaymentsAdapter(payments, this);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            noPayments.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.equals("")){
            loadPayments();
            return true;
        }else{
            final List<MpesaPaymentObject> filteredModelList = filter(payments, newText);

            adapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
            return true;
        }
    }

    private List<MpesaPaymentObject> filter(List<MpesaPaymentObject> models, String query) {

        query = query.toLowerCase();

        final List<MpesaPaymentObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return payments; }

        for (MpesaPaymentObject model : models) {
            final String text = model.getPayMode().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
