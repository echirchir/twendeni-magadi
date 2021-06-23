package org.etma.main.asyncs;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.etma.main.URLS;
import org.etma.main.db.EtmaUser;
import org.etma.main.db.UserOauth;
import org.etma.main.pojos.GetUserInformationResponse;
import org.etma.main.pojos.User;
import org.etma.main.pojos.UserInformationResult;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

public class RequestUserInformation extends AsyncTask<String, Void, String> {

    private Context context;

    public RequestUserInformation(Context contxt){

        this.context = contxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        Realm realm = Realm.getDefaultInstance();

        UserOauth user = realm.where(UserOauth.class).findFirst();

        try {
            String response = doGETUserInformation(user).string();

            boolean success = save(response, realm);

            if (success){
                return "SUCCESS";
            }

            return "FAILED";

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private ResponseBody doGETUserInformation(UserOauth userOauth) throws IOException {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(URLS.GET_USER_INFORMATION).newBuilder();

        urlBuilder.addQueryParameter("Id", ""+userOauth.getUserId());

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + userOauth.getAccessToken())
                .addHeader("Abp.TenantId", "5")
                .build();

        return client.newCall(request).execute().body();
    }

    private boolean save( String response, Realm realm ){

        try{

            realm.beginTransaction();

            Gson gson = new Gson();

            GetUserInformationResponse coreResponse = gson.fromJson(response, GetUserInformationResponse.class);

            if (coreResponse.isSuccess()){

                UserInformationResult results = coreResponse.getResult();

                User user = results.getUser();

                if (user != null){

                    EtmaUser existing = realm.where(EtmaUser.class).findFirst();

                    if (existing == null) {

                        RealmResults<EtmaUser> totalUsers = realm.where(org.etma.main.db.EtmaUser.class).findAll().sort("id");

                        EtmaUser etmaUser = new EtmaUser();

                        long lastId;

                        if (totalUsers.isEmpty()) {
                            etmaUser.setId(0);
                        } else {
                            lastId = totalUsers.last().getId();
                            etmaUser.setId(lastId + 1);
                        }

                        etmaUser.setCell_phone(user.getUserName());
                        etmaUser.setEmail_address(user.getEmailAddress());
                        etmaUser.setFull_name(user.getName());
                        etmaUser.setPassword(user.getPassword());
                        etmaUser.setSurname(user.getSurname());
                        etmaUser.setCan_login(true);

                        realm.copyToRealm(etmaUser);

                        realm.commitTransaction();
                    }else{
                        existing.setCell_phone(user.getUserName());
                        existing.setEmail_address(user.getEmailAddress());
                        existing.setFull_name(user.getName());
                        existing.setPassword(user.getPassword());
                        existing.setSurname(user.getSurname());
                        existing.setCan_login(true);

                        realm.copyToRealmOrUpdate(existing);
                    }

                }

                return coreResponse.isSuccess();
            }else{
                //notify of failure;
                return false;
            }


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @Override
    protected void onPostExecute(String response) {

        super.onPostExecute(response);

    }
}
