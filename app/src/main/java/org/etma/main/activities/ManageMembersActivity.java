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
import org.etma.main.adapters.MembersAdapter;
import org.etma.main.db.Contribution;
import org.etma.main.db.Member;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.MemberRelationship;
import org.etma.main.db.Project;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.RecyclerViewItemDecorator;
import org.etma.main.helpers.Util;
import org.etma.main.services.CreateMemberService;
import org.etma.main.ui.MemberObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ManageMembersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_members)
    TextView mNoMembers;

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

    private List<MemberObject> members;

    private MembersAdapter adapter;

    private Realm realm;

    private LinearLayout selectedView;
    private int selectedItemPosition;
    private ActionMode currentActionMode;
    private String originatingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_members);
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

        Intent createMemberService = new Intent(this, CreateMemberService.class);
        startService(createMemberService);

        Intent intent = getIntent();

        originatingActivity = intent.getExtras().getString("origin");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(ManageMembersActivity.this, MemberRelationshipsActivity.class));
            }
        });

        loadMembers();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new RecyclerViewItemDecorator(this, RecyclerViewItemDecorator.VERTICAL_LIST));

        mRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if(originatingActivity.equals("ADDPLEDGE")){

                    Intent intent = new Intent();
                    intent.putExtra("id", adapter.getItem(position).getId());
                    setResult(RESULT_OK, intent);
                    finish();

                }else{

                    // view member details page;
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

                selectedItemPosition = position;

                if (currentActionMode != null) {
                    return;
                }

                selectedView = (LinearLayout) view;

                selectedView.setBackgroundColor(ContextCompat.getColor(ManageMembersActivity.this, R.color.blue));
                currentActionMode = startSupportActionMode(modeCallBack);
                view.setSelected(true);
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
                selectedView.setBackgroundColor(ContextCompat.getColor(ManageMembersActivity.this, R.color.white));
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

    private double computeMemberPledges(RealmResults<MemberPledge> pledges){

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

        RealmResults<Member> members = realm.where(Member.class).equalTo("id", memberId).findAll();

        confirmDeleteAction(members, selectedItemPosition);

    }

    private void confirmDeleteAction(final RealmResults<Member> member, final int position){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete_member))
                .setMessage(getString(R.string.delete_member_desc))
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

        Intent intent = new Intent(this, AddMemberActivity.class);

        Member member = realm.where(Member.class).equalTo("id", adapter.getItem(selectedItemPosition).getId()).findFirst();
        MemberRelationship relationship = realm.where(MemberRelationship.class).equalTo("relationshipId", ""+member.getMemberRelationshipId()).findFirst();

        intent.putExtra("id", relationship.getId());

        intent.putExtra("memberId", adapter.getItem(selectedItemPosition).getId());
        intent.putExtra("action", "EDIT");
        startActivity(intent);
    }

    private void loadMembers(){

        members = new ArrayList<>();

        RealmResults<Member> groupmembers = realm.where(Member.class).findAll().sort("id", Sort.DESCENDING);

        if (!groupmembers.isEmpty()){

            for (Member member : groupmembers) {
                RealmResults<MemberPledge> pledges = realm.where(MemberPledge.class).equalTo("memberId", member.getMemberId()).findAll().sort("id");

                double sum = computeMemberPledges(pledges);

                String formattedAmount = "KES " + Util.formatMoney(Util.DECIMAL_FORMAT, sum);
                members.add(new MemberObject(member.getId(), member.getFullName(), member.getEmailAddress(), formattedAmount));
            }
        }

        adapter = new MembersAdapter(members, this);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0){
            mNoMembers.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private double computePledges(RealmResults<MemberPledge> pledges){


        double sum = 0.0;

        if (!pledges.isEmpty()){

            for (MemberPledge pledge : pledges){

                sum += Double.parseDouble(pledge.getAmount());
            }

        }

        return sum;
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
                startActivity(new Intent(this, EndTimeMessageMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.equals("")){
            loadMembers();
            return true;
        }else{
            final List<MemberObject> filteredModelList = filter(members, newText);

            adapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
            return true;
        }
    }

    private List<MemberObject> filter(List<MemberObject> models, String query) {

        query = query.toLowerCase();

        final List<MemberObject> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return members; }

        for (MemberObject model : models) {
            final String text = model.getFullName().toLowerCase();
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
