package org.etma.main.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.etma.main.R;
import org.etma.main.adapters.DashboardAdapter;
import org.etma.main.adapters.DashboardItem;
import org.etma.main.db.Amortization;
import org.etma.main.db.Contribution;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.Project;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.RecyclerItemClickListener;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.ActivateEmail;
import org.etma.main.services.CreateContributionService;
import org.etma.main.services.CreateMemberService;
import org.etma.main.services.ProjectInformationService;
import org.etma.main.services.RequestAllPledgesService;
import org.etma.main.services.RequestAmortizationsService;
import org.etma.main.services.RequestChurchEventsService;
import org.etma.main.services.RequestContributionsService;
import org.etma.main.services.RequestMembersService;
import org.etma.main.services.RequestMpesaPaymentsService;
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

public class EndTimeMessageMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{

    private DrawerLayout drawer;

    private SliderLayout mDemoSlider;

    private RecyclerView recyclerView;

    private List<DashboardItem> items;
    private DashboardAdapter adapter;

    @BindView(R.id.target_amount)
    TextView targetAmount;

    @BindView(R.id.my_contributions)
    TextView  my_contributions;

    @BindView(R.id.my_target)
    TextView myTarget;

    @BindView(R.id.total_contributed)
    TextView all_contributions;

    private Realm realm;
    private NetworkResolver resolver;

    private UserOauth user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end_time_message_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        user = realm.where(UserOauth.class).findFirst();

        Project project = realm.where(Project.class).findFirst();

        if (project != null){

            setupStatsLabels( project );

        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_dashboard);
        navigationView.getMenu().performIdentifierAction(R.id.nav_dashboard, 0);

