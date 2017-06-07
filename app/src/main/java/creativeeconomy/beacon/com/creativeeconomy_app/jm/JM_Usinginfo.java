package creativeeconomy.beacon.com.creativeeconomy_app.jm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import creativeeconomy.beacon.com.creativeeconomy_app.R;
import creativeeconomy.beacon.com.creativeeconomy_app.common.CommonVO;

public class JM_Usinginfo extends Fragment {

    WebView jmWebView1;
    CommonVO commonVO = new CommonVO();

    String localIp = commonVO.localIp;
    String serverIp = commonVO.serverIp;

    String myBlogAddr = serverIp + "/creativeEconomy/JungmunUsinginfo.do";
    String myUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jm_usinginfo, container, false);
        jmWebView1 = (WebView)view.findViewById(R.id.jmwebview1);

        WebSettings setting = jmWebView1.getSettings();

        setting.setJavaScriptEnabled(true);
        jmWebView1.setWebViewClient(new MyWebViewClient());

        if(myUrl == null){
            myUrl = myBlogAddr;
        }
        jmWebView1.loadUrl(myUrl);

        return view;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

}