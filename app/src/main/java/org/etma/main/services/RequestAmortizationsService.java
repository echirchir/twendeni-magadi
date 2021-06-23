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
import org.etma.main.db.Amortization;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.AmortizationResult;
import org.etma.main.pojos.AmortizationResultItems;
import org.etma.main.pojos.PledgeAmortizationResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RequestAmortizationsService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;


    public RequestAmortizationsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("RequestAmortizationsService.HandlerThread");
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

            static final long DELAY = 800000;

            @Override
            public void run() {
                while (isRunning) {

                    Realm realm = Realm.getDefaultInstance();
                    UserOauth user = realm.where(UserOauth.class).findFirst();

                    if (user != null) {
                        if (detector.isConnected()) {

                            try {
                                String response = doGETPledgeAmortizations(user).string();

                                save(response, realm);

                                Thread.sleep(DELAY);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            realm.close();
                            Toast.makeText(RequestAmortizationsService.this, "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        realm.close();
                        isRunning = false;
                        RequestAmortizationsService.this.stopSelf();
                    }
                }
            }
        });

        return START_STICKY;
    }


    private ResponseBody doGETPledgeAmortizations(UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_PLEDGE_AMORTIZATIONS)
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

            PledgeAmortizationResponse coreResponse = gson.fromJson(response, PledgeAmortizationResponse.class);

            AmortizationResult results = coreResponse.getResult();

            AmortizationResultItems[] items = results.getItems();

            if (items.length > 0){

                realm.beginTransaction();

                for (AmortizationResultItems item : items) {

                    Amortization existing = realm.where(Amortization.class).equalTo("amortizationId", Integer.parseInt(item.getPledgeAmortization().getId())).findFirst();

                    if (existing == null){

                        RealmResults<org.etma.main.db.Amortization> amortizations = realm.where(org.etma.main.db.Amortization.class).findAll().sort("id", Sort.ASCENDING);

                        Amortization amortization = new Amortization();

                        long lastAmortizationId;

                        if (amortizations.isEmpty()) {
                            amortization.setId(0);
                        } else {
                            lastAmortizationId = amortizations.last().getId();
                            amortization.setId(lastAmortizationId + 1);
                        }

                        amortization.setAmortizationId(Integer.parseInt(item.getPledgeAmortization().getId()));
                        amortization.setMemberPledgeId(Integer.parseInt(item.getPledgeAmortization().getMemberPledgeId()));
                        amortization.setUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        amortization.setStatus("COMPLETED");
                        amortization.setMemberId(Integer.parseInt(item.getPledgeAmortization().getMemberId()));
                        amortization.setLocalPledgeId(Integer.parseInt(item.getPledgeAmortization().getMemberPledgeId()));
                        amortization.setLastModifierUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        amortization.setLastModificationTime(Util.getCurrentDate());
                        amortization.setFullyContributed(Boolean.parseBoolean(item.getPledgeAmortization().getFullyContributed()));
                        amortization.setDeletionTime(Util.getCurrentDate());
                        amortization.setDeleterUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        amortization.setDeleted(false);
                        amortization.setDateContributed(item.getPledgeAmortization().getDateContributed());
                        amortization.setCreatorUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        amortization.setCreationTime(item.getPledgeAmortization().getContributionDate());

                        //format date here
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                        DateTime preliminaryEnd = new DateTime(item.getPledgeAmortization().getContributionDate());
                        LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                        LocalDate prelimDue = new LocalDate(formattedEnd);

                        String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                        LocalDate date = formatter.parseLocalDate(actualDueDate);

                        amortization.setContributionDate(date.toString("MM/dd/yyyy"));
                        amortization.setContributed((int)Double.parseDouble(item.getPledgeAmortization().getContributed()));
                        amortization.setBalance((int)Double.parseDouble(item.getPledgeAmortization().getBalance()));
                        amortization.setAmount((int)Double.parseDouble(item.getPledgeAmortization().getAmount()));

                        realm.copyToRealm(amortization);
                    }else{

                        existing.setAmortizationId(Integer.parseInt(item.getPledgeAmortization().getId()));
                        existing.setMemberPledgeId(Integer.parseInt(item.getPledgeAmortization().getMemberPledgeId()));
                        existing.setUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        existing.setStatus("COMPLETED");
                        existing.setMemberId(Integer.parseInt(item.getPledgeAmortization().getMemberId()));
                        existing.setLocalPledgeId(Integer.parseInt(item.getPledgeAmortization().getMemberPledgeId()));
                        existing.setLastModifierUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        existing.setLastModificationTime(Util.getCurrentDate());
                        existing.setFullyContributed(Boolean.parseBoolean(item.getPledgeAmortization().getFullyContributed()));
                        existing.setDeletionTime(Util.getCurrentDate());
                        existing.setDeleterUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        existing.setDeleted(false);
                        existing.setDateContributed(item.getPledgeAmortization().getDateContributed());
                        existing.setCreatorUserId(Integer.parseInt(item.getPledgeAmortization().getUserId()));
                        existing.setCreationTime(item.getPledgeAmortization().getContributionDate());

                        //format date here
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                        DateTime preliminaryEnd = new DateTime(item.getPledgeAmortization().getContributionDate());
                        LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                        LocalDate prelimDue = new LocalDate(formattedEnd);

                        String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                        LocalDate date = formatter.parseLocalDate(actualDueDate);

                        existing.setContributionDate(date.toString("MM/dd/yyyy"));
                        existing.setContributed((int)Double.parseDouble(item.getPledgeAmortization().getContributed()));
                        existing.setBalance((int)Double.parseDouble(item.getPledgeAmortization().getBalance()));
                        existing.setAmount((int)Double.parseDouble(item.getPledgeAmortization().getAmount()));

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
