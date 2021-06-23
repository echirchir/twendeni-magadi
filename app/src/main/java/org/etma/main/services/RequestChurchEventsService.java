package org.etma.main.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.ChurchEvent;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.pojos.CFMEventsObject;
import org.etma.main.pojos.Post;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RequestChurchEventsService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    public RequestChurchEventsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("RequestChurchEventsService.HandlerThread");
        mHandlerThread.start();
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        isRunning = true;
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    mServiceHandler.post(
        new Runnable() {

          static final long DELAY = 80000;

          @Override
          public void run() {
            while (isRunning) {

              Realm realm = Realm.getDefaultInstance();
              UserOauth user = realm.where(UserOauth.class).findFirst();

              if (user != null) {
                if (detector.isConnected()) {

                  try {
                    String response = doGETChurchEvents(user).string();

                    save(response, realm);

                    Thread.sleep(DELAY);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }

                } else {
                  realm.close();
                  Toast.makeText(
                          RequestChurchEventsService.this,
                          "Turn ON your data bundles to connect!",
                          Toast.LENGTH_SHORT)
                      .show();
                }
              } else {
                realm.close();
                isRunning = false;
                RequestChurchEventsService.this.stopSelf();
              }
            }
          }
        });

        return START_STICKY;
    }

    private ResponseBody doGETChurchEvents(UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_CHURCH_EVENTS)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute().body();
    }

    private void save(String response, Realm realm){

        try{

            Gson gson = new Gson();

            CFMEventsObject coreResponse = gson.fromJson(response, CFMEventsObject.class);

            List<Post> items = coreResponse.getPosts();

            if (items.size() > 0){

                final RealmResults<ChurchEvent> oldEvents = realm.where(ChurchEvent.class).findAll();

                if (!oldEvents.isEmpty()){

                    realm.executeTransaction(realm1 -> oldEvents.deleteAllFromRealm());
                }

                ChurchEvent event;

                realm.beginTransaction();

                for (Post post : items){

                    RealmResults<ChurchEvent> events = realm.where(org.etma.main.db.ChurchEvent.class).findAll();

                    event = new ChurchEvent();

                    long lastEventId;

                    if (events.isEmpty()) {
                        event.setId(0);
                    } else {
                        lastEventId = events.last().getId();
                        event.setId(lastEventId + 1);
                    }

                    event.setPost_id(post.getId());
                    event.setTitle(post.getTitle());
                    event.setContent(post.getContent());
                    event.setDate(post.getDate());
                    event.setAuthor(post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName());
                    event.setImg_url(post.getAttachments().get(0).getImages().getFull().getUrl());
                    event.setPost_url(post.getUrl());

                    realm.copyToRealm(event);
                }

                realm.commitTransaction();

            }


        }catch (Exception e){
            e.printStackTrace();
        }finally{
            realm.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!isRunning) {
            mHandlerThread.quit();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
