package creativeeconomy.beacon.com.creativeeconomy_app.jm;

import android.app.Activity;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TabHost;

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

import creativeeconomy.beacon.com.creativeeconomy_app.R;
import creativeeconomy.beacon.com.creativeeconomy_app.common.BackgroundService;
import creativeeconomy.beacon.com.creativeeconomy_app.common.CommonVO;
import creativeeconomy.beacon.com.creativeeconomy_app.common.MappingCheckClass;
import creativeeconomy.beacon.com.creativeeconomy_app.main.Constants;
import creativeeconomy.beacon.com.creativeeconomy_app.MyApplication;
import creativeeconomy.beacon.com.creativeeconomy_app.tamra.PageOpen;

import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lat;
import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lon;

/**
 * Created by TJ on 2017-02-22.
 */

public class JM_Tab extends TabActivity implements TabHost.OnTabChangeListener {

    public static TabHost tabHost;

    Activity act;


    DialogInterface mPopupDlg = null;
    AlertDialog.Builder alertbox;
    MappingCheckClass check;

    SharedPreferences pref;
    public SharedPreferences.Editor editor = null;

    String contentsNo;
    String titleNm="";
    String fileUrl="";
    CommonVO commonVO = new CommonVO();

    String localIp = commonVO.localIp;
    String serverIp = commonVO.serverIp;

