package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.etma.main.R;
import org.etma.main.adapters.StakeAdapter;
import org.etma.main.db.PaymentMode;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.ui.StakeObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class PaymentModesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private List<StakeObject> stakes;
    private StakeAdapter adapter;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_modes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();

        final String origin = intent.getExtras().getString("origin");
        final long amortizationId = intent.getExtras().getLong("amortizationId");
        final int localPledgeId = intent.getExtras().getInt("localPledgeId");

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, RecyclerViewItemDecorator.VERTICAL_LIST));

        init();

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent;

                if (origin.equals("CONTRIBUTIONS")){
                    intent = new Intent(PaymentModesActivity.this, DefaultPayActivity.class);
                    intent.putExtra("paymentModeId", adapter.getItem(position).getId());
                    intent.putExtra("amortizationId", amortizationId);
                    intent.putExtra("origin", "CONTRIBUTIONS");
                    intent.putExtra("localPledgeId", localPledgeId);
                }else{
                    intent = new Intent(PaymentModesActivity.this, DefaultPayActivity.class);
                    intent.putExtra("paymentModeId", adapter.getItem(position).getId());
                    intent.putExtra("amortizationId", amortizationId);
                    intent.putExtra("origin", "DEFAULT");
                    intent.putExtra("localPledgeId", localPledgeId);
                }

                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void init(){

        stakes = new ArrayList<>();

        RealmResults<PaymentMode> allModes = realm.where(PaymentMode.class).findAll().sort("id", Sort.ASCENDING);

        if (!allModes.isEmpty()){

            for ( PaymentMode mode : allModes){

                stakes.add( new StakeObject(mode.getId(), mode.getName()));
            }
        }

        adapter = new StakeAdapter(stakes);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.equals("")){
            init();
            return true;
        }else{
            final List<StakeObject> filteredModelList = filter(stakes, newText);

            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
            return true;
        }
    }

    private List<StakeObject> filter(List<StakeObject> models, String query) {

        query = query.toLowerCase();

        final List<StakeObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return stakes; }

        for (StakeObject model : models) {
            final String text = model.getName().toLowerCase();
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