        TextView fullName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.name);
        TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);

        EtmaUser user = realm.where(EtmaUser.class).findFirst();

        if (user != null) {
            fullName.setText(user.getFull_name());
            email.setText(user.getEmail_address());
        }

        mDemoSlider = findViewById(R.id.slider);

        resolver = new NetworkResolver(this);

        if(resolver.isConnected()){

            //createMemberIfNeeded();

            Intent fetchProjectsService = new Intent(this, ProjectInformationService.class);
            startService(fetchProjectsService);

            Intent requestMembersService = new Intent(this, RequestMembersService.class);
            startService(requestMembersService);

            Intent createMemberService = new Intent(this, CreateMemberService.class);
            startService(createMemberService);

            //CREATE PLEDGE AND UPDATE PLEDGE ID
            //Intent createPledges = new Intent(this, CreateMemberPledgeService.class);
            //startService(createPledges);

            //CHECK IF ALL PLEDGES ARE CREATED, IF SO, SYNC AMORTIZATIONS
            //Intent amortizationService = new Intent(this, CreateAmortizationService.class);
            //startService(amortizationService);

            //ENSURE ALL PLEDGES & AMORTIZATIONS HAVE SYNCED, THEN REQUEST THEM WITH IDS
            Intent requestAmortizationsService = new Intent(this, RequestAmortizationsService.class);
            startService(requestAmortizationsService);

            //REQUEST CHURCH EVENTS FROM SERVER
            Intent requestEvents = new Intent(this, RequestChurchEventsService.class);
            startService(requestEvents);

            //UPDATE MPESA PAYMENTS FROM SERVER
            Intent mpesaPaymentsService = new Intent(this, RequestMpesaPaymentsService.class);
            startService(mpesaPaymentsService);

            //IN PARALLEL, SYNC CONTRIBUTIONS
            Intent contributionService = new Intent(this, CreateContributionService.class);
            startService(contributionService);

            Intent getAllPledgesService = new Intent(this, RequestAllPledgesService.class);
            startService(getAllPledgesService);

            Intent getAllContributionsService = new Intent(this, RequestContributionsService.class);
            startService(getAllContributionsService);

            //do more here to get data from portal

            //POST Request Password CODE
            String emailAddress = "elisha.java@gmail.com";
            //new SendPasswordResetCodeAsync(this).execute(emailAddress);

            //POST send email action link
            String sendActivationLink = "dicky.tech@gmail.com";
            //new SendEmailActionLinkAsync(this).execute(sendActivationLink);

            //POST activate email
            String activateEmail = prepareEmailActivaton();
            //new ActivateEmailAsync(this).execute(activateEmail);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        listUrl.add("http://saleslifecrm.com:8282/CF/CHURCH%201.jpg");
        listName.add("IMAGE - 1");

        listUrl.add("http://saleslifecrm.com:8282/CF/CHURCH%202.jpg");
        listName.add("IMAGE - 2");

        listUrl.add("http://saleslifecrm.com:8282/CF/CHURCH%203.jpg");
        listName.add("IMAGE - 3");

        listUrl.add("http://saleslifecrm.com:8282/CF/CHURCH%204.jpg");
        listName.add("IMAGE - 4");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    .setRequestOption(requestOptions)
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        loadItems();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(EndTimeMessageMainActivity.this, ManageMembersActivity.class);
                        intent.putExtra("origin", "MAIN");
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(EndTimeMessageMainActivity.this, PledgeHistoryActivity.class);
                        intent.putExtra("origin", "MAIN");
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(EndTimeMessageMainActivity.this, MakeContributionActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        startActivity(new Intent(EndTimeMessageMainActivity.this, DueContributionsActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(EndTimeMessageMainActivity.this, MpesaPaymentActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(EndTimeMessageMainActivity.this, ExtraMileCampaignsActivity.class));
                        break;
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

    private String prepareEmailActivaton(){

        ActivateEmail activateEmail = new ActivateEmail();
        activateEmail.setConfirmationCode("70878");
        activateEmail.setC("C");
        activateEmail.setUserId("2");

        Gson gson = new GsonBuilder().serializeNulls().create();

        return gson.toJson(activateEmail);
    }

    private void loadItems(){

        items = new ArrayList<>();

        RealmResults<org.etma.main.db.Member> members = realm.where(org.etma.main.db.Member.class).findAll().sort("id");
        RealmResults<MemberPledge> pledges = realm.where(MemberPledge.class).findAll().sort("id", Sort.ASCENDING);
        RealmResults<Amortization> amortizations = realm.where(Amortization.class).findAll().sort("id", Sort.ASCENDING);
        RealmResults<Contribution> contributions = realm.where(Contribution.class).findAll().sort("id", Sort.ASCENDING);
        RealmResults<Project> projects = realm.where(Project.class).findAll().sort("id", Sort.ASCENDING);
        RealmResults<MpesaPayment> payments = realm.where(MpesaPayment.class).findAll().sort("id", Sort.ASCENDING);
        RealmResults<MpesaPayment> extra = realm.where(MpesaPayment.class).equalTo("type", "EXTRA").findAll().sort("id", Sort.ASCENDING);

        String target = "0";

        if (!projects.isEmpty()){

            target = projects.first().getProjectTarget();
        }

        double totalPledges = computePledges(pledges);
        int dueContributions = computeDueContributions(amortizations);
        double totalContributions = computeContributions(contributions);

        items.add(new DashboardItem(1, 1, 1, "MY MEMBERS", "" + members.size()));
        items.add(new DashboardItem(1, 1, 1, "PLEDGE HISTORY", Util.formatMoney(Util.DECIMAL_FORMAT, totalPledges)));
        items.add(new DashboardItem(1, 1, 1, "MAKE CONTRIBUTIONS", Util.formatMoney(Util.DECIMAL_FORMAT, totalContributions)));
        items.add(new DashboardItem(1, 1, 1, "DUE CONTRIBUTIONS", "" + dueContributions));
        items.add(new DashboardItem(1, 1, 1, "MY PAYMENTS", "" + payments.size()));
        items.add(new DashboardItem(1, 1, 1, "EXTRA CAMPAIGNS", "" + payments.size()));

        adapter = new DashboardAdapter(items, this);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    private double computeContributions(RealmResults<Contribution> contributions){

        double totals = 0;

        if (!contributions.isEmpty()){

            for (Contribution contribution : contributions){

                totals += Double.parseDouble(contribution.getAmount());
            }
        }

        return totals;
    }

    private int computeDueContributions(RealmResults<Amortization> amortizations){

        List<Amortization> dueList = new ArrayList<>();

        if (!amortizations.isEmpty()){

            for (Amortization amortization : amortizations){

                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                LocalDate start = formatter.parseLocalDate(amortization.getContributionDate());

                LocalDate today = new LocalDate();

                boolean isBeforeToday = start.isBefore(today);

                if (isBeforeToday){
                    dueList.add(amortization);
                }
            }

        }

        return dueList.size();
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }*/
        startActivity(new Intent(this, CreateAccountActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_right_menu) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_members) {
            Intent intent = new Intent(EndTimeMessageMainActivity.this, ManageMembersActivity.class);
            intent.putExtra("origin", "MAIN");
            startActivity(intent);
        } else if (id == R.id.nav_pledge) {
            Intent intent = new Intent(this, PledgeHistoryActivity.class);
            intent.putExtra("origin", "MAIN");
            startActivity(intent);
        } else if (id == R.id.nav_contribute) {
            Intent intent = new Intent(EndTimeMessageMainActivity.this, MakeContributionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_events){
            startActivity(new Intent(EndTimeMessageMainActivity.this, ChurchEventsActivity.class));
        }else if (id == R.id.nav_share) {
            //startActivity(new Intent(this, StatementsActivity.class));
        }else if (id == R.id.nav_invite){
            invite();
        }else if (id == R.id.nav_logout){
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){

        startActivity( new Intent(this, CreateAccountActivity.class));
    }

    private void invite() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .build();
        startActivityForResult(intent, 100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

}