    ImageView selectedIv, unselectedIv;
    int[] menu_off = {R.drawable.jm_bottom_menu01, R.drawable.jm_bottom_menu02, R.drawable.jm_bottom_menu03, R.drawable.jm_bottom_menu04};
    int[] menu_on = {R.drawable.ce_bottom_click_menu01, R.drawable.jm_bottom_click_menu02, R.drawable.jm_bottom_click_menu03, R.drawable.ce_bottom_click_menu04};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jm_bottom);

        Resources res = getResources(); //리소스 객체 생성
        tabHost = getTabHost(); //탭을 붙이기위한 탭호스객체선언
        TabHost.TabSpec spec; //탭호스트에 붙일 각각의 탭스펙을 선언 ; 각 탭의 메뉴와 컨텐츠를 위한 객체
        Intent intent; //각탭에서 사용할 인텐트 선언

        //인텐트 생성
        intent = new Intent().setClass(this, JM_IntrdcMain.class);
        //각 탭의 메뉴와 컨텐츠를 위한 객체 생성
        spec = tabHost.newTabSpec("0").setIndicator("",getResources().getDrawable(R.drawable.ce_bottom_click_menu01)).setContent(intent);
        tabHost.addTab(spec);


        //인텐트 생성
        intent = new Intent().setClass(this, JM_Dulle.class);
        //각 탭의 메뉴와 컨텐츠를 위한 객체 생성
        spec = tabHost.newTabSpec("1").setIndicator("",getResources().getDrawable(R.drawable.jm_bottom_menu02)).setContent(intent);
        tabHost.addTab(spec);


        //인텐트 생성
        intent = new Intent().setClass(this, JM_Attraction.class);
        //각 탭의 메뉴와 컨텐츠를 위한 객체 생성
        spec = tabHost.newTabSpec("2").setIndicator("",getResources().getDrawable(R.drawable.jm_bottom_menu03)).setContent(intent);
        tabHost.addTab(spec);

        //인텐트 생성
        intent = new Intent().setClass(this, JM_Map.class);
        //각 탭의 메뉴와 컨텐츠를 위한 객체 생성
        spec = tabHost.newTabSpec("3").setIndicator("",getResources().getDrawable(R.drawable.jm_bottom_menu04)).setContent(intent);
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.color.colorBlack);
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.color.colorBlack);
        tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.color.colorBlack);
        tabHost.addTab(spec);
        tabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.color.colorBlack);

        tabHost.setCurrentTab(0); //현재화면에 보여질 탭의 위치를 결정
        tabHost.setOnTabChangedListener( this );

        //----------------------------------tabHost 끝-------------------------

        setResources();
    } // end of onCreate

    @Override
    public void onTabChanged(String tabId) {

        tabHost.getTabWidget().getChildAt(Integer.parseInt(tabId)).setBackgroundColor(Color.parseColor("#7392B5"));

        for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            unselectedIv = (ImageView)tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.icon);
            unselectedIv.setImageDrawable(getResources().getDrawable(menu_off[i]));
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.colorBlack);
        }
        selectedIv = (ImageView)tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).findViewById(android.R.id.icon);
        selectedIv.setImageDrawable(getResources().getDrawable(menu_on[tabHost.getCurrentTab()]));

        if (Integer.parseInt(tabId) ==3) {
            Intent intent = new Intent(JM_Tab.this, JM_Map.class);
            startActivity(intent);
            tabHost.setCurrentTab(0); //현재화면에 보여질 탭의 위치를 결정
        }
    }

    public void setResources() {

        act = this;

        Constants.btAdapter = BluetoothAdapter.getDefaultAdapter();     //블루투스 어댑터

        pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Constants.pushState = pref.getString("push", "");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode != KeyEvent.KEYCODE_BACK) return false;

        //세션 죽이기
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        MyApplication.appRun = "1";

        Log.d("Jinwoo", "JM_Tap => onDestroy 호출");

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);

        stopService(intent);
        StopMonitoring();

        super.onDestroy();
    }

    @Override
    protected void onPause() {

        Log.d("Jinwoo", "JM_Tap onPause 호출");

        StopMonitoring();

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);
        intent.putExtra("PageNm", "CE_MAIN");
        startService(intent);
        Log.d("Jinwoo", "JM_Tap startService111111111111");

        super.onPause();
    }

    @Override
    protected void onResume() {
        MyApplication.appRun = "1";

        Log.d("Jinwoo", "JM_Tap onResume 호출");

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);

        stopService(intent);

        if ("1".equals(Constants.bluetoothState))
            StartMonitoring();
        else if ("0".equals(Constants.bluetoothState))
            StopMonitoring();

        super.onResume();
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

            Log.d("Jinwoo", "JM_Tap ----didEnter ----" + region.name() + "에 진입");
        }

        @Override
        public void didExit(Region region) {

            Log.d("Jinwoo", "JM_Tap ----didExit ----" + region.name() + "에서 이탈");
        }

        @Override
        public void ranged(NearbySpots nearbySpots) {
            final Set<Spot> droppedSet = new HashSet<>();

            Log.d("Jinwoo", "JM_Tap ranged");
            droppedSet.addAll(Constants.spotSet);
            nearbySpots.orderBy(Spot.ACCURACY_ORDER).foreach(new Consumer<Spot>() {
                @Override
                public void accept(Spot spot) {
                    droppedSet.remove(spot);

                    //if (Constants.spotSet.contains(spot)) return;

                    Constants.spotSet.add(spot);
                    Log.d("Jinwoo", "JM_Tap raged Start()");
                    Log.d("Jinwoo", "----JM_Tap spotSet----   근처에" + spot.description() + "발견");
                    Log.d("Jinwoo", "---- JM_Tap spot.id() ----" + spot.id());
                    if (check != null) check = null;

                    check = new MappingCheckClass();
                    if (check.isReceivedMappingContents(String.valueOf(spot.id()))) {
                        // 비콘 id(공항의 37번 비콘)가 37이면 창경으로 찾아가기 맵실행
                        if (spot.id()==37){
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp=" + lat + "," + lon + "&ep=33.5003147,126.5300349&by=CAR"));
                                startActivity(intent);
                                //CE_Tab.tabHost.setCurrentTab(0);
                            } catch (Exception e) {
                                Log.d("CE", "ㅇㅇㅇㅇㅇㅇㅇㅇㅇ" + e);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=net.daum.android.map"));
                                startActivity(intent);
                            }
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

        Log.d("Jinwoo", "JM_Tap => 감지된 spotId ::::: " + spotId);

        // 비콘 id가 0이 아닐경우 진입
        if(spotId != 0) {
            //비콘 id가 0이 아니면서 매핑된 컨텐츠가 있을 경우 진입

            String url="";

            Log.d("Jinwoo", "JM_Tap => onBeaconId   " + spotId);
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

            Log.d("Jinwoo", "JM_Tap => uri : " + uri);
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                    Log.d("Jinwoo", "JM_Tap => json   " + json);
                }

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("Jinwoo",  "JM_Tap => result : " + result);

            JSONObject jsonRes = null;

            if(result == null) {
                Log.d("Jinwoo",  "JM_Tap => result : null");

            } else {

                try{

                    jsonRes = new JSONObject(result);

                    Log.d("Jinwoo",  "JM_Tap => result : " + result);
                    contentsNo = jsonRes.getString("contentsNo");
                    fileUrl = jsonRes.getString("filePath");
                    titleNm = jsonRes.getString("contentsTitle");
                    Log.d("Jinwoo", "JM_Tap => fileUrl : " + fileUrl);

                    if(!"FALSE".equals(contentsNo))
                        showList(fileUrl, titleNm);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showList(final String fileUrl, final String titleNm) {

        Log.d("Jinwoo", "showList() Start;");

        if(alertbox != null) {
            mPopupDlg.dismiss();
            alertbox = null;
            mPopupDlg = null;
        }

        alertbox =  new AlertDialog.Builder(JM_Tab.this);

        alertbox.setTitle("비콘 신호 감지");
        alertbox.setMessage("페이지를 여시겠습니까?");
        alertbox.setIcon(R.drawable.ic_menu_gallery);
        alertbox.setCancelable(false); // back 버튼 막음
        alertbox.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(JM_Tab.this, PageOpen.class);
//                    intent.putExtra("value", "http://192.168.17.7:8080/creativeEconomy/MobileCommonPage.do?contentsNo="+contentsNo);
                intent.putExtra("value", fileUrl);
                intent.putExtra("PageNm", "JM_Tab");
                intent.putExtra("titleNm", titleNm);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        alertbox.setNegativeButton("아니오", null);
        mPopupDlg = alertbox.show();

    }

}
