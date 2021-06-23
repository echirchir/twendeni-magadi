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
import org.etma.main.db.Amortization;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.pojos.CreateMemberPledge;
import org.etma.main.pojos.CreateMemberPledgeResponse;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CreateMemberPledgeService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private CreateMemberPledgeService.ServiceHandler mServiceHandler;

    public CreateMemberPledgeService() {}

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("CreateMemberPledgeService.HandlerThread");
        mHandlerThread.start();
        mServiceHandler = new CreateMemberPledgeService.ServiceHandler(mHandlerThread.getLooper());

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

                            UserOauth userOauth = realm.where(UserOauth.class).findFirst();

                            final RealmResults<MemberPledge> pledges = realm.where(MemberPledge.class).equalTo("status", "PROCESSING").findAll().sort("id", Sort.ASCENDING);

                            int size = pledges.size();

                            try {
                                if (size > 0) {

                                    final MemberPledge pledge = pledges.get(0);

                                    if (pledge != null){

                                        if (detector.isConnected()){

                                            String response = createMemberPledgeApiCall(pledge, userOauth).string();

                                            Gson gson = new Gson();

                                            final CreateMemberPledgeResponse coreResponse = gson.fromJson(response, CreateMemberPledgeResponse.class);

                                            if (coreResponse.isSuccess()){

                                                //update memberPledge status to completed
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(@NonNull Realm realm) {

                                                        int pledgeId = coreResponse.getResult();

                                                        pledge.setPledgeId(""+ pledgeId);
                                                        pledge.setStatus("PROCESSED");
                                                        realm.copyToRealmOrUpdate(pledge);

                                                        //UPDATE all amortizations belonging to this pledge with pledge ID
                                                        RealmResults<Amortization> amortizationsToUpdate = realm.where(Amortization.class)
                                                                .equalTo("localPledgeId", pledge.getLocalPledgeId())
                                                                .findAll().sort("id", Sort.ASCENDING);

                                                        if (!amortizationsToUpdate.isEmpty()){

                                                            for (Amortization amortization : amortizationsToUpdate){

                                                                amortization.setMemberPledgeId(pledgeId);
                                                                realm.copyToRealmOrUpdate(amortization);
                                                            }
                                                        }

                                                    }
                                                });

                                            }

                                        }else{
                                            Toast.makeText(
                                                    CreateMemberPledgeService.this,
                                                    "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    Thread.sleep(DELAY);
                                } else {
                                    realm.close();
                                    isRunning = false;
                                    CreateMemberPledgeService.this.stopSelf();
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

    private ResponseBody createMemberPledgeApiCall(MemberPledge draft, UserOauth user) throws IOException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateMemberPledge memberPledge = new CreateMemberPledge();

        memberPledge.setName(draft.getName());
        memberPledge.setAmount(draft.getAmount());
        memberPledge.setDatePledged(draft.getDatePledged());
        memberPledge.setInitialPayment(draft.getInitialPayment());
        memberPledge.setContributed(draft.getContributed());
        memberPledge.setBalance(draft.getBalance());
        memberPledge.setActive(draft.getActive());
        memberPledge.setMemberId(draft.getMemberId());
        memberPledge.setUserId(draft.getUserId());
        memberPledge.setPledgeStakeId(draft.getPledgeStakeId());
        memberPledge.setPaymentPeriodId(draft.getPaymentPeriodId());
        memberPledge.setIsDeleted(draft.getIsDeleted());
        memberPledge.setDeleterUserId(draft.getDeleterUserId());
        memberPledge.setDeletionTime(draft.getDeletionTime());
        memberPledge.setLastModificationTime(draft.getLastModificationTime());
        memberPledge.setLastModifierUserId(draft.getLastModifierUserId());
        memberPledge.setCreationTime(draft.getCreationTime());
        memberPledge.setCreatorUserId(draft.getCreatorUserId());
        memberPledge.setId(null);

        String json = gson.toJson(memberPledge);

        return createMemberPledgeCall(json, user);

    }

    private ResponseBody createMemberPledgeCall(String body, UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.CREATE_OR_EDIT_PLEDGE)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + userOauth.getAccessToken())
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
