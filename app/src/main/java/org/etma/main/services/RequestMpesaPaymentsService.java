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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.MpesaPayment;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.pojos.LipaNaMpesaItem;
import org.etma.main.pojos.MpesaPaymentResult;
import org.etma.main.pojos.MpesaPaymentsResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RequestMpesaPaymentsService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("RequestMpesaPaymentsService.HandlerThread");
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

    public RequestMpesaPaymentsService() {
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
                                String response = doGETMpesaPaymentsApiCall(user).string();

                                save(response, realm);

                                Thread.sleep(DELAY);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            realm.close();
                            Toast.makeText(RequestMpesaPaymentsService.this, "Turn ON your data bundles to connect!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        realm.close();
                        isRunning = false;
                        RequestMpesaPaymentsService.this.stopSelf();
                    }
                }
            }
        });

        return START_STICKY;
    }


    private ResponseBody doGETMpesaPaymentsApiCall(UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_ALL_LIPA_NA_MPESA)
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

            MpesaPaymentsResponse coreResponse = gson.fromJson(response, MpesaPaymentsResponse.class);

            MpesaPaymentResult results = coreResponse.getResult();

            List<LipaNaMpesaItem> items = results.getItems();

            if (items.size() > 0){

                for (LipaNaMpesaItem item : items) {

                    final org.etma.main.pojos.LipaNaMpesa lipaNaMpesa = item.getLipaNaMpesa();

                    final org.etma.main.db.MpesaPayment existing = realm.where(org.etma.main.db.MpesaPayment.class).equalTo("amortizationId", lipaNaMpesa.getPledgeAmortizationId()).findFirst();

                    if (existing != null) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {

                                existing.setResultCode(lipaNaMpesa.getResultCode());
                                existing.setResultDesc(lipaNaMpesa.getResultDesc());
                                existing.setMpesaPaymentId(Integer.parseInt(lipaNaMpesa.getId().toString()));
                                realm.copyToRealmOrUpdate(existing);
                            }
                        });

                    }else{

                        RealmResults<MpesaPayment> allPayments = realm.where(MpesaPayment.class).findAll().sort("id", Sort.ASCENDING);

                        MpesaPayment mpesaPayment = new MpesaPayment();

                        long lastId;

                        if (allPayments.isEmpty()){
                            mpesaPayment.setId(0);
                        }else{
                            lastId = allPayments.last().getId();
                            mpesaPayment.setId( lastId + 1 );
                        }

                        //format date here
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                        DateTime preliminaryEnd = new DateTime(lipaNaMpesa.getRequestDate());
                        LocalDate formattedEnd = formatter.parseLocalDate(preliminaryEnd.toString("MM/dd/yyyy"));
                        LocalDate prelimDue = new LocalDate(formattedEnd);

                        String actualDueDate = prelimDue.toString("MM/dd/yyyy");

                        LocalDate date = formatter.parseLocalDate(actualDueDate);

                        mpesaPayment.setAmortizationId(lipaNaMpesa.getPledgeAmortizationId());

                        mpesaPayment.setAccountRefDesc(lipaNaMpesa.getAccountRefDesc());
                        mpesaPayment.setAmountToPay(String.valueOf(lipaNaMpesa.getAmountToPay()));
                        mpesaPayment.setCreationTime(lipaNaMpesa.getRequestDate());
                        mpesaPayment.setCreatorUserId(String.valueOf(lipaNaMpesa.getUserId()));
                        mpesaPayment.setDeleterUserId(String.valueOf(lipaNaMpesa.getUserId()));
                        mpesaPayment.setDeletionTime(date.toString("MM/dd/yyyy"));
                        mpesaPayment.setIsDeleted("false");
                        mpesaPayment.setLastModificationTime(date.toString("MM/dd/yyyy"));
                        mpesaPayment.setLastModifierUserId(String.valueOf(date.toString("MM/dd/yyyy")));
                        mpesaPayment.setMpesaReceiptNumber("");
                        mpesaPayment.setPhoneNumber(lipaNaMpesa.getPhoneNumber());
                        mpesaPayment.setRequestDate(date.toString("MM/dd/yyyy"));
                        mpesaPayment.setRespCheckoutRequestID(lipaNaMpesa.getRespCheckoutRequestID());
                        mpesaPayment.setRespCode(lipaNaMpesa.getRespCode());
                        mpesaPayment.setRespCustMsg(lipaNaMpesa.getRespCustMsg());
                        mpesaPayment.setRespDesc(lipaNaMpesa.getRespDesc());
                        mpesaPayment.setRespMerchantRequestID(lipaNaMpesa.getRespMerchantRequestID());
                        mpesaPayment.setResultCode(lipaNaMpesa.getResultCode());
                        mpesaPayment.setResultDesc(lipaNaMpesa.getResultDesc());
                        mpesaPayment.setStatus(lipaNaMpesa.getRespDesc());
                        mpesaPayment.setUserId(String.valueOf(lipaNaMpesa.getUserId()));
                        mpesaPayment.setMpesaPaymentId(Integer.parseInt(lipaNaMpesa.getId().toString()));

                        realm.beginTransaction();
                        realm.copyToRealm(mpesaPayment);
                        realm.commitTransaction();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
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
