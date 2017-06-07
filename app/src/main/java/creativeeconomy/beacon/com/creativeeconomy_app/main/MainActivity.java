package creativeeconomy.beacon.com.creativeeconomy_app.main;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

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
import creativeeconomy.beacon.com.creativeeconomy_app.ce.CE_Tab;
import creativeeconomy.beacon.com.creativeeconomy_app.common.BackgroundService;
import creativeeconomy.beacon.com.creativeeconomy_app.common.CommonVO;
import creativeeconomy.beacon.com.creativeeconomy_app.common.MappingCheckClass;
import creativeeconomy.beacon.com.creativeeconomy_app.jm.JM_Tab;
import creativeeconomy.beacon.com.creativeeconomy_app.tamra.PageOpen;

import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lat;
import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lon;


/**
 * Created by chihong on 2016-12-08.
 */

public class MainActivity extends AppCompatActivity {
    //commit test
    Activity act;

    DialogInterface mPopupDlg = null;
    AlertDialog.Builder alertbox, main_alertbox, networkCheckDialog;
    MappingCheckClass check;

    private long pressBackTimeInMillis = 0;

    int MAX_PAGE = 1;
    Fragment cur_fragment = new Fragment();
    ViewPager viewPager;

    Switch sw1, sw2;
    SharedPreferences pref;
    public SharedPreferences.Editor editor = null;
    BluetoothAdapter adapter;
    String contentsNo;
    String fileUrl="";
    String titleNm="";

    Intent intent;

    CommonVO commonVO = new CommonVO();
    String localIp = commonVO.localIp;
    String serverIp = commonVO.serverIp;

    private boolean m_bFlag = false;
    private Handler m_hHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setResources();

