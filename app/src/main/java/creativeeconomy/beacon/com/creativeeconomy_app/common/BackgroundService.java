package creativeeconomy.beacon.com.creativeeconomy_app.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.kakao.oreum.common.function.Consumer;
import com.kakao.oreum.tamra.Tamra;
import com.kakao.oreum.tamra.base.NearbySpots;
import com.kakao.oreum.tamra.base.Region;
import com.kakao.oreum.tamra.base.Spot;
import com.kakao.oreum.tamra.base.TamraObserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import creativeeconomy.beacon.com.creativeeconomy_app.MyApplication;
import creativeeconomy.beacon.com.creativeeconomy_app.R;
import creativeeconomy.beacon.com.creativeeconomy_app.ce.CE_Map;
import creativeeconomy.beacon.com.creativeeconomy_app.main.Constants;
import creativeeconomy.beacon.com.creativeeconomy_app.tamra.PageOpen;

/**
 * Created by chihong on 2017-01-17.
 */

public class BackgroundService extends Service{

    String searchUrl = "";
    Context con;
    String PageNm="";

    String spotNm = "";

    MappingCheckClass check;
    String contentsNo;
    String fileUrl="";
    CommonVO commonVO = new CommonVO();
    String localIp = commonVO.localIp;
    String serverIp = commonVO.serverIp;
    String titleNm="";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Jinwoo", "BackgroundService => onCreate()");
        unregisterRestartAlarm();

        con = getApplicationContext();

        if (Constants.pushState.equals("0")) {
            StopMonitoring();
        } else {
            StartMonitoring();
        }
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        PageNm = arg0.getStringExtra("PageNm");

        Log.d("Jinwoo", "BackgroundService => onBind()");

        return null;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Log.d("Jinwoo", "BackgroundService => onStart()");

