package creativeeconomy.beacon.com.creativeeconomy_app;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import creativeeconomy.beacon.com.creativeeconomy_app.main.Constants;

import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.gps1;
import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.gps2;
import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lat;
import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lon;


/**
 * Created by ggg on 2016-12-20.
 */

public class MyApplication extends Application {


    private IntentFilter scrOnFilter;
    private IntentFilter scrOffFilter;
    SharedPreferences pref;
    public SharedPreferences.Editor editor = null;
    BluetoothAdapter adapter;

    public static Context context;

    public static LocationManager locationManager;
    public static LocationListener locationListener;

    public static boolean useGps = false;
    public static boolean firstCheck = false;

    public static String appRun = "";


    @Override
    public void onCreate() {
        super.onCreate();

        appRun = "1";

        init();

        bluetoothStateCheck();
        pushStateCheck();

        //화면이 켜져 있을 때
        Constants.scrOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("ko", "SCREEN ON ");

                Constants.mIsReceiverRegistered = true;
                //unregisterReceiver(scrOffReceiver);
            }
        };

        Log.d("Jinwoo",  "MyApplication => Constants.pushState : " + Constants.pushState);

        /*Constants.scrOffReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {


                Log.d("ko", "SCREEN OFF");

                Constants.mIsReceiverRegistered = false;


                if("1".equals(Constants.pushState)) {   // 화면이 꺼져 있으면서 푸시 알림이 켜져있을 경우
                    Log.d("ko", "MyApplication => Constants.pushState : 1");

                    Intent offIntent  = new Intent(MyApplication.this, BReceive.class);

                    sendBroadcast(offIntent);
                } else if("0".equals(Constants.pushState)) {
                    Log.d("ko", "MyApplication => Constants.pushState : 0");
                }
            }
        };*/

        scrOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        scrOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        registerReceiver(Constants.scrOnReceiver, scrOnFilter);
        registerReceiver(Constants.scrOffReceiver, scrOffFilter);

        find_Location();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterReceiver(Constants.scrOnReceiver);
        unregisterReceiver(Constants.scrOffReceiver);
    }

    public void bluetoothStateCheck() {

        if(adapter.isEnabled()) { // 블루투스가 켜져있을 경우
            Constants.bluetoothState = "1";
        } else {                    // 블루투스가 꺼져있을 경우
            Constants.bluetoothState = "0";
        }
    }

    /* 설정 푸시 상태 값 가져오기 위함. */
    public void pushStateCheck() {

        Log.d("Jinwoo",  "pushState : " + Constants.pushState);
        if(Constants.pushState == null || "".equals(Constants.pushState)) {
            editor.putString("push", "1");
            editor.commit();

            Constants.pushState = "1";
        }
    }

    /* Application Class 초기화 함수kosaf24574  */
    public void init() {

        pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Constants.pushState = pref.getString("push", "");

        adapter = BluetoothAdapter.getDefaultAdapter();     //블루투스 어댑터
    }

    public void find_Location() {

        Log.i("gps", "in find_location");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {


            public void onLocationChanged(Location location) {

                gps1 = "";
                gps2 = "";

                gps1 = String.valueOf(location.getLatitude());
                gps2 = String.valueOf(location.getLongitude());

                Log.d("Map", "before gps1 : " + gps1);
                Log.d("Map", "before gps2 : " + gps2);

                int index1 = gps1.indexOf(".");
                int index2 = gps2.indexOf(".");

                String tempGps1 = gps1.substring(0,index1);
                if(gps1.length() > 9) {
                    tempGps1 += gps1.substring(index1, index1 +7);
                    Log.d("Map", "Length()1 : " + gps1.length());
                } else
                    tempGps1 = gps1;

                String tempGps2 = gps2.substring(0,index2);

                if(gps2.length() > 9) {
                    tempGps2 += gps2.substring(index2, index2 + 7);
                    Log.d("Map", "Length()2 : " + gps2.length());
                } else
                    tempGps2 = gps2;
//				tempGps2 += gps2.substring(index2,  index2 + 7);

                gps1 = tempGps1;
                gps2 = tempGps2;

                Log.d("Map", "after gps1 : " + gps1);
                Log.d("Map", "after gps2 : " + gps2);

                lat = Double.parseDouble(gps1);
                lon = Double.parseDouble(gps2);
                Log.d("Map", "lat -> " + lat + "   lon -> " + lon);


//				Log.i("gps", "in onLocationChanged - GPS1 IS : " + gps1);
//				Log.i("gps", "in onLocationChanged - GPS2 IS : " + gps2);

                locationManager.removeUpdates(locationListener);
            }

            public void onProviderDisabled(String provider) {
                // Log.i("log", "onProviderDisabled");

                useGps = false;

                if (!firstCheck) {
                    firstCheck = true;
                    //gpsSetting(ShcsAppIntro.this);
                }
            }

            public void onProviderEnabled(String provider) {

                useGps = true;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

/*
    public static boolean isReceivedMappingContents(String mappingId) {

        boolean isMappingedOk = false;
        int i = 0;

        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis() / 1000;

        Log.d("zzz", "MyApplication.currentTime : " + currentTime);
        Log.d("zzz", "MyApplication.mappingId : " + mappingId);

        for (i = 0; i < Constants.commonVOList.size(); i++) {

            if (mappingId.equals(Constants.commonVOList.get(i).maappingID)) {
                isMappingedOk = true;
                break;
            }
            isMappingedOk = false;
        }

        if (isMappingedOk) {

            if( Constants.commonVOList.get(i).mappingTime + Constants.delayTime <  currentTime){

                Constants.commonVOList.remove(i);

                CommonVO vo = new CommonVO();

                vo.mappingTime = currentTime;
                vo.maappingID = mappingId;

                Constants.commonVOList.add(vo);

                return true;

            } else {

                return false;
            }

        } else {
            CommonVO vo = new CommonVO();

            vo.mappingTime = currentTime;
            vo.maappingID = mappingId;
            Constants.commonVOList.add(vo);

            return true;
        }
    }*/

}
