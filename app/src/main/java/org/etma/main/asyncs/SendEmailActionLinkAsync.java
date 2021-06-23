package org.etma.main.asyncs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.etma.main.URLS;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.EmailActionResponse;
import org.etma.main.pojos.PassResetCodeOrSendActivation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

public class SendEmailActionLinkAsync extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    public  Context context;

    public SendEmailActionLinkAsync(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            String body = getBody(params[0]);

            String bearer = "Bearer " + Util.getValueFromSharedPrefs("BEARER", context);

            OkHttpClient client = new OkHttpClient();

            MediaType json = MediaType.parse("application/json; charset=utf-8");

            RequestBody requestBody = RequestBody.create(json, body);

            Request request = new Request.Builder()
                    .url(URLS.SEND_EMAIL_ACTIVATION_LINK)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", bearer)
                    .addHeader("Abp.TenantId", "5")
                    .build();

            return client.newCall( request ).execute().body().string();

        }catch (SocketTimeoutException e){
            e.getMessage();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){ e.printStackTrace(); }

        return null;
    }

    @Override
    protected void onPostExecute(final String result) {

        super.onPostExecute(result);

        try{

            Gson gson = new Gson();

            EmailActionResponse response = gson.fromJson(result, EmailActionResponse.class);

            if (Boolean.parseBoolean(response.getSuccess())){

                //link sent successfully
            }else{
                //show notification of failure to send link;
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getBody( String email ){

        PassResetCodeOrSendActivation passwordResetCode = new PassResetCodeOrSendActivation();

        passwordResetCode.setEmailAddress(email);

        Gson gson = new Gson();

        return gson.toJson(passwordResetCode);
    }

}