        if (Constants.pushState.equals("0")) {
            StopMonitoring();
        } else {
            StartMonitoring();
        }
    }

    @Override
    public void onDestroy()
    {
        if (MyApplication.appRun.equals("1")) { //앱이 켜졌을 때
            unregisterRestartAlarm();
        } else if (MyApplication.appRun.equals("0")){   //앱이 백그라운드 혹은 꺼졌을 때
            registerRestartAlarm();
            if (Constants.pushState.equals("0")) {
                StopMonitoring();
            } else {
                StartMonitoring();
            }
        }
        Log.d("Beacon_ranged", "BackgroundService => onDestroy()");
        StopMonitoring();
        super.onDestroy();
    }

    void registerRestartAlarm() {
        Log.d("Jinwoo", "registerRestartAlarm");
        Intent intent = new Intent(BackgroundService.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(
                BackgroundService.this, 0, intent, 0); // 브로드케스트할 Intent
        long firstTime = SystemClock.elapsedRealtime();  // 현재 시간
        firstTime += 1 * 1000; // 10초 후에 알람이벤트 발생
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE); // 알람 서비스 등록
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                10 * 1000, sender); // 알람이

    }

    void unregisterRestartAlarm() {
        Log.d("Jinwoo", "unregisterRestartAlarm");
        Intent intent = new Intent(BackgroundService.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(
                BackgroundService.this, 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);

    }

    public void StartMonitoring() {
        Tamra.addObserver(tamraObserver);
    }

    public void StopMonitoring() {
        Tamra.removeObserver(tamraObserver);
    }


    private final TamraObserver tamraObserver = new TamraObserver() {
        @Override
        public void didEnter(Region region) {

            Log.d("Jinwoo","BackgroundService => ----didEnter ----"+region.name()+"에 진입");
        }

        @Override
        public void didExit(Region region) {

            Log.d("Jinwoo","BackgroundService => ----didExit ----"+region.name()+"에서 이탈");
        }

        @Override
        public void ranged(NearbySpots nearbySpots) {
            final Set<Spot> droppedSet = new HashSet<>();

            Log.d("Jinwoo", "BackgroundService => ranged");

            droppedSet.addAll(Constants.spotSet);
            nearbySpots.orderBy(Spot.ACCURACY_ORDER).foreach(new Consumer<Spot>() {
                @Override
                public void accept(Spot spot) {
                    droppedSet.remove(spot);

                    //if (Constants.spotSet.contains(spot)) return;

                    Constants.spotSet.add(spot);
                    Log.d("Jinwoo", "BackgroundService => raged Start()");
                    Log.d("Jinwoo","----BackgroundService() spotSet----   근처에" + spot.description()+"발견");
                    Log.d("Jinwoo","---- BackgroundService() spot.id() ----" + spot.id());
                    spotNm = spot.description();

                    if (check != null) check = null;

                    check = new MappingCheckClass();
                    if (check.isReceivedMappingContents(String.valueOf(spot.id()))) {
                        // 비콘 id(공항의 37번 비콘)가 37이면 창경으로 찾아가기 맵실행
                        if (spot.id()==37){
                            PathFinding();
                        } else {
                            BeaconScanning(spot.id());
                        }
                    }
                }
            });
            Constants.spotSet.removeAll(droppedSet);
            droppedSet.clear();
        }
    };

    public void  BeaconScanning(long spotId) {

        Log.d("Jinwoo", "BackgroundService => 감지된 spotId ::::: " + spotId);

        // 비콘 id가 0이 아닐경우 진입
        if(spotId != 0) {
            //비콘 id가 0이 아니면서 매핑된 컨텐츠가 있을 경우 진입

            String url="";

            Log.d("Jinwoo", "BackgroundService => onBeaconId   " + spotId);
//            url = "http://192.168.17.6:8080/creativeEconomy/MobileMappingChecked.do?beaconId=" + spotId + "&userKey=ietrceea";
//            url = "http://192.168.17.7:8080/creativeEconomy/MobileMappingChecked.do?beaconId=" + spotId + "&userKey=ietrceea";
            url = serverIp + "/creativeEconomy/UserMappingInfoData.do?beaconId=" + spotId + "&userKey=ietrceea";
            GetDataJSON g = new GetDataJSON();
            g.execute(url);
        }
    }

    private class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String uri = params[0];

            Log.d("Jinwoo","BackgroundService => uri : " + uri);
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                    Log.d("Jinwoo","BackgroundService => json   " + json);
                }

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("Jinwoo",  "BackgroundService => result : " + result);

            JSONObject jsonRes = null;

            if(result == null) {
                Log.d("Jinwoo",  "BackgroundService => result : null");

            } else {

                try{

                    jsonRes = new JSONObject(result);

                    Log.d("Jinwoo",  "BackgroundService => result : " + result);
                    contentsNo = jsonRes.getString("contentsNo");
                    fileUrl = jsonRes.getString("filePath");
                    titleNm = jsonRes.getString("contentsTitle");
                    Log.d("Jinwoo", "BackgroundService => fileUrl : " + fileUrl);

                    if(!"FALSE".equals(contentsNo))
                        showList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showList() {

        Log.d("Jinwoo", "showList() Start;");
        Log.d("Jinwoo", "BackgroundService : " + Constants.pushState);

        if("1".equals(Constants.pushState))
            NotificationSomethings(con);

    }

    private void NotificationSomethings(Context context) {

        Log.d("Jinwoo", "BackgroundService NotificationSomethings()");
        Resources res = context.getResources();

        Intent notificationIntent = new Intent(context, PageOpen.class);
        notificationIntent.putExtra("value", fileUrl); //전달할 값
        notificationIntent.putExtra("PageNm", "service"); //전달할 값
        notificationIntent.putExtra("titleNm", titleNm);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle("창조경제혁신센터 비콘감지!")
                .setContentText(spotNm + "에 연결된 컨텐츠를 감지하였습니다.")
                .setTicker("창조경제혁신센터 비콘감지!")
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.app_icon))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }

    //id가 37번을 받으면 길찾기 노티를 실행
    public void PathFinding() {
            if("1".equals(Constants.pushState))
                NotificationForPathFinding(con);
    }

    private void NotificationForPathFinding(Context context) {

        Log.d("Jinwoo", "BackgroundService NotificationSomethings()");
        Resources res = context.getResources();

        Intent notificationIntent = new Intent(context, CE_Map.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle("창조경제혁신센터의 길안내입니다.")
                .setContentText(/*spotNm + */"창조경제혁신센터의 길안내입니다.")
                .setTicker("창조경제혁신센터의 길안내입니다.")
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.app_icon))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }

}
