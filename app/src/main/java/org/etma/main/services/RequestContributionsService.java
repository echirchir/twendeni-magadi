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
import org.etma.main.db.Contribution;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.PledgeContributionItem;
import org.etma.main.pojos.PledgeContributionResponse;
import org.etma.main.pojos.PledgeContributionResult;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RequestContributionsService extends Service {


    public RequestContributionsService() {
    }

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;


    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("RequestContributionsService.HandlerThread");
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

            static final long DELAY = 8000000;

            @Override
            public void run() {
                while (isRunning) {

                    Realm realm = Realm.getDefaultInstance();
                    UserOauth user = realm.where(UserOauth.class).findFirst();

                    if (user != null) {
                        if (detector.isConnected()) {

                            try {
                                String response = doGETContributionsApiCall(user).string();

                                save(response, realm);

                                Thread.sleep(DELAY);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            realm.close();
                            Toast.makeText(RequestContributionsService.this, "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        realm.close();
                        isRunning = false;
                        RequestContributionsService.this.stopSelf();
                    }
                }
            }
        });

        return START_STICKY;
    }


    private ResponseBody doGETContributionsApiCall(UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_PLEDGE_CONTRIBUTIONS)
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

            PledgeContributionResponse coreResponse = gson.fromJson(response, PledgeContributionResponse.class);

            if (coreResponse.isSuccess()){

                PledgeContributionResult results = coreResponse.getResult();

                List<PledgeContributionItem> items = results.getItems();

                if (items.size() > 0){

                    realm.beginTransaction();

                    for (PledgeContributionItem item : items) {

                        org.etma.main.pojos.PledgeContribution contribution = item.getPledgeContribution();

                        Contribution existing = realm.where(Contribution.class).equalTo("contributionId", ""+contribution.getId()).findFirst();

                        if (existing == null){

                            org.etma.main.db.Contribution newContribution = new org.etma.main.db.Contribution();

                            RealmResults<org.etma.main.db.Contribution> contributions = realm.where(org.etma.main.db.Contribution.class).findAll();

                            long lastId;

                            if (contributions.isEmpty()){

                                newContribution.setId(0);

                            }else{
                                lastId = contributions.last().getId();
                                newContribution.setId( lastId + 1);
                            }

                            newContribution.setCustom1("");
                            newContribution.setCustom2(item.getMemberPledgeName());
                            newContribution.setCustom3("");
                            newContribution.setCustom4("");
                            newContribution.setLastModifierUserId(""+contribution.getUserId());
                            newContribution.setPaymentModeId(String.valueOf(contribution.getPaymentModeId()));
                            newContribution.setDeletionTime(Util.getCurrentDate());
                            newContribution.setIsDeleted("false");
                            newContribution.setContributionId(String.valueOf(contribution.getId()));
                            newContribution.setRefNumber(contribution.getRefNumber());
                            newContribution.setMemberPledgeId(String.valueOf(contribution.getMemberPledgeId()));
                            newContribution.setContributionId(String.valueOf(contribution.getId()));
                            newContribution.setAmount(""+ contribution.getAmount());
                            newContribution.setCreatorUserId(""+contribution.getUserId());
                            newContribution.setAccountNumber(contribution.getAccountNumber());
                            newContribution.setVerified(String.valueOf(contribution.isVerified()));

                            //format date here
                            DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                            DateTime preliminaryEnd = new DateTime(contribution.getContributionDate());
                            LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                            LocalDate prelimDue = new LocalDate(formattedEnd);

                            String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                            LocalDate date = formatter.parseLocalDate(actualDueDate);
                            newContribution.setContributionDate(date.toString("MM/dd/yyyy"));

                            newContribution.setUserId(""+ contribution.getUserId());
                            newContribution.setDeleterUserId("" + contribution.getUserId());
                            newContribution.setCreationTime(Util.getCurrentDate());
                            newContribution.setMemberId(String.valueOf(contribution.getMemberId()));
                            newContribution.setPaidBy(contribution.getPaidBy());
                            newContribution.setLastModificationTime(Util.getCurrentDate());
                            newContribution.setPledgeAmortizationId(""+contribution.getPledgeAmortizationId());
                            newContribution.setVerifiedBy(""+ contribution.getVerifiedBy());
                            newContribution.setStatus("COMPLETED");

                            realm.copyToRealm(newContribution);

                        }else{

                            existing.setCustom1("");
                            existing.setCustom2(item.getMemberPledgeName());
                            existing.setCustom3("");
                            existing.setCustom4("");
                            existing.setLastModifierUserId(""+contribution.getUserId());
                            existing.setPaymentModeId(String.valueOf(contribution.getPaymentModeId()));
                            existing.setDeletionTime(Util.getCurrentDate());
                            existing.setIsDeleted("false");
                            existing.setContributionId(String.valueOf(contribution.getId()));
                            existing.setRefNumber(contribution.getRefNumber());
                            existing.setMemberPledgeId(String.valueOf(contribution.getMemberPledgeId()));
                            existing.setContributionId(String.valueOf(contribution.getId()));
                            existing.setAmount(""+ contribution.getAmount());
                            existing.setCreatorUserId(""+contribution.getUserId());
                            existing.setAccountNumber(contribution.getAccountNumber());
                            existing.setVerified(String.valueOf(contribution.isVerified()));

                            //format date here
                            DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                            DateTime preliminaryEnd = new DateTime(contribution.getContributionDate());
                            LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                            LocalDate prelimDue = new LocalDate(formattedEnd);

                            String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                            LocalDate date = formatter.parseLocalDate(actualDueDate);
                            existing.setContributionDate(date.toString("MM/dd/yyyy"));

                            existing.setUserId(""+ contribution.getUserId());
                            existing.setDeleterUserId("" + contribution.getUserId());
                            existing.setCreationTime(Util.getCurrentDate());
                            existing.setMemberId(String.valueOf(contribution.getMemberId()));
                            existing.setPaidBy(contribution.getPaidBy());
                            existing.setLastModificationTime(Util.getCurrentDate());
                            existing.setPledgeAmortizationId(""+contribution.getPledgeAmortizationId());
                            existing.setVerifiedBy(""+ contribution.getVerifiedBy());
                            existing.setStatus("COMPLETED");

                            realm.copyToRealmOrUpdate(existing);
                        }

                    }
                    realm.commitTransaction();
                }

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
