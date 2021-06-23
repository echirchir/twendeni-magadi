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
import org.etma.main.events.RegisterUserEvent;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.Error;
import org.etma.main.pojos.RegisterAccountResponse;
import org.etma.main.pojos.RegisterAccountResult;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

public class RegisterAccountAsync extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    public  Context context;

    public RegisterAccountAsync(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            String body = params[0];

            String bearer = "Bearer " + Util.getValueFromSharedPrefs("BEARER", context);

            OkHttpClient client = new OkHttpClient();

            MediaType json = MediaType.parse("application/json; charset=utf-8");

            RequestBody requestBody = RequestBody.create(json, body);

            Request request = new Request.Builder()
                    .url(URLS.REGISTER_ACCOUNT)
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

            RegisterAccountResponse coreResponse = gson.fromJson(result, RegisterAccountResponse.class);

            if (coreResponse.isSuccess()){
                EventBus.getDefault().post(new RegisterUserEvent(coreResponse.getResult().isCanLogin(), "Success"));
            }else{
                Error response = coreResponse.getError();
                EventBus.getDefault().post( new RegisterUserEvent(false, response.getMessage()));
            }

        }catch (Exception e){
            e.printStackTrace();
            EventBus.getDefault().post(new RegisterUserEvent(false, "Failure"));
        }

    }

}
