package creativeeconomy.beacon.com.creativeeconomy_app.ce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import creativeeconomy.beacon.com.creativeeconomy_app.R;
import creativeeconomy.beacon.com.creativeeconomy_app.common.CommonVO;

//비전 및 전략 탭
public class CE_Intrdc extends Fragment {
    WebView ceWebView2;
    CommonVO commonVO = new CommonVO();

    String localIp = commonVO.localIp;
    String serverIp = commonVO.serverIp;

    String myBlogAddr = serverIp + "/creativeEconomy/CreativeIntrdc.do";
    String myUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ce_arttoy, container, false);
        ceWebView2 = (WebView) view.findViewById(R.id.cewebview2);

        WebSettings setting = ceWebView2.getSettings();

        setting.setJavaScriptEnabled(true);
        ceWebView2.setWebViewClient(new MyWebViewClient());

        if (myUrl == null) {
            myUrl = myBlogAddr;
        }
        ceWebView2.loadUrl(myUrl);

        return view;

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String overrideUrl) {

            view.stopLoading();

            Log.d("webview", overrideUrl);

            if (overrideUrl.contains("close")) {

                getActivity().finish();

            } else {
                if (overrideUrl.startsWith("http:") || overrideUrl.startsWith("https:")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(overrideUrl));
                    startActivity(i);
                    return true;
                } else {
                    view.loadUrl(overrideUrl);
                }
            }

            return false;

        }

    }
}
