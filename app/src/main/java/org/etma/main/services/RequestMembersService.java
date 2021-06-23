package org.etma.main.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.MemberRelationship;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.AllMembersResponse;
import org.etma.main.pojos.Member;
import org.etma.main.pojos.MembersGetItems;
import org.etma.main.pojos.MembersGetResult;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

public class RequestMembersService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    public RequestMembersService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("RequestMembersService.HandlerThread");
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

        mServiceHandler.post(new Runnable() {

            static final long DELAY = 6000000;

            @Override
            public void run() {
                while (isRunning) {

                    Realm realm = Realm.getDefaultInstance();
                    UserOauth user = realm.where(UserOauth.class).findFirst();

                    if (user != null) {
                        if (detector.isConnected()) {

                            try {
                                String response = doGETMembersApiCall(user).string();

                                Util.prettyPrintJson(response);

                                save(response, realm);

                                Thread.sleep(DELAY);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            realm.close();
                            Toast.makeText(RequestMembersService.this, "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        realm.close();
                        isRunning = false;
                        RequestMembersService.this.stopSelf();
                    }
                }
            }
        });

        return START_STICKY;
    }


    private ResponseBody doGETMembersApiCall(UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_ALL_MEMBERS)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + userOauth.getAccessToken())
                .addHeader("Abp.TenantId", "5")
                .build();

        return client.newCall(request).execute().body();
    }

    private void save( String response, Realm realm ){

        try{

            Gson gson = new Gson();

            AllMembersResponse coreResponse = gson.fromJson(response, AllMembersResponse.class);

            MembersGetResult results = coreResponse.getResult();

            MembersGetItems[] items = results.getItems();

            if (items.length > 0){

                realm.beginTransaction();

                for (MembersGetItems item : items) {

                    Member memberItem = item.getMember();

                    org.etma.main.db.Member member = new org.etma.main.db.Member();

                    org.etma.main.db.Member existing = realm.where(org.etma.main.db.Member.class).equalTo("memberId", memberItem.getId()).findFirst();

                    if (existing == null){

                        RealmResults<org.etma.main.db.Member> members = realm.where(org.etma.main.db.Member.class).findAll();

                        long lastMemberId;

                        if (members.isEmpty()) {
                            member.setId(0);
                        } else {
                            lastMemberId = members.last().getId();
                            member.setId(lastMemberId + 1);
                        }

                        member.setCellphone(memberItem.getCellphone());
                        member.setCustom1(memberItem.getCustom1());
                        member.setCustom2(memberItem.getCustom2());
                        member.setCustom3(memberItem.getCustom3());
                        member.setActive(memberItem.getActive());
                        member.setFullName(memberItem.getFullName());
                        member.setUserId(memberItem.getUserId());
                        member.setEmailAddress(memberItem.getEmailAddress());
                        member.setMemberRelationshipId(memberItem.getMemberRelationshipId());
                        member.setMemberId(memberItem.getId());
                        member.setStatus("COMPLETED");

                        realm.copyToRealm(member);
                    }else{
                        existing.setCellphone(memberItem.getCellphone());
                        existing.setCustom1(memberItem.getCustom1());
                        existing.setCustom2(memberItem.getCustom2());
                        existing.setCustom3(memberItem.getCustom3());
                        existing.setActive(memberItem.getActive());
                        existing.setFullName(memberItem.getFullName());
                        existing.setUserId(memberItem.getUserId());
                        existing.setEmailAddress(memberItem.getEmailAddress());
                        existing.setMemberRelationshipId(memberItem.getMemberRelationshipId());
                        existing.setMemberId(memberItem.getId());
                        existing.setStatus("COMPLETED");

                        realm.copyToRealmOrUpdate(existing);
                    }

                }
                realm.commitTransaction();
            }else{
                initializeFirstMemberAsSelf(realm);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializeFirstMemberAsSelf(Realm realm){

        RealmResults<org.etma.main.db.Member> all = realm.where(org.etma.main.db.Member.class).findAll();
        EtmaUser etmaUser = realm.where(EtmaUser.class).findFirst();
        UserOauth oauth = realm.where(UserOauth.class).findFirst();
        MemberRelationship relationship = realm.where(MemberRelationship.class).equalTo("relationship", "SELF").findFirst();

        if (all.isEmpty()){

            org.etma.main.db.Member member = new org.etma.main.db.Member();

            member.setId(0);

            member.setCellphone(etmaUser.getCell_phone());
            member.setCustom1("");
            member.setCustom2("");
            member.setCustom3("");
            member.setActive("true");
            member.setFullName(etmaUser.getFull_name());
            member.setUserId(""+oauth.getUserId());
            member.setDeleterUserId(""+0);
            member.setCreationTime(Util.getCurrentDate());
            member.setEmailAddress(etmaUser.getEmail_address());
            member.setLastModificationTime(Util.getCurrentDate());
            if (relationship != null){
                member.setMemberRelationshipId(relationship.getRelationshipId());
            }else{
                member.setMemberRelationshipId("1");
            }

            member.setMemberId(""+oauth.getUserId());
            member.setCreatorUserId(""+oauth.getUserId());
            member.setStatus("Draft");

            realm.beginTransaction();

            realm.copyToRealm(member);

            realm.commitTransaction();

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
