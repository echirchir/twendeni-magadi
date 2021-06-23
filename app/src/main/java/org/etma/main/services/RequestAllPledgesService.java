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
import org.etma.main.db.MemberPledge;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.pojos.MemberPledgeItem;
import org.etma.main.pojos.MemberPledgeResponse;
import org.etma.main.pojos.MemberPledgeResult;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RequestAllPledgesService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;


    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("RequestAllPledges.HandlerThread");
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

    public RequestAllPledgesService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mServiceHandler.post(new Runnable() {

            static final long DELAY = 10000000;

            @Override
            public void run() {
                while (isRunning) {

                    Realm realm = Realm.getDefaultInstance();
                    UserOauth user = realm.where(UserOauth.class).findFirst();

                    if (user != null) {
                        if (detector.isConnected()) {

                            try {
                                String response = doGETMemberPledges(user).string();

                                save(response, realm);

                                Thread.sleep(DELAY);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally{
                                realm.close();
                            }

                        } else {
                            realm.close();
                            Toast.makeText(RequestAllPledgesService.this, "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        realm.close();
                        isRunning = false;
                        RequestAllPledgesService.this.stopSelf();
                    }
                }
            }
        });

        return START_STICKY;
    }


    private ResponseBody doGETMemberPledges(UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_ALL_MEMBER_PLEDGES)
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

            MemberPledgeResponse coreResponse = gson.fromJson(response, MemberPledgeResponse.class);

            MemberPledgeResult results = coreResponse.getResult();

            List<MemberPledgeItem> items = results.getItems();

            if (items.size() > 0){

                realm.beginTransaction();

                for (MemberPledgeItem item : items) {

                    org.etma.main.pojos.MemberPledge pledge = item.getMemberPledge();

                    MemberPledge existing = realm.where(MemberPledge.class).equalTo("pledgeId", ""+pledge.getId()).findFirst();

                    if (existing == null){

                        org.etma.main.db.MemberPledge memberPledge = new org.etma.main.db.MemberPledge();

                        RealmResults<org.etma.main.db.MemberPledge> pledges = realm.where(org.etma.main.db.MemberPledge.class).findAll();

                        long lastPledgeId;

                        if (pledges.isEmpty()) {
                            memberPledge.setId(0);
                        } else {
                            lastPledgeId = pledges.last().getId();
                            memberPledge.setId(lastPledgeId + 1);
                        }

                        memberPledge.setPledgeId(""+pledge.getId());
                        memberPledge.setStatus("PROCESSED");
                        memberPledge.setAmount(""+pledge.getAmount());
                        memberPledge.setBalance(""+pledge.getBalance());
                        memberPledge.setContributed(""+pledge.getContributed());

                        //format date here
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                        DateTime preliminaryEnd = new DateTime(pledge.getDatePledged());
                        LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                        LocalDate prelimDue = new LocalDate(formattedEnd);

                        String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                        LocalDate date = formatter.parseLocalDate(actualDueDate);

                        memberPledge.setCreationTime(date.toString("MM/dd/yyyy"));
                        memberPledge.setCreatorUserId(""+pledge.getUserId());
                        memberPledge.setDatePledged(date.toString("MM/dd/yyyy"));
                        memberPledge.setDeleterUserId(""+pledge.getUserId());
                        memberPledge.setPledgeStakeId(""+ pledge.getPledgeStakeId());
                        memberPledge.setDeletionTime(date.toString("MM/dd/yyyy"));
                        memberPledge.setEndDate(date.toString("MM/dd/yyyy"));
                        memberPledge.setInitialPayment(""+pledge.getInitialPayment());
                        memberPledge.setIsDeleted("false");
                        memberPledge.setLastModificationTime(pledge.getDatePledged());
                        memberPledge.setLastModifierUserId(""+pledge.getUserId());
                        memberPledge.setLocalPledgeId(pledge.getId());
                        memberPledge.setMemberId(""+pledge.getMemberId());
                        memberPledge.setName(pledge.getName());
                        memberPledge.setPaymentPeriodId(""+pledge.getPaymentPeriodId());
                        memberPledge.setStartDate(date.toString("MM/dd/yyyy"));

                        realm.copyToRealm(memberPledge);
                    }else{
                        existing.setPledgeId(""+pledge.getId());
                        existing.setStatus("PROCESSED");
                        existing.setAmount(""+pledge.getAmount());
                        existing.setBalance(""+pledge.getBalance());
                        existing.setContributed(""+pledge.getContributed());

                        //format date here
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                        DateTime preliminaryEnd = new DateTime(pledge.getDatePledged());
                        LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                        LocalDate prelimDue = new LocalDate(formattedEnd);

                        String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                        LocalDate date = formatter.parseLocalDate(actualDueDate);

                        existing.setCreationTime(date.toString("MM/dd/yyyy"));
                        existing.setCreatorUserId(""+pledge.getUserId());
                        existing.setDatePledged(date.toString("MM/dd/yyyy"));
                        existing.setDeleterUserId(""+pledge.getUserId());
                        existing.setPledgeStakeId(""+ pledge.getPledgeStakeId());
                        existing.setDeletionTime(date.toString("MM/dd/yyyy"));
                        existing.setEndDate(date.toString("MM/dd/yyyy"));
                        existing.setInitialPayment(""+pledge.getInitialPayment());
                        existing.setIsDeleted("false");
                        existing.setLastModificationTime(pledge.getDatePledged());
                        existing.setLastModifierUserId(""+pledge.getUserId());
                        existing.setLocalPledgeId(pledge.getId());
                        existing.setMemberId(""+pledge.getMemberId());
                        existing.setName(pledge.getName());
                        existing.setPaymentPeriodId(""+pledge.getPaymentPeriodId());
                        existing.setStartDate(date.toString("MM/dd/yyyy"));

                        realm.copyToRealmOrUpdate(existing);
                    }


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
