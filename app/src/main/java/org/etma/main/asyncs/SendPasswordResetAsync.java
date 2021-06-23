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
import org.etma.main.db.UserOauth;
import org.etma.main.events.PasswordResetCompletedEvent;
import org.etma.main.events.PasswordResetEmailSentEvent;
import org.etma.main.pojos.PasswordResetCodeResponse;
import org.etma.main.pojos.PasswordResetResponse;
import org.etma.main.pojos.PasswordResetResult;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import io.realm.Realm;

public class SendPasswordResetAsync extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    public  Context context;

    public SendPasswordResetAsync(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            String body = params[0];

            Realm realm = Realm.getDefaultInstance();

            UserOauth oauth = realm.where(UserOauth.class).findFirst();

            String bearer = "Bearer " + oauth.getAccessToken();

            OkHttpClient client = new OkHttpClient();

            MediaType json = MediaType.parse("application/json; charset=utf-8");

            RequestBody requestBody = RequestBody.create(json, body);

            Request request = new Request.Builder()
                    .url(URLS.RESET_PASSWORD)
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

        Gson gson = new Gson();

        if (result != null){
            PasswordResetResponse resetCodeResponse = gson.fromJson(result, PasswordResetResponse.class);

            if (resetCodeResponse.isSuccess()){

                PasswordResetResult resetResult = resetCodeResponse.getResult();

                if (resetResult.isCanLogin()){
                    EventBus.getDefault().post( new PasswordResetCompletedEvent(true));
                }else{
                    EventBus.getDefault().post( new PasswordResetCompletedEvent(false));
                }

            }else{
                EventBus.getDefault().post( new PasswordResetCompletedEvent(false));
            }
        }else{
            EventBus.getDefault().post( new PasswordResetCompletedEvent(false));
        }

    }

}
