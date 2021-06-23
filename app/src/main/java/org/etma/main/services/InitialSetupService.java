package org.etma.main.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.etma.main.URLS;
import org.etma.main.db.MemberRelationship;
import org.etma.main.db.PaymentMode;
import org.etma.main.db.PaymentPeriod;
import org.etma.main.db.PledgeStake;
import org.etma.main.db.Project;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.pojos.MemberRelationshipItems;
import org.etma.main.pojos.MembersRelationshipsResponse;
import org.etma.main.pojos.PaymentModeItems;
import org.etma.main.pojos.PaymentModesResponse;
import org.etma.main.pojos.PaymentModesResult;
import org.etma.main.pojos.PaymentPeriodItems;
import org.etma.main.pojos.PaymentPeriodsResponse;
import org.etma.main.pojos.PaymentPeriodsResult;
import org.etma.main.pojos.PledgeStakeItems;
import org.etma.main.pojos.PledgeStakesResponse;
import org.etma.main.pojos.PledgeStakesResult;
import org.etma.main.pojos.ProjectResultItems;
import org.etma.main.pojos.ProjectsResponse;
import org.etma.main.pojos.ProjectsResults;
import org.etma.main.pojos.RelationshipResult;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

public class InitialSetupService extends Service {

      public boolean isRunning = false;

      private NetworkResolver detector;

      private volatile HandlerThread mHandlerThread;
      private ServiceHandler mServiceHandler;

      public InitialSetupService() {}

