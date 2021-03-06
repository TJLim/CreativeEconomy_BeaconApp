package creativeeconomy.beacon.com.creativeeconomy_app.ce;

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

//소개탭
public class CE_Greetings extends Fragment {

    WebView ceWebView1;
    CommonVO commonVO = new CommonVO();

    String localIp = commonVO.localIp;
    String serverIp = commonVO.serverIp;

    String myBlogAddr = serverIp + "/creativeEconomy/CreativeGreetings.do";

    String myUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ce_greetings, container, false);
        ceWebView1 = (WebView)view.findViewById(R.id.cewebview1);

        WebSettings setting = ceWebView1.getSettings();

        setting.setJavaScriptEnabled(true);
        ceWebView1.setWebViewClient(new MyWebViewClient());

        if(myUrl == null){
            myUrl = myBlogAddr;
        }
        ceWebView1.loadUrl(myUrl);

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