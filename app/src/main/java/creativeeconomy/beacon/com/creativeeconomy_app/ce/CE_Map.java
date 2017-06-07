package creativeeconomy.beacon.com.creativeeconomy_app.ce;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import creativeeconomy.beacon.com.creativeeconomy_app.R;
import creativeeconomy.beacon.com.creativeeconomy_app.common.BackgroundService;
import creativeeconomy.beacon.com.creativeeconomy_app.common.MappingCheckClass;
import creativeeconomy.beacon.com.creativeeconomy_app.main.Constants;
import creativeeconomy.beacon.com.creativeeconomy_app.MyApplication;

import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lat;
import static creativeeconomy.beacon.com.creativeeconomy_app.main.Constants.lon;

/**
 * Created by TJ on 2017-02-22.
 */

public class CE_Map extends AppCompatActivity {

    Activity act;
    ActionBar actionBar;
    DialogInterface mPopupDlg = null;
    AlertDialog.Builder alertbox, ce_alertbox;
    MappingCheckClass check;

    public static Switch sw1, sw2;
    SharedPreferences pref;
    public SharedPreferences.Editor editor = null;

    boolean mapStart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp=" + lat + "," + lon + "&ep=33.5003147,126.5300349&by=CAR"));
            startActivity(intent);
            CE_Tab.tabHost.setCurrentTab(0);
        } catch (Exception e) {
            Log.d("CE", "ㅇㅇㅇㅇㅇㅇㅇㅇㅇ" + e);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=net.daum.android.map"));
            startActivity(intent);
        }

    }

    /* 초기화 함수 */
    public void init() {

        actionBar = getSupportActionBar();

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.icon_24_1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.right_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            final LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.settings_detail, null);

            ce_alertbox = new AlertDialog.Builder(CE_Map.this);
            ce_alertbox.setCancelable(false);
            //ce_alertbox.setIcon(R.drawable.icon_24_2);
            ce_alertbox.setTitle(Html.fromHtml("<font color='#6D5652'>비콘 신호 수신 설정</font>"));
            ce_alertbox.setView(linear);
            ce_alertbox.setNegativeButton("닫기", null);

            sw1 = (Switch) linear.findViewById(R.id.bt_switch);
            sw2 = (Switch) linear.findViewById(R.id.ps_switch);

            blueToothStateCheck();
            pushStateCheck();

            //스위치1 on / off change
            sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked == true) {
                        Constants.btAdapter.enable();     // Bluetooth On
                        Constants.bluetoothState = "1";

                        Toast.makeText(CE_Map.this, "블루투스 켜짐", Toast.LENGTH_SHORT).show();
                        Log.d("Jinwoo","isChecked == true");

                    } else {
                        Constants.btAdapter.disable();   // Bluetooth Off
                        Constants.bluetoothState = "0";

                        Toast.makeText(CE_Map.this, "블루투스 꺼짐", Toast.LENGTH_SHORT).show();
                        Log.d("Jinwoo","isChecked == false");
                    }
                }
            });

            //스위치2 on / off change
            sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked == true) {

                        editor.putString("push", "1");
                        editor.commit();
                        Constants.pushState = "1";
                        Toast.makeText(CE_Map.this, "푸시 켜짐", Toast.LENGTH_SHORT).show();
                        Log.d("Jinwoo","isChecked == true");
                    } else { // OFF

                        editor.putString("push", "0");
                        editor.commit();
                        Constants.pushState = "0";
                        Toast.makeText(CE_Map.this, "푸시 꺼짐", Toast.LENGTH_SHORT).show();
                        Log.d("Jinwoo","isChecked == false");
                    }
                }
            });

            AlertDialog alertDialog = ce_alertbox.create();
            alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setResources() {

        act = this;

        Constants.btAdapter = BluetoothAdapter.getDefaultAdapter();     //블루투스 어댑터

        pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Constants.pushState = pref.getString("push", "");
    }

    /* 블루투스 상태 체크하여 스위치 on/off 시킴 */
    public void blueToothStateCheck() {

        if(Constants.btAdapter.isEnabled()) // 블루투스가 켜져있을 경우 스위치 on
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode != KeyEvent.KEYCODE_BACK) return false;

        //세션 죽이기
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        Log.d("Jinwoo", "ㅇㅇㅇㅇㅇㅇㅇㅇㅇ");
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        MyApplication.appRun = "0";

        Log.d("Jinwoo", "CE_Map => onDestroy 호출");

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);

        stopService(intent);


        super.onDestroy();
    }

    @Override
    protected void onPause() {

        Log.d("Jinwoo", "CE_Map => onPause 호출");
        mapStart = true;


        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);
        intent.putExtra("PageNm", "CE_MAIN");
        startService(intent);
        Log.d("Jinwoo", "CE_Tab => startService111111111111");

        super.onPause();
    }

    @Override
    protected void onResume() {
        MyApplication.appRun = "1";

        Log.d("Jinwoo", "CE_Map => onResume 호출");

        /*final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);

        stopService(intent);*/

        if (mapStart == true){
            Intent intent = new Intent(CE_Map.this, CE_Tab.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
        }


        super.onResume();
    }

}
