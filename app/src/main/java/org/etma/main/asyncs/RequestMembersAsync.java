package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.EventLog;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.UserOauth;
import org.etma.main.events.MembersRequestedEvent;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.AllMembersResponse;
import org.etma.main.pojos.Member;
import org.etma.main.pojos.MembersGetItems;
import org.etma.main.pojos.MembersGetResult;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RequestMembersAsync extends AsyncTask<String, Void, String> {

    private Realm realm = Realm.getDefaultInstance();
    private Context context;

    public RequestMembersAsync(Context contxt){

        this.context = contxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            ResponseBody body =  getMembersRelationships();

            return body.string();

        }catch (IOException e){ e.printStackTrace(); }

        return null;
    }

    private ResponseBody getMembersRelationships() throws IOException{

        Realm realm = Realm.getDefaultInstance();

        UserOauth userOauth = realm.where(UserOauth.class).findFirst();

        String bearer = "Bearer " + userOauth.getAccessToken();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_ALL_MEMBERS)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", bearer)
                .addHeader("Abp.TenantId", "5")
                .build();

        return client.newCall(request).execute().body();
    }


    @Override
    protected void onPostExecute(String response) {

        super.onPostExecute(response);

        try{

            Gson gson = new Gson();

            AllMembersResponse coreResponse = gson.fromJson(response, AllMembersResponse.class);

            MembersGetResult results = coreResponse.getResult();

            MembersGetItems[] items = results.getItems();

            if (items.length > 0){
                EventBus.getDefault().post( new MembersRequestedEvent( true, items.length));
            }else{
                EventBus.getDefault().post( new MembersRequestedEvent( false, items.length));
            }

        }catch (Exception e){
            e.printStackTrace();
            EventBus.getDefault().post( new MembersRequestedEvent( false, 0));
        }
    }
}
