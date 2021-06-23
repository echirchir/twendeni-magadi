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
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.CreateMpesaPayment;
import org.etma.main.pojos.MpesaCallResponse;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class LipaNaMpesaService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    public LipaNaMpesaService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("LipaNaMpesaService.HandlerThread");
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

                    static final long DELAY = 5000;

                    @Override
                    public void run() {

                        while (isRunning) {

                            Realm realm = Realm.getDefaultInstance();

                            UserOauth userOauth = realm.where(UserOauth.class).findFirst();

                            final RealmResults<MpesaPayment> payments = realm.where(MpesaPayment.class).equalTo("status", "Draft").findAll().sort("id", Sort.ASCENDING);

                            int size = payments.size();

                            try {
                                if (size > 0) {

                                    final MpesaPayment payment = payments.get(0);

                                    if (payment != null){

                                        if (detector.isConnected()){

                                            String response = createMpesaPaymentApiCall(payment, userOauth).string();

                                            Gson gson = new Gson();

                                            final MpesaCallResponse coreResponse = gson.fromJson(response, MpesaCallResponse.class);

                                            if (coreResponse.isSuccess()){

                                                //update memberPledge status to completed
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(@NonNull Realm realm) {

                                                        payment.setStatus("PROCESSING");
                                                        realm.copyToRealmOrUpdate(payment);

                                                    }
                                                });

                                                //EventBus.getDefault().post( new LipaNaMpesaCompletedEvent(true, "Success"));

                                            }else{
                                                //EventBus.getDefault().post( new LipaNaMpesaCompletedEvent(false, "Failed"));
                                            }

                                        }else{
                                            Toast.makeText(
                                                    LipaNaMpesaService.this,
                                                    "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //EventBus.getDefault().post( new LipaNaMpesaCompletedEvent(false, "Failure"));

                                    Thread.sleep(DELAY);
                                } else {
                                    realm.close();
                                    isRunning = false;
                                    LipaNaMpesaService.this.stopSelf();
                                    //EventBus.getDefault().post( new LipaNaMpesaCompletedEvent(false, "Failure"));
                                }
                            } catch (InterruptedException e) {
                                realm.close();
                                isRunning = false;
                                e.printStackTrace();
                                //EventBus.getDefault().post( new LipaNaMpesaCompletedEvent(false, "Failure"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        return START_STICKY;
    }

    private ResponseBody createMpesaPaymentApiCall(MpesaPayment draft, UserOauth user) throws IOException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateMpesaPayment mpesaPayment = new CreateMpesaPayment();

        mpesaPayment.setId(null);
        mpesaPayment.setAccountRefDesc(draft.getAccountRefDesc());
        mpesaPayment.setAmountToPay(Integer.parseInt(draft.getAmountToPay()));
        mpesaPayment.setCreationTime(draft.getCreationTime());
        mpesaPayment.setCreatorUserId(Integer.parseInt(draft.getCreatorUserId()));
        mpesaPayment.setDeleterUserId(Integer.parseInt(draft.getDeleterUserId()));
        mpesaPayment.setDeletionTime(draft.getDeletionTime());
        mpesaPayment.setIsDeleted(false);
        mpesaPayment.setPledgeAmortizationId(draft.getAmortizationId());
        mpesaPayment.setLastModificationTime(draft.getLastModificationTime());
        mpesaPayment.setLastModifierUserId(Integer.parseInt(draft.getLastModifierUserId()));
        mpesaPayment.setMpesaReceiptNumber("");
        mpesaPayment.setPhoneNumber(draft.getPhoneNumber());
        mpesaPayment.setRequestDate(Util.getCurrentDate());
        mpesaPayment.setRespCheckoutRequestID("");
        mpesaPayment.setRespCode("");
        mpesaPayment.setRespCustMsg("");
        mpesaPayment.setRespDesc("");
        mpesaPayment.setRespMerchantRequestID("");
        mpesaPayment.setResultCode("");
        mpesaPayment.setResultDesc("");
        mpesaPayment.setStatus("Pending");
        mpesaPayment.setUserId(Integer.parseInt(draft.getUserId()));

        String json = gson.toJson(mpesaPayment);

        return doMpesaInitializationCall(json, user);

    }

    private ResponseBody doMpesaInitializationCall(String body, UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.LIPA_NA_MPESA)
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
