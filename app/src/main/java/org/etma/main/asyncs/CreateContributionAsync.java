package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.UserOauth;
import org.etma.main.events.CreateContributionEvent;
import org.etma.main.events.MemberPledgeCreatedEvent;
import org.etma.main.pojos.CreateContributionResponse;
import org.etma.main.pojos.CreateMemberPledgeResponse;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.realm.Realm;

public class CreateContributionAsync extends AsyncTask<String, Void, String> {


    private Context context;

    public CreateContributionAsync(Context contxt){

        this.context = contxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        Realm realm = Realm.getDefaultInstance();

        String json = strings[0];

        try {

            ResponseBody body =  createContributionApiCall(json, realm);

            Gson gson = new Gson();

            CreateContributionResponse resp = gson.fromJson(body.string(), CreateContributionResponse.class);

            if (resp.isSuccess()){

                return "SUCCESS";
            }else{ return  "FAILED"; }

        }catch (IOException e){ e.printStackTrace(); }

        return null;
    }

    private ResponseBody createContributionApiCall(String body, Realm realm) throws IOException {

        UserOauth userOauth = realm.where(UserOauth.class).findFirst();

        String bearer = "Bearer " + userOauth.getAccessToken();

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.CREATE_CONTRIBUTION)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", bearer)
                .addHeader("Abp.TenantId", "5")
                .build();

        return client.newCall( request ).execute().body();

    }

    @Override
    protected void onPostExecute(String response) {

        super.onPostExecute(response);

        if (response != null){

            if (response.equals("SUCCESS")){
                EventBus.getDefault().post( new CreateContributionEvent( true ));
            }else{
                EventBus.getDefault().post( new CreateContributionEvent( false ));
            }

        }else{
            EventBus.getDefault().post( new CreateContributionEvent( false ));
        }

    }
}
