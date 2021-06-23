package org.etma.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.etma.main.R;
import org.etma.main.db.ChurchEvent;
import org.etma.main.helpers.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class EventDetailsActivity extends AppCompatActivity {

    @BindView(R.id.event_image)
    ImageView eventImage;

    @BindView(R.id.event_title)
    TextView eventTitle;

    @BindView(R.id.description)
    TextView eventDescription;

    @BindView(R.id.event_date)
    TextView eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        long id = intent.getExtras().getLong("id");

        Realm realm = Realm.getDefaultInstance();

        ChurchEvent event = realm.where(ChurchEvent.class).equalTo("id", id).findFirst();

        if (event != null){

            Picasso.get()
                    .load(event.getImg_url())
                    .placeholder(R.mipmap.logo)
                    .error(R.mipmap.logo)
                    .centerInside()
                    .resize(0, 300)
                    .into(eventImage);

            eventTitle.setText(event.getTitle());
            eventDescription.setText(Util.removeHtmlTags(event.getContent()));
            eventDate.setText(event.getDate());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.chatting_menu, menu);
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
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
    }

}
