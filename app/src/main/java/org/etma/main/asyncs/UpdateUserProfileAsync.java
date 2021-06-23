package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.Member;
import org.etma.main.db.UserOauth;
import org.etma.main.events.UpdateUserProfileEvent;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.CreateMember;
import org.etma.main.pojos.MemberCreateResponse;
import org.etma.main.pojos.UpdateUserProfile;
import org.etma.main.pojos.UpdateUserProfileResponse;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import io.realm.Realm;

public class UpdateUserProfileAsync extends AsyncTask<String, Void, String> {


    private Context context;

    public UpdateUserProfileAsync(Context contxt){

        this.context = contxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {

            String body = strings[0];

            Realm realm = Realm.getDefaultInstance();

            UserOauth oauth = realm.where(UserOauth.class).findFirst();

            String bearer = "Bearer " + oauth.getAccessToken();

            OkHttpClient client = new OkHttpClient();

            MediaType json = MediaType.parse("application/json; charset=utf-8");

            RequestBody requestBody = RequestBody.create(json, body);

            Request request = new Request.Builder()
                    .url(URLS.UPDATE_USER_PROFILE)
                    .put(requestBody)
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
    protected void onPostExecute(String response) {

        super.onPostExecute(response);

        Util.prettyPrintJson(response);

        if (response != null){

            Gson gson = new Gson();

            UpdateUserProfileResponse coreResponse = gson.fromJson(response, UpdateUserProfileResponse.class);

            if (coreResponse.isSuccess()){

                EventBus.getDefault().post( new UpdateUserProfileEvent( true ));
            }else{
                EventBus.getDefault().post( new UpdateUserProfileEvent( false ));
            }
        }else{
            EventBus.getDefault().post( new UpdateUserProfileEvent( false ));
        }

    }
}
