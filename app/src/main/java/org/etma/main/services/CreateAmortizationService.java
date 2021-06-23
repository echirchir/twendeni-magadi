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
import org.etma.main.pojos.AmortizationList;
import org.etma.main.pojos.CreateAmortizationResponse;
import org.etma.main.pojos.CreateBatchAmortizations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CreateAmortizationService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("CreateAmortizationService.HandlerThread");
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

    public CreateAmortizationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mServiceHandler.post(
                new Runnable() {

                    static final long DELAY = 6000;

                    @Override
                    public void run() {

                        while (isRunning) {

                            Realm realm = Realm.getDefaultInstance();

                            UserOauth userOauth = realm.where(UserOauth.class).findFirst();

                            RealmResults<MemberPledge> memberPledges = realm.where(MemberPledge.class).equalTo("status", "PROCESSED").findAll().sort("id");

                            if (!memberPledges.isEmpty()){

                                //loop through pledges and query for amortizations that are Drafts; then sync
                                for (MemberPledge pledge : memberPledges){

                                    final RealmResults<Amortization> amortizations = realm.where(Amortization.class)
                                            .equalTo("localPledgeId", pledge.getLocalPledgeId())
                                            .equalTo("status", "Draft")
                                            .findAll().sort("id");

                                    if (!amortizations.isEmpty()){

                                        try {

                                            if (detector.isConnected()){

                                                String response = createAmortizationApiCall(amortizations, userOauth).string();

                                                Gson gson = new Gson();

                                                final CreateAmortizationResponse coreResponse = gson.fromJson(response, CreateAmortizationResponse.class);

                                                if (coreResponse.isSuccess()){

                                                    //update amortization status to completed
                                                    realm.executeTransaction(new Realm.Transaction() {
                                                        @Override
                                                        public void execute(@NonNull Realm realm) {

                                                            for (Amortization amortization : amortizations){

                                                                amortization.setStatus("COMPLETED");
                                                                realm.copyToRealmOrUpdate(amortization);
                                                            }
                                                        }
                                                    });

                                                }

                                            }else{
                                                Toast.makeText(
                                                        CreateAmortizationService.this,
                                                        "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                                            }

                                            Thread.sleep(DELAY);
                                        } catch (InterruptedException e) {
                                            realm.close();
                                            isRunning = false;
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            }

                        }
                    }
                });

        return START_STICKY;
    }

    private ResponseBody createAmortizationApiCall(RealmResults<Amortization> amortizations, UserOauth user) throws IOException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateBatchAmortizations batchAmortizations = new CreateBatchAmortizations();

        List<AmortizationList> amortizationList = new ArrayList<>();

        for (Amortization draft : amortizations){

            AmortizationList amortization = new AmortizationList();

            amortization.setAmount(draft.getAmount());
            amortization.setContributionDate(draft.getContributionDate());
            amortization.setDateContributed(draft.getDateContributed());
            amortization.setFullyContributed(draft.isFullyContributed());
            amortization.setContributed(draft.getContributed());
            amortization.setBalance(draft.getBalance());
            amortization.setUserId(draft.getUserId());
            amortization.setMemberPledgeId(draft.getMemberPledgeId());
            amortization.setMemberId(draft.getMemberId());
            amortization.setIsDeleted(draft.isDeleted());
            amortization.setDeleterUserId(draft.getDeleterUserId());
            amortization.setDeletionTime(draft.getDeletionTime());
            amortization.setLastModificationTime(draft.getLastModificationTime());
            amortization.setLastModifierUserId(draft.getLastModifierUserId());
            amortization.setCreationTime(draft.getCreationTime());
            amortization.setCreatorUserId(draft.getCreatorUserId());
            amortization.setId(null);

            amortizationList.add(amortization);
        }

        batchAmortizations.setList(amortizationList);

        String json = gson.toJson(batchAmortizations);

        return doAmortizationApiCall(json, user);

    }

    private ResponseBody doAmortizationApiCall(String body, UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.CREATE_AMORTIZATION_LIST)
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