      @Override
      public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("InitialSetupService.HandlerThread");
        mHandlerThread.start();
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        isRunning = true;
      }

      private final class ServiceHandler extends Handler {

            public ServiceHandler(Looper looper) {
              super(looper);
            }

            @Override
            public void handleMessage(Message message) {}
      }

      @Override
      public void onDestroy() {
        super.onDestroy();

        if (!isRunning) {
          mHandlerThread.quit();
        }
      }

      @Override
      public int onStartCommand(Intent intent, int flags, int startId) {

        mServiceHandler.post(
            new Runnable() {

              static final long DELAY = 1500000;

              @Override
              public void run() {

                while (isRunning) {

                  Realm realm = Realm.getDefaultInstance();

                  UserOauth user = realm.where(UserOauth.class).findFirst();

                  if (user != null) {

                      String bearer = "Bearer " + user.getAccessToken();

                      if (detector.isConnected()) {
                          try {

                              requestMemberRelationships(realm, bearer);

                              requestPaymentModes(realm, bearer);

                              requestPaymentPeriods(realm, bearer);

                              requestPledgeStakes(realm, bearer);

                              Thread.sleep(DELAY);
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                      } else {
                          isRunning = false;
                          realm.close();
                          Toast.makeText(InitialSetupService.this, "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                      }
                    }else{

                      isRunning = false;
                      realm.close();
                      InitialSetupService.this.stopSelf();

                  }
                  }
                }
            });

        return START_STICKY;
      }


      private void requestMemberRelationships( Realm realm , String token) throws IOException{

          OkHttpClient client = new OkHttpClient();

          Request request = new Request.Builder()
                  .url(URLS.GET_MEMBER_RELATIONSHIPS)
                  .get()
                  .addHeader("Content-Type", "application/json")
                  .addHeader("Authorization", token)
                  .addHeader("Abp.TenantId", "5")
                  .build();

          String response = client.newCall(request).execute().body().string();

          saveMemberRelationships( realm, response );

      }

      private void saveMemberRelationships( Realm realm, String response ){

          try{

              Gson gson = new Gson();

              MembersRelationshipsResponse coreResponse = gson.fromJson(response, MembersRelationshipsResponse.class);

              RelationshipResult results = coreResponse.getResult();

              MemberRelationshipItems[] items = results.getItems();

              realm.beginTransaction();

              org.etma.main.db.MemberRelationship memberRelationship;

              for (MemberRelationshipItems item : items) {

                  org.etma.main.pojos.MemberRelationship relationship = item.getMemberRelationship();

                  MemberRelationship existing = realm.where(MemberRelationship.class).equalTo("relationshipId", relationship.getId()).findFirst();

                  if (existing == null){

                      memberRelationship = new org.etma.main.db.MemberRelationship();

                      RealmResults<org.etma.main.db.MemberRelationship> existingRelationships = realm.where(org.etma.main.db.MemberRelationship.class).findAll();

                      long lastRelationshipId;

                      if (existingRelationships.isEmpty()) {
                          memberRelationship.setId(0);
                      } else {
                          lastRelationshipId = existingRelationships.last().getId();
                          memberRelationship.setId(lastRelationshipId + 1);
                      }

                      memberRelationship.setRelationship(relationship.getName());
                      memberRelationship.setDescription(relationship.getDescription());
                      memberRelationship.setRelationshipId(relationship.getId());
                      memberRelationship.setAllowRegistration(relationship.getAllowRegistration());

                      realm.copyToRealm(memberRelationship);
                  }else{

                      existing.setRelationship(relationship.getName());
                      existing.setDescription(relationship.getDescription());
                      existing.setRelationshipId(relationship.getId());
                      existing.setAllowRegistration(relationship.getAllowRegistration());
                      realm.copyToRealmOrUpdate(existing);
                  }

              }

              realm.commitTransaction();

          }catch (Exception e){
              e.printStackTrace();
          }

      }


      private void requestPaymentPeriods( Realm realm, String token) throws IOException{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_PAYMENT_PERIODS)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .addHeader("Abp.TenantId", "5")
                .build();

        String response = client.newCall(request).execute().body().string();

        savePaymentPeriods(realm, response);

      }

      private void savePaymentPeriods( Realm realm, String response ){

        try{

            Gson gson = new Gson();

            PaymentPeriodsResponse coreResponse = gson.fromJson(response, PaymentPeriodsResponse.class);

            PaymentPeriodsResult results = coreResponse.getResult();

            PaymentPeriodItems[] items = results.getItems();


            realm.beginTransaction();

            org.etma.main.db.PaymentPeriod period;

            for (PaymentPeriodItems item : items) {

                org.etma.main.pojos.PaymentPeriod paymentPeriod = item.getPaymentPeriod();

                PaymentPeriod existing = realm.where(PaymentPeriod.class).equalTo("paymentPeriodId", paymentPeriod.getId()).findFirst();

                if (existing == null){

                    period = new org.etma.main.db.PaymentPeriod();

                    RealmResults<org.etma.main.db.PaymentPeriod> paymentPeriods = realm.where(org.etma.main.db.PaymentPeriod.class).findAll();

                    long lastPeriodId;

                    if (paymentPeriods.isEmpty()) {
                        period.setId(0);
                    } else {
                        lastPeriodId = paymentPeriods.last().getId();
                        period.setId(lastPeriodId + 1);
                    }

                    period.setPeriod(paymentPeriod.getName());
                    period.setPaymentPeriodId(paymentPeriod.getId());
                    period.setDescription(paymentPeriod.getDescription());
                    period.setMobileApp(paymentPeriod.getMobileApp());
                    period.setWebPortal(paymentPeriod.getWebPortal());

                    realm.copyToRealm(period);
                }else{
                    existing.setPeriod(paymentPeriod.getName());
                    existing.setPaymentPeriodId(paymentPeriod.getId());
                    existing.setDescription(paymentPeriod.getDescription());
                    existing.setMobileApp(paymentPeriod.getMobileApp());
                    existing.setWebPortal(paymentPeriod.getWebPortal());
                    realm.copyToRealmOrUpdate(existing);
                }

            }

            realm.commitTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestPaymentModes( Realm realm , String token) throws IOException{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_PAYMENT_MODES)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .addHeader("Abp.TenantId", "5")
                .build();

        String response =  client.newCall(request).execute().body().string();

        savePaymentModes(realm, response);
    }

    private void savePaymentModes( Realm realm, String response ){

        try{

            Gson gson = new Gson();

            PaymentModesResponse coreResponse = gson.fromJson(response, PaymentModesResponse.class);

            PaymentModesResult results = coreResponse.getResult();

            PaymentModeItems[] items = results.getItems();


            realm.beginTransaction();

            org.etma.main.db.PaymentMode mode;

            for (PaymentModeItems item : items) {

                org.etma.main.pojos.PaymentMode paymentMode = item.getPaymentMode();

                PaymentMode existing = realm.where(PaymentMode.class).equalTo("paymentModeId", paymentMode.getId()).findFirst();

                if (existing == null){

                    mode = new org.etma.main.db.PaymentMode();

                    RealmResults<org.etma.main.db.PaymentMode> paymentModes = realm.where(org.etma.main.db.PaymentMode.class).findAll();

                    long lastModeId;

                    if (paymentModes.isEmpty()) {
                        mode.setId(0);
                    } else {
                        lastModeId = paymentModes.last().getId();
                        mode.setId(lastModeId + 1);
                    }

                    mode.setPaymentModeId(paymentMode.getId());
                    mode.setName(paymentMode.getName());
                    mode.setDescription(paymentMode.getDescription());
                    mode.setActive(paymentMode.getActive());

                    realm.copyToRealm(mode);
                }else{
                    existing.setPaymentModeId(paymentMode.getId());
                    existing.setName(paymentMode.getName());
                    existing.setDescription(paymentMode.getDescription());
                    existing.setActive(paymentMode.getActive());

                    realm.copyToRealmOrUpdate(existing);
                }


            }

            realm.commitTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestPledgeStakes(Realm realm, String token) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_PLEDGE_STAKES)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .addHeader("Abp.TenantId", "5")
                .build();

        String response = client.newCall(request).execute().body().string();

        savePledgeStakes(realm, response);
    }

    private void savePledgeStakes( Realm realm , String response ){

        try{

            Gson gson = new Gson();

            PledgeStakesResponse coreResponse = gson.fromJson(response, PledgeStakesResponse.class);

            PledgeStakesResult results = coreResponse.getResult();

            PledgeStakeItems[] items = results.getItems();


            realm.beginTransaction();

            org.etma.main.db.PledgeStake stake;

            for (PledgeStakeItems item : items) {

                org.etma.main.pojos.PledgeStake pledgeStake = item.getPledgeStake();

                PledgeStake existing = realm.where(PledgeStake.class).equalTo("pledgeStakeId", pledgeStake.getId()).findFirst();

                if (existing == null){

                    stake = new org.etma.main.db.PledgeStake();

                    RealmResults<org.etma.main.db.PledgeStake> pledgeStakes = realm.where(org.etma.main.db.PledgeStake.class).findAll();

                    long lastPledgeId;

                    if (pledgeStakes.isEmpty()) {
                        stake.setId(0);
                    } else {
                        lastPledgeId = pledgeStakes.last().getId();
                        stake.setId(lastPledgeId + 1);
                    }

                    stake.setPledgeStakeId(pledgeStake.getId());
                    stake.setDescription(pledgeStake.getDescription());
                    stake.setStake(pledgeStake.getName());
                    stake.setMaximum(pledgeStake.getMaximum());
                    stake.setMinimum(pledgeStake.getMinimum());
                    stake.setMobileApp(pledgeStake.getMobileApp());
                    stake.setWebApp(pledgeStake.getWebApp());

                    realm.copyToRealm(stake);
                }else{
                    existing.setPledgeStakeId(pledgeStake.getId());
                    existing.setDescription(pledgeStake.getDescription());
                    existing.setStake(pledgeStake.getName());
                    existing.setMaximum(pledgeStake.getMaximum());
                    existing.setMinimum(pledgeStake.getMinimum());
                    existing.setMobileApp(pledgeStake.getMobileApp());
                    existing.setWebApp(pledgeStake.getWebApp());
                    realm.copyToRealmOrUpdate(existing);
                }


            }

            realm.commitTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}