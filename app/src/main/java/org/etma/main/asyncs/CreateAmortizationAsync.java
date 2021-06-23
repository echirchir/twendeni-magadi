package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.events.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.Amortization;
import org.etma.main.db.UserOauth;
import org.etma.main.events.AmortizationCreatedEvent;
import org.etma.main.events.MemberPledgeCreatedEvent;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.AmortizationList;
import org.etma.main.pojos.CreateAmortizationResponse;
import org.etma.main.pojos.CreateBatchAmortizations;
import org.etma.main.pojos.CreateMemberPledgeResponse;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CreateAmortizationAsync extends AsyncTask<String, Void, String> {


    private Context context;

    public CreateAmortizationAsync(Context contxt){

        this.context = contxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        Realm realm = Realm.getDefaultInstance();

        String json = strings[0];

        try {

            ResponseBody response =  createAmortizationApiCall(json, realm);

            Gson gson = new Gson();

            final CreateAmortizationResponse coreResponse = gson.fromJson(response.string(), CreateAmortizationResponse.class);

            if (coreResponse.isSuccess()){

                RequestAmortizationsAsync async = new RequestAmortizationsAsync(context);
                async.executeOnExecutor(THREAD_POOL_EXECUTOR);

                EventBus.getDefault().post( new AmortizationCreatedEvent( true ));

            }else{

                EventBus.getDefault().post( new AmortizationCreatedEvent( false ));
            }


        }catch (IOException e){ e.printStackTrace(); }

        return null;
    }

    private ResponseBody createAmortizationApiCall(String json, Realm realm) throws IOException {

        return doAmortizationApiCall(json, realm);

    }

    private ResponseBody doAmortizationApiCall(String body, Realm realm) throws IOException {

        UserOauth userOauth = realm.where(UserOauth.class).findFirst();

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.CREATE_AMORTIZATION_LIST)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + userOauth.getAccessToken())
                .addHeader("Abp.TenantId", "5")
                .build();

        return client.newCall( request ).execute().body();

    }

    @Override
    protected void onPostExecute(String response) {

        super.onPostExecute(response);

    }
}
