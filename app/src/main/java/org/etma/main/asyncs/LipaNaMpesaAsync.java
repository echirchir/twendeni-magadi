package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.UserOauth;
import org.etma.main.events.LipaNaMpesaCompletedEvent;
import org.etma.main.events.LipaNaMpesaExtraMileEvent;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.MpesaCallResponse;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.realm.Realm;

public class LipaNaMpesaAsync extends AsyncTask<String, Void, String> {

      public Context context;

      public LipaNaMpesaAsync(Context contxt) {

        this.context = contxt;
      }

      @Override
      protected String doInBackground(String... strings) {

        Realm realm = Realm.getDefaultInstance();

        String body = strings[0];

        final int amortizationId = Integer.parseInt(strings[1]);

        long mpesaPaymentId = Long.parseLong(strings[2]);

        try {

          UserOauth userOauth = realm.where(UserOauth.class).findFirst();

          if (body != null) {

                String responseBody = createMpesaPaymentApiCall(body, userOauth).string();

                //Util.prettyPrintJson(responseBody);

                Gson gson = new Gson();

               try {

                  final MpesaCallResponse coreResponse = gson.fromJson(responseBody, MpesaCallResponse.class);

                  if (coreResponse.isSuccess()) {

                      return coreResponse.getResult().getRespCode() +","+ coreResponse.getResult().getRespDesc();

                  }else{
                      return "";
                  }

              }catch (Exception e){
                  e.printStackTrace();

                  return "";
              }finally{
                  realm.close();
              }


          }

        } catch (IOException e) {
          e.printStackTrace();
        }

        return null;
      }

      private ResponseBody createMpesaPaymentApiCall(String json, UserOauth userOauth)
          throws IOException {

        return createMpesaApiCall(json, userOauth);
      }

      private ResponseBody createMpesaApiCall(String body, UserOauth userOauth) throws IOException {

        String bearer = "Bearer " + userOauth.getAccessToken();

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.LIPA_NA_MPESA)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", bearer)
                .addHeader("Abp.TenantId", "5")
                .build();

        return client.newCall(request).execute().body();
      }

      @Override
      protected void onPostExecute(String response) {

          super.onPostExecute(response);

          if (response != null && !response.equals("")){

              String payload = response.split(",")[0];
              String status = response.split(",")[1];

              EventBus.getDefault().post( new LipaNaMpesaCompletedEvent(true, payload, status));
          }else{
              EventBus.getDefault().post( new LipaNaMpesaCompletedEvent(false, "", ""));
          }
      }
}
