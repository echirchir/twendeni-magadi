package org.etma.main.asyncs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.etma.main.URLS;
import org.etma.main.db.UserOauth;
import org.etma.main.events.LoginSuccessEvent;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.AuthenticationResponse;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmResults;

public class AuthenticateAsync extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    public  Context context;

    public AuthenticateAsync(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            String body = params[0];

            OkHttpClient client = new OkHttpClient();

            MediaType json = MediaType.parse("application/json; charset=utf-8");

            RequestBody requestBody = RequestBody.create(json, body);

            Request request = new Request.Builder()
                    .url(URLS.AUTHENTICATE_LOGIN)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
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

        if (result != null){

            try{

                Gson gson = new Gson();

                final AuthenticationResponse response = gson.fromJson(result, AuthenticationResponse.class);

                if (response.isSuccess()){

                    String token = response.getResult().getAccessToken();

                    Util.storeValueInSharedPrefs(context, token);

                    Realm realm = Realm.getDefaultInstance();

                    if (realm.isInTransaction()){
                        realm.cancelTransaction();
                    }

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {

                            RealmResults<UserOauth> oauths = realm.where(UserOauth.class).findAll();
                            if (!oauths.isEmpty()){
                                oauths.deleteAllFromRealm();
                            }

                            UserOauth newInstance = new UserOauth();
                            newInstance.setId(1);
                            newInstance.setAccessToken(response.getResult().getAccessToken());
                            newInstance.setEncryptedAccessToken(response.getResult().getEncryptedAccessToken());
                            newInstance.setExpiresInSeconds(response.getResult().getExpireInSeconds());
                            newInstance.setRequiresTwoFactorVerification(true);
                            newInstance.setPasswordResetCode(null);
                            newInstance.setShouldResetPassword(true);
                            newInstance.setTwoFactorRememberClientToken(null);
                            newInstance.setUserId(response.getResult().getUserId());

                            realm.copyToRealm(newInstance);
                        }
                    });

                    EventBus.getDefault().post( new LoginSuccessEvent( true));
                }else{
                    EventBus.getDefault().post( new LoginSuccessEvent( false));
                }

            }catch (Exception e){
                e.printStackTrace();
                EventBus.getDefault().post( new LoginSuccessEvent( false));
            }

        }

    }

}
