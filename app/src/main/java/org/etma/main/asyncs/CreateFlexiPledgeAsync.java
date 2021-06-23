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
import org.etma.main.events.FlexiPledgeCreatedEvent;
import org.etma.main.events.MemberPledgeCreatedEvent;
import org.etma.main.pojos.CreateMemberPledgeResponse;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.realm.Realm;

public class CreateFlexiPledgeAsync extends AsyncTask<String, Void, String> {


    private Context context;

    public CreateFlexiPledgeAsync(Context contxt){

        this.context = contxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        Realm realm = Realm.getDefaultInstance();

        String json = strings[0];

        try {

            ResponseBody body =  createMemberPledgeApiCall(json, realm);

            Gson gson = new Gson();

            CreateMemberPledgeResponse coreResponse = gson.fromJson(body.string(), CreateMemberPledgeResponse.class);

            if (coreResponse != null){

                if (coreResponse.isSuccess()){

                    //update memberPledge status to completed
                    int pledgeId = coreResponse.getResult();

                    EventBus.getDefault().post( new FlexiPledgeCreatedEvent( true, pledgeId));

                }else{
                    EventBus.getDefault().post( new FlexiPledgeCreatedEvent( false, 0));
                }
            }else{
                EventBus.getDefault().post( new FlexiPledgeCreatedEvent( false, 0));
            }

        }catch (IOException e){ e.printStackTrace(); }

        return null;
    }

    private ResponseBody createMemberPledgeApiCall(String json, Realm realm) throws IOException {

        return createMemberPledgeCall(json, realm);

    }

    private ResponseBody createMemberPledgeCall(String body, Realm realm) throws IOException {

        UserOauth userOauth = realm.where(UserOauth.class).findFirst();

        String bearer = "Bearer " + userOauth.getAccessToken();

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.CREATE_OR_EDIT_PLEDGE)
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

    }
}
