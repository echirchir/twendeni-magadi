package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.etma.main.R;
import org.etma.main.db.Contribution;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.Member;
import org.etma.main.db.Project;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.user_photo)
    CircleImageView profileImage;

    @BindView(R.id.name)
    TextView fullName;

    @BindView(R.id.members)
    TextView totalMembers;

    @BindView(R.id.pledges)
    TextView totalPledges;

    @BindView(R.id.contributions)
    TextView totalContributions;

    @BindView(R.id.email_address)
    TextView emailAddress;

    @BindView(R.id.cell_phone)
    TextView cellPhone;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        EtmaUser user = realm.where(EtmaUser.class).findFirst();
        RealmResults<Member> members = realm.where(Member.class).findAll().sort("id");

        if (user != null){

            fullName.setText(user.getFull_name());
            emailAddress.setText(user.getEmail_address());
            cellPhone.setText(user.getCell_phone());
            totalMembers.setText("" + members.size());
        }

        RealmResults<Project> projects = realm.where(Project.class).findAll().sort("id", Sort.ASCENDING);

        double pledges;
        String contributions = "0";

        if(!projects.isEmpty()){
            pledges = computeContributions();
            contributions = projects.first().getTotalContributions();
            totalPledges.setText(String.valueOf(pledges));

            totalContributions.setText(contributions);

        }

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

    @OnClick(R.id.fab)
    public void onEditTouch(){

        // handle edit action
        startActivity( new Intent(this, EditProfileActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.change_password:
                startActivity( new Intent(this, ChangePasswordActivity.class));
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
