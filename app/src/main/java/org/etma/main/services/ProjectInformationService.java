package org.etma.main.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.etma.main.URLS;
import org.etma.main.db.Project;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.NetworkResolver;
import org.etma.main.pojos.ProjectResultItems;
import org.etma.main.pojos.ProjectsResponse;
import org.etma.main.pojos.ProjectsResults;
import org.joda.time.DateTime;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectInformationService extends Service {

    public boolean isRunning = false;

    private NetworkResolver detector;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    public ProjectInformationService() {}

    @Override
    public void onCreate() {
        super.onCreate();

        detector = new NetworkResolver(getApplicationContext());

        mHandlerThread = new HandlerThread("ProjectInformationService.HandlerThread");
        mHandlerThread.start();
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        isRunning = true;
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!isRunning) {
            mHandlerThread.quit();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mServiceHandler.post(
                new Runnable() {

                    static final long DELAY = 1500000;

                    @Override
                    public void run() {
                        while (isRunning) {

                            Realm realm = Realm.getDefaultInstance();

                            UserOauth user = realm.where(UserOauth.class).findFirst();

                            if (user != null) {
                                String bearer = "Bearer " + user.getAccessToken();
                                if (detector.isConnected()) {
                                    try{
                                        requestProjects( realm, bearer );

                                        Thread.sleep(DELAY);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }finally{
                                        realm.close();
                                    }
                                } else {
                                    realm.close();
                                    Toast.makeText(
                                            ProjectInformationService.this,
                                            "Turn ON your data bundles to connect!",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                realm.close();
                                isRunning = false;
                                ProjectInformationService.this.stopSelf();
                            }
                        }
                    }
                });

        return START_STICKY;
    }

    private void requestProjects( Realm realm , String token) throws IOException{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URLS.GET_ALL_PROJECTS)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .addHeader("Abp.TenantId", "5")
                .build();

        String response = client.newCall(request).execute().body().string();

        saveAllProjects( realm, response );

    }

    private void saveAllProjects( Realm realm, String response){

        try{

            Gson gson = new Gson();

            ProjectsResponse coreResponse = gson.fromJson(response, ProjectsResponse.class);

            ProjectsResults results = coreResponse.getResult();

            ProjectResultItems[] items = results.getItems();

            realm.beginTransaction();

            Project project;

            for (ProjectResultItems item : items) {

                org.etma.main.pojos.Project proj = item.getProject();

                Project existing = realm.where(Project.class).equalTo("projectId", proj.getId()).findFirst();

                if (existing == null){

                    project = new Project();

                    RealmResults<org.etma.main.db.Project> existingProjects = realm.where(org.etma.main.db.Project.class).findAll();

                    long lastRelationshipId;

                    if (existingProjects.isEmpty()) {
                        project.setId(0);
                    } else {
                        lastRelationshipId = existingProjects.last().getId();
                        project.setId(lastRelationshipId + 1);
                    }

                    DateTime start = new DateTime(proj.getStartDate());
                    DateTime end = new DateTime(proj.getEndDate());

                    project.setProjectId(proj.getId());
                    project.setEndDate(end.toString("MM/dd/yyyy"));
                    project.setLastContrAmount(proj.getLastContrAmount());
                    project.setLastPledgeAmount(proj.getLastPledgeAmount());
                    project.setProjectName(proj.getProjectName());
                    project.setProjectTarget(proj.getProjectTarget());
                    project.setStartDate(start.toString("MM/dd/yyyy"));
                    project.setTotalContributions(proj.getTotalContributions());
                    project.setTotalPledges(proj.getTotalPledges());

                    realm.copyToRealm(project);
                }else{
                    DateTime start = new DateTime(proj.getStartDate());
                    DateTime end = new DateTime(proj.getEndDate());

                    existing.setProjectId(proj.getId());
                    existing.setEndDate(end.toString("MM/dd/yyyy"));
                    existing.setLastContrAmount(proj.getLastContrAmount());
                    existing.setLastPledgeAmount(proj.getLastPledgeAmount());
                    existing.setProjectName(proj.getProjectName());
                    existing.setProjectTarget(proj.getProjectTarget());
                    existing.setStartDate(start.toString("MM/dd/yyyy"));
                    existing.setTotalContributions(proj.getTotalContributions());
                    existing.setTotalPledges(proj.getTotalPledges());
                    realm.copyToRealmOrUpdate(existing);
                }


            }

            realm.commitTransaction();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}
