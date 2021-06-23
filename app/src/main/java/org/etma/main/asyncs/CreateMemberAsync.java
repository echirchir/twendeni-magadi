package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.Member;
import org.etma.main.helpers.Util;
import org.etma.main.pojos.CreateMember;
import org.etma.main.pojos.MemberCreateResponse;

import java.io.IOException;

import io.realm.Realm;

public class CreateMemberAsync extends AsyncTask<String, Void, String> {


    private Context context;

    public CreateMemberAsync(Context contxt){

        this.context = contxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        Realm realm = Realm.getDefaultInstance();

        try {

            final org.etma.main.db.Member draft = realm.where(org.etma.main.db.Member.class).equalTo("status", "Draft").findFirst();

            if (draft != null){

                ResponseBody body =  createMemberApiCall(draft);

                Gson gson = new Gson();

                MemberCreateResponse coreResponse = gson.fromJson(body.string(), MemberCreateResponse.class);

                if (coreResponse.isSuccess()){

                    //update member status to completed
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            draft.setStatus("COMPLETED");
                            realm.copyToRealmOrUpdate(draft);
                        }
                    });

                }
            }


        }catch (IOException e){ e.printStackTrace(); }

        return null;
    }

    private ResponseBody createMemberApiCall(Member draft) throws IOException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create();

        CreateMember member = new CreateMember();

        member.setId(null);
        member.setFullName(draft.getFullName());
        member.setCellphone(draft.getCellphone());
        member.setEmailAddress(draft.getEmailAddress());
        member.setCustom1(draft.getCustom1());
        member.setCustom2(draft.getCustom2());
        member.setCustom3(draft.getCustom3());
        member.setMemberRelationshipId(Integer.parseInt(draft.getMemberRelationshipId()));
        member.setDeleted(false);
        member.setUserId(Integer.parseInt(draft.getUserId()));
        member.setDeleterUserId(Integer.parseInt(draft.getDeleterUserId()));
        member.setDeletionTime(draft.getDeletionTime());
        member.setLastModificationTime(draft.getLastModificationTime());
        member.setLastModifierUserId(Integer.parseInt(draft.getLastModifierUserId()));
        member.setCreationTime(draft.getCreationTime());
        member.setCreatorUserId(Integer.parseInt(draft.getCreatorUserId()));

        String json = gson.toJson(member);

        return createMemberCall(json);

    }

    private ResponseBody createMemberCall(String body) throws IOException {

        String bearer = "Bearer " + Util.getValueFromSharedPrefs("BEARER", context);

        OkHttpClient client = new OkHttpClient();

        MediaType json = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(json, body);

        Request request = new Request.Builder()
                .url(URLS.CREATE_OR_EDIT_MEMBER)
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
