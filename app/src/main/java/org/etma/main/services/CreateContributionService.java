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
import org.etma.main.db.Contribution;
import org.etma.main.db.MemberPledge;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.pojos.CreateContributionResponse;
import org.etma.main.pojos.CreateOrEditContribution;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CreateContributionService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("CreateContributionService.HandlerThread");
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

    public CreateContributionService() {
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

                            final RealmResults<Contribution> contributions = realm.where(Contribution.class).equalTo("status", "Draft").findAll().sort("id", Sort.ASCENDING);

                            int size = contributions.size();

                            try {
                                if (size > 0) {

                                    final Contribution contribution = contributions.get(0);

                                    String pledgeId = contribution.getMemberPledgeId();

                                    MemberPledge pledge = realm.where(MemberPledge.class).equalTo("pledgeId", pledgeId).findFirst();

                                    if (pledge != null){

                                        if (pledge.getStatus().equals("PROCESSED")){

                                            if (detector.isConnected()){

                                                String response = createContributionApiCall(contribution, userOauth).string();

                                                Gson gson = new Gson();

                                                CreateContributionResponse resp = gson.fromJson(response, CreateContributionResponse.class);

                                                if (resp.isSuccess()){

                                                    realm.executeTransaction(new Realm.Transaction() {
                                                        @Override
                                                        public void execute(@NonNull Realm realm) {

                                                            contribution.setStatus("COMPLETED");
                                                            realm.copyToRealmOrUpdate(contribution);
                                                        }
                                                    });
                                                }

                                                Thread.sleep(DELAY);

                                            }else{
                                                Toast.makeText(
                                                        CreateContributionService.this,
                                                        "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }
                                    Thread.sleep(DELAY);
                                } else {
                                    realm.close();
                                    isRunning = false;
                                    CreateContributionService.this.stopSelf();
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

    private ResponseBody createContributionApiCall(Contribution draft, UserOauth user) throws IOException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateOrEditContribution contribution = new CreateOrEditContribution();

        contribution.setCustom1(draft.getCustom1());
        contribution.setCustom2(draft.getCustom2());
        contribution.setCustom3(draft.getCustom3());
        contribution.setCustom4(draft.getCustom4());
        contribution.setLastModifierUserId(draft.getLastModifierUserId());
        contribution.setPaymentModeId(draft.getPaymentModeId());
        contribution.setDeletionTime(draft.getDeletionTime());
        contribution.setIsDeleted(draft.getIsDeleted());
        contribution.setRefNumber(draft.getRefNumber());
        contribution.setMemberPledgeId(draft.getMemberPledgeId());
        contribution.setId(draft.getContributionId());
        contribution.setAmount(draft.getAmount());
        contribution.setCreatorUserId(draft.getCreatorUserId());
        contribution.setAccountNumber(draft.getAccountNumber());
        contribution.setVerified(draft.getVerified());
        contribution.setContributionDate(draft.getContributionDate());
        contribution.setUserId(draft.getUserId());
        contribution.setDeleterUserId(draft.getDeleterUserId());
        contribution.setCreationTime(draft.getCreationTime());
        contribution.setMemberId(draft.getMemberId());
        contribution.setPaidBy(draft.getPaidBy());
        contribution.setLastModificationTime(draft.getLastModificationTime());
        contribution.setPledgeAmortizationId(draft.getPledgeAmortizationId());
        contribution.setVerifiedBy(draft.getVerifiedBy());

        String json = gson.toJson(contribution);

        return doContributionApiCall(json, user);

    }

    private ResponseBody doContributionApiCall(String body, UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.CREATE_CONTRIBUTION)
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
