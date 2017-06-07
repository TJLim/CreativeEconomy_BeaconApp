package creativeeconomy.beacon.com.creativeeconomy_app.tamra;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import creativeeconomy.beacon.com.creativeeconomy_app.MyApplication;
import creativeeconomy.beacon.com.creativeeconomy_app.R;
import creativeeconomy.beacon.com.creativeeconomy_app.ce.CE_IntrdcMain;
import creativeeconomy.beacon.com.creativeeconomy_app.ce.CE_Tab;
import creativeeconomy.beacon.com.creativeeconomy_app.common.BackgroundService;
import creativeeconomy.beacon.com.creativeeconomy_app.jm.JM_IntrdcMain;
import creativeeconomy.beacon.com.creativeeconomy_app.jm.JM_Tab;
import creativeeconomy.beacon.com.creativeeconomy_app.main.MainActivity;

/**
 * Created by ggg on 2016-12-18.
 */

public class PageOpen extends AppCompatActivity {

    WebView pageWebView;

    String myUrl;
    String PageNm;
    String getTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_open);
        Log.d("zz","   pageOpen() onCreate Start();;;;;;;;;;;;;;   ");
        ActionBar actionBar = getSupportActionBar();

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Intent intent = getIntent();

        Log.d("zz","1111111111111111111");
        String myAddr = intent.getStringExtra("value");
        getTitle = intent.getStringExtra("titleNm");
        Log.d("zz","222222222222222222222222");
        setTitle(getTitle);
        PageNm = intent.getStringExtra("PageNm");
        Log.d("zz","   myAddr   " + myAddr);
        Log.d("zz","   pageOpen => pageNm                       :::::::::::::   " + PageNm);

        pageWebView = (WebView)findViewById(R.id.pageWebview);

        WebSettings setting = pageWebView.getSettings();

        setting.setJavaScriptEnabled(true);
        pageWebView.setWebViewClient(new MyWebViewClient());

        if(myUrl == null){
            myUrl = myAddr;
        }
        pageWebView.loadUrl(myUrl);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            myUrl = url;
            view.loadUrl(url);
            return true;
        }
    }


    @Override
    protected void onResume() {

        Log.d("PageOpen","PageOpen => onResume 시작");

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);

        stopService(intent);
        Log.d("PageOpen","PageOpen => onResume 끝");
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent;

                if("Main".equals(PageNm)) {
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else if("CE_MAIN".equals(PageNm)) {
                    intent = new Intent(this, CE_IntrdcMain.class);
                    startActivity(intent);
                } else if("JM_MAIN".equals(PageNm)) {
                    intent = new Intent(this, JM_IntrdcMain.class);
                    startActivity(intent);
                } else if("service".equals(PageNm)) {
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else if("CE_Tab".equals(PageNm)) {
                    intent = new Intent(this, CE_Tab.class);
                    startActivity(intent);
                } else if("JM_Tab".equals(PageNm)) {
                    intent = new Intent(this, JM_Tab.class);
                    startActivity(intent);
                }

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode != KeyEvent.KEYCODE_BACK) return false;

        if(pageWebView.canGoBack()) {
            pageWebView.goBack();
            return true;
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        Intent intent;

        if("Main".equals(PageNm)) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if("CE_MAIN".equals(PageNm)) {
            intent = new Intent(this, CE_IntrdcMain.class);
            startActivity(intent);
        } else if("JM_MAIN".equals(PageNm)) {
            intent = new Intent(this, JM_IntrdcMain.class);
            startActivity(intent);
        } else if("service".equals(PageNm)) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if("CE_Tab".equals(PageNm)) {
            intent = new Intent(this, CE_Tab.class);
            startActivity(intent);
        } else if("JM_Tab".equals(PageNm)) {
            intent = new Intent(this, JM_Tab.class);
            startActivity(intent);
        }

        finish();

        return true;
    }

    @Override
    protected void onDestroy() {
        MyApplication.appRun = "0";

        Log.d("Jinwoo", "PageOpen => onDestroy 호출");

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);

        stopService(intent);

        super.onDestroy();
    }

    @Override
    protected void onPause() {

        Log.d("Jinwoo", "PageOpen => onPause 호출");

        /*final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);
        intent.putExtra("PageNm", "JM_MAIN");
        startService(intent);
        Log.d("Jinwoo", "PageOpen => startService111111111111");*/

        super.onPause();
    }
}
