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
import org.etma.main.adapters.MemberRelationshipAdapter;
import org.etma.main.db.MemberRelationship;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.ui.MemberRelationshipObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MemberRelationshipsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private List<MemberRelationshipObject> relationships;
    private MemberRelationshipAdapter adapter;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_relationships);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, RecyclerViewItemDecorator.VERTICAL_LIST));

        init();

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                /* go to next activity to add member*/
                Intent intent = new Intent(MemberRelationshipsActivity.this, AddMemberActivity.class);
                intent.putExtra("id", adapter.getItem(position).getId());
                intent.putExtra("action", "CREATE");
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void init(){

        relationships = new ArrayList<>();

        RealmResults<MemberRelationship> data = realm.where(MemberRelationship.class).findAll().sort("id", Sort.ASCENDING);

        if (!data.isEmpty()){

            for (MemberRelationship relationship : data){

                relationships.add( new MemberRelationshipObject(relationship.getId(), relationship.getRelationship()) );
            }
        }

        adapter = new MemberRelationshipAdapter(relationships);
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
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
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

    private List<MemberRelationshipObject> filter(List<MemberRelationshipObject> models, String query) {

        query = query.toLowerCase();

        final List<MemberRelationshipObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return relationships; }

        for (MemberRelationshipObject model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
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
            final List<MemberRelationshipObject> filteredModelList = filter(relationships, newText);

            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
            return true;
        }
    }
}