        init();
        intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);

        //네트워크 연결이 안됐을 시, 설정 다이얼로그 띄움
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isConnected() || mobile.isConnected()) {   // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
            Log.d("Jinwoo" , "연결이 되었습니다.");

        } else {
            networkCheckDialog = new AlertDialog.Builder(MainActivity.this);
            networkCheckDialog.setTitle(Html.fromHtml("<font color='#6D5652'>연결할 수 없음</font>"));
            networkCheckDialog.setMessage("네트워크 연결이 되어 있지 않습니다. \n네트워크를 연결하시겠습니까?");
            networkCheckDialog.setCancelable(false);
            networkCheckDialog.setNegativeButton("닫기", null);
            networkCheckDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            }).create().show();
        }

        m_hHandler = new Handler() {    //백키 핸들러
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    m_bFlag = false;
                }
            }
        };
    }

    /*플로팅 버튼 클릭 리스너*/
    ImageView.OnClickListener FabClickListener = new ImageView.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {

                case R.id.fab:

                    final LinearLayout linear = (LinearLayout) View.inflate(MainActivity.this, R.layout.settings_detail, null);     //다이얼로그를 xml로 커스텀함.

                    main_alertbox = new AlertDialog.Builder(MainActivity.this);
                    main_alertbox.setCancelable(false);
                    //main_alertbox.setIcon(R.drawable.icon_24_2);
                    main_alertbox.setTitle(Html.fromHtml("<font color='#6D5652'>비콘 신호 수신 설정</font>"));
                    main_alertbox.setView(linear);      //커스텀한 xml을 set
                    main_alertbox.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    });

                    sw1 = (Switch) linear.findViewById(R.id.bt_switch);
                    sw2 = (Switch) linear.findViewById(R.id.ps_switch);

                    blueToothStateCheck();
                    pushStateCheck();

                    //블루투스 스위치 on / off change
                    sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked == true) {
                                adapter.enable();     // Bluetooth On
                                Constants.bluetoothState = "1";

                                Toast.makeText(MainActivity.this, "블루투스 켜짐", Toast.LENGTH_SHORT).show();
                                Log.d("Jinwoo", "isChecked == true");

                            } else {
                                adapter.disable();   // Bluetooth Off
                                Constants.bluetoothState = "0";

                                Toast.makeText(MainActivity.this, "블루투스 꺼짐", Toast.LENGTH_SHORT).show();
                                Log.d("Jinwoo", "isChecked == false");
                            }
                        }
                    });

                    //푸시 스위치 on / off change
                    sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked == true) {
                                editor.putString("push", "1");
                                editor.commit();
                                Constants.pushState = "1";
                                Toast.makeText(MainActivity.this, "푸시 켜짐", Toast.LENGTH_SHORT).show();
                                Log.d("Jinwoo", "isChecked == true");
                            } else { // OFF
                                editor.putString("push", "0");
                                editor.commit();
                                Constants.pushState = "0";
                                Toast.makeText(MainActivity.this, "푸시 꺼짐", Toast.LENGTH_SHORT).show();
                                Log.d("Jinwoo", "isChecked == false");
                            }
                        }
                    });

                    AlertDialog alertDialog = main_alertbox.create();

                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);       //다이얼로그 뜰 때, 뒷 배경 흐리게
                    alertDialog.setCanceledOnTouchOutside(false);       //다이얼로그 외부 터치 막음
                    alertDialog.show();

                    //블루투스 리시
                    IntentFilter btFilter = new IntentFilter();
                    btFilter.addAction(Constants.btAdapter.ACTION_STATE_CHANGED);
                    Constants.btOnReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Constants.btIsReceiverRegistered = true;
                            //unregisterReceiver(scrOffReceiver);
                            blueToothStateCheck();
                        }
                    };
                    registerReceiver(Constants.btOnReceiver, btFilter);

                 break;
            }
        }
    };

    /* 초기화 함수 */
    public void init() {

        viewPager.setAdapter(new adapter(getSupportFragmentManager()));
        act.findViewById(R.id.fab).setOnClickListener(FabClickListener);

    }

    /* 리소스 세팅 */
    public void setResources() {

        act = this;
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        adapter = BluetoothAdapter.getDefaultAdapter();     //블루투스 어댑터
        pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        Constants.pushState = pref.getString("push", "");

    }

    /* 블루투스 상태 체크하여 스위치 on/off 시킴 */
    public void blueToothStateCheck() {

        if(adapter.isEnabled()) // 블루투스가 켜져있을 경우 스위치 on
            sw1.setChecked(true);
        else                    // 블루투스가 꺼져있을 경우 스위치 off
            sw1.setChecked(false);
    }

    /* 푸시알림 상태 체크하여 스위치 on/off 시킴 */
    public void pushStateCheck() {

        if("0".equals(Constants.pushState))
            sw2.setChecked(false);
        else if("1".equals(Constants.pushState))
            sw2.setChecked(true);
    }

    public class adapter extends FragmentPagerAdapter {
        public adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position<0 || MAX_PAGE<=position)
                return null;
            switch (position){
                case 0:
                    cur_fragment = new MainTab1();
                    Log.d("Jinwoo","   MainTab1   " + position);
                    break;

            }
            return cur_fragment;
        }
        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }

    /*창경 go 버튼 이벤트*/
    public void txtClick(View v) {
        switch (v.getId()) {
            case R.id.go_ce:
                Intent intent = new Intent(MainActivity.this, CE_Tab.class);
                Log.d("Jinwoo","   창경   ");
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.go_ce2:
                Intent intent2 = new Intent(MainActivity.this, CE_Tab.class);
                Log.d("Jinwoo","   창경   ");
                startActivity(intent2);
                overridePendingTransition(0, 0);
                break;

            case R.id.go_jm:
                Intent intent3 = new Intent(MainActivity.this, JM_Tab.class);
                Log.d("Jinwoo","   중문   ");
                startActivity(intent3);
                overridePendingTransition(0, 0);
                break;

            case R.id.go_jm2:
                Intent intent4 = new Intent(MainActivity.this, JM_Tab.class);
                Log.d("Jinwoo","   중문   ");
                startActivity(intent4);
                overridePendingTransition(0, 0);
                break;
        }
    }

    public void  BeaconScanning(long spotId) {

        Log.d("Jinwoo", "MainActivity => 감지된 spotId ::::: " + spotId);

        // 비콘 id가 0이 아닐경우 진입
        if(spotId != 0) {
            //비콘 id가 0이 아니면서 매핑된 컨텐츠가 있을 경우 진입

            String url="";

            Log.d("Jinwoo", "MainActivity => onBeaconId   " + spotId);

            url = serverIp + "/creativeEconomy/UserMappingInfoData.do?beaconId=" + spotId + "&userKey=ietrceea";
            GetDataJSON g = new GetDataJSON();
            g.execute(url);
        }
    }

    private class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String uri = params[0];

            Log.d("Jinwoo", "MainActivity => uri : " + uri);
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                    Log.d("Jinwoo", "MainActivity => json   " + json);
                }

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("Jinwoo",  "MainActivity => result : " + result);

            JSONObject jsonRes = null;

            if(result == null) {
                Log.d("Jinwoo",  "MainActivity => result : null");

            } else {

                try{

                    jsonRes = new JSONObject(result);

                    Log.d("Jinwoo",  "MainActivity => result : " + result);
                    fileUrl = jsonRes.getString("filePath");        //매핑된 컨텐츠의 경로
                    contentsNo = jsonRes.getString("contentsNo");   //매핑된 컨텐츠의 번호
                    titleNm = jsonRes.getString("contentsTitle");   //매핑된 컨텐츠의 제목
                    Log.d("Jinwoo", "MainActivity => fileUrl : " + fileUrl);

                    if(!"FALSE".equals(contentsNo)) //매핑된 컨텐츠가 있다면 다이얼로그 띄움
                        showList(fileUrl, titleNm);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showList(final String fileUrl, final String titleNm) {

        Log.d("Jinwoo", "showList() Start;");

        if(alertbox != null) {      //다이얼로그가 뜬 상태이면 이전에 다이얼로그 없앰
            mPopupDlg.dismiss();
            alertbox = null;
            mPopupDlg = null;
        }

        alertbox =  new AlertDialog.Builder(MainActivity.this);

        alertbox.setTitle("비콘 신호 감지");
        alertbox.setMessage("페이지를 여시겠습니까?");
        alertbox.setIcon(R.drawable.ic_menu_gallery);
        alertbox.setCancelable(false); // back 버튼 막음
        alertbox.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(MainActivity.this, PageOpen.class);
//                    intent.putExtra("value", "http://192.168.17.7:8080/creativeEconomy/MobileCommonPage.do?contentsNo="+contentsNo);
                intent.putExtra("value", fileUrl);
                intent.putExtra("PageNm", "Main");
                intent.putExtra("titleNm", titleNm);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        alertbox.setNegativeButton("아니오", null);
        mPopupDlg = alertbox.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if (!m_bFlag) {
                Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한 번 더 누르시면 종료합니다.", Toast.LENGTH_SHORT).show();
                m_bFlag = true;
                m_hHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            }
            else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        MyApplication.appRun = "0";

        Log.d("Beacon_ranged","MainActivity => onDestroy 호출");

        stopService(intent);
        StopMonitoring();

        super.onDestroy();
    }

    @Override
    protected void onPause() {

        Log.d("Beacon_ranged","MainActivity => onPause 호출");

        StopMonitoring();
        intent.putExtra("PageNm", "MAIN");
        startService(intent);
        Log.d("zz","MainActivity => startService111111111111");

        super.onPause();
    }

    @Override
    protected void onResume() {

        Log.d("Jinwoo","MainActivity => onResume 호출");
        MyApplication.appRun = "1";

        stopService(intent);

        if("1".equals(Constants.bluetoothState))
            StartMonitoring();
        else if("0".equals(Constants.bluetoothState))
            StopMonitoring();

        super.onResume();
    }

    @Override
    protected void onRestart() {

        Log.d("Jinwoo","MainActivity => onRestart())))))))))))))))))))))))))))))");

        super.onRestart();
    }

    private final TamraObserver tamraObserver = new TamraObserver() {
        @Override
        public void didEnter(Region region) {

            Log.d("Jinwoo","MainActivity => ----didEnter ----"+region.name()+"에 진입");
        }

        @Override
        public void didExit(Region region) {

            Log.d("Jinwoo","MainActivity => ----didExit ----"+region.name()+"에서 이탈");
        }

        @Override
        public void ranged(NearbySpots nearbySpots) {
            final Set<Spot> droppedSet = new HashSet<>();

            Log.d("Jinwoo", "MainActivity => ranged");
            droppedSet.addAll(Constants.spotSet);
            nearbySpots.orderBy(Spot.ACCURACY_ORDER).foreach(new Consumer<Spot>() {
                @Override
                public void accept(Spot spot) {
                droppedSet.remove(spot);

                //if (Constants.spotSet.contains(spot)) return;

                Constants.spotSet.add(spot);
                Log.d("Jinwoo", "MainActivity => raged Start()");
                Log.d("Jinwoo","----MainActivity() spotSet----   근처에" + spot.description()+"발견");
                Log.d("Jinwoo","---- MainActivity() spot.id() ----" + spot.id());

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

    public void StartMonitoring() {
        Tamra.addObserver(tamraObserver);
    }

    public void StopMonitoring() {
        Tamra.removeObserver(tamraObserver);
    }

}
