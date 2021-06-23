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
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.Member;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.CreateMember;
import org.etma.main.pojos.MemberCreateResponse;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CreateMemberService extends Service {

      public boolean isRunning = false;

      private NetworkResolver detector;

      private volatile HandlerThread mHandlerThread;
      private ServiceHandler mServiceHandler;

      public CreateMemberService() {}

      @Override
      public void onCreate() {

        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("CreateMemberService.HandlerThread");
        mHandlerThread.start();
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        isRunning = true;
      }

      private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
          super(looper);
        }

        // Define how to handle any incoming messages here
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

              static final long DELAY = 50000;

              @Override
              public void run() {

                  while (isRunning) {

                  Realm realm = Realm.getDefaultInstance();

                  final RealmResults<org.etma.main.db.Member> members = realm.where(org.etma.main.db.Member.class)
                          .equalTo("status", "Draft")
                          .or()
                          .equalTo("status", "UPDATED")
                          .findAll().sort("id", Sort.ASCENDING);

                  int size = members.size();

                  try {
                    if (size > 0) {

                      final Member member = members.get(0);

                      if (member != null){

                          if (detector.isConnected()){

                              String response = createMemberApiCall(member, realm).string();

                              Gson gson = new Gson();

                              MemberCreateResponse coreResponse = gson.fromJson(response, MemberCreateResponse.class);

                              if (coreResponse.isSuccess()){

                                  //update member status to completed
                                  realm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(@NonNull Realm realm) {

                                          if (member.isValid()){
                                              member.setStatus("COMPLETED");
                                              realm.copyToRealmOrUpdate(member);
                                          }

                                      }
                                  });

                              }

                          }else{
                              Toast.makeText(
                                      CreateMemberService.this,
                                      "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                          }
                      }

                      Thread.sleep(DELAY);
                    } else {
                      realm.close();
                      isRunning = false;
                        CreateMemberService.this.stopSelf();
                    }
                  } catch (InterruptedException e) {
                    realm.close();
                    isRunning = false;
                    e.printStackTrace();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
                }
              }
            });

        return START_STICKY;
      }

        private ResponseBody createMemberApiCall(Member draft, Realm realm) throws IOException {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();

            Gson gson = gsonBuilder.create();

            CreateMember member = new CreateMember();

            if (draft.getStatus().equals("Draft")){
                member.setId(null);
            }else{
                member.setId(draft.getMemberId());
            }

            member.setFullName(draft.getFullName());
            member.setCellphone(draft.getCellphone());
            member.setEmailAddress(draft.getEmailAddress());
            member.setCustom1(draft.getCustom1());
            member.setCustom2(draft.getCustom2());
            member.setCustom3(draft.getCustom3());
            member.setMemberRelationshipId(Integer.parseInt(draft.getMemberRelationshipId()));
            member.setDeleted(false);
            member.setUserId(Integer.parseInt(draft.getUserId()));
            member.setDeleterUserId(Integer.parseInt("0"));
            member.setDeletionTime(draft.getDeletionTime());
            member.setLastModificationTime(draft.getLastModificationTime());
            member.setLastModifierUserId(0);
            member.setCreationTime(Util.getCurrentDate());
            member.setCreationTime(draft.getCreationTime());
            member.setCreatorUserId(Integer.parseInt(draft.getCreatorUserId()));

            String json = gson.toJson(member);

            return createMemberCall(json, realm);

        }

        private ResponseBody createMemberCall(String body, Realm realm) throws IOException {

            UserOauth userOauth = realm.where(UserOauth.class).findFirst();

            OkHttpClient client = new OkHttpClient();

            MediaType json = MediaType.parse("application/json; charset=utf-8");

            RequestBody requestBody = RequestBody.create(json, body);

            Request request = new Request.Builder()
                    .url(URLS.CREATE_OR_EDIT_MEMBER)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer "+ userOauth.getAccessToken())
                    .addHeader("Abp.TenantId", "5")
                    .build();

            return client.newCall( request ).execute().body();

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