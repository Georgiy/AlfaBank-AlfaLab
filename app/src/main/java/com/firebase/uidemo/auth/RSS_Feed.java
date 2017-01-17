package com.firebase.uidemo.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.uidemo.R;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class RSS_Feed extends AppCompatActivity {

    private static final String TOAST_TEXT = "No connection have been detected. "
            + "\nPlease, check your Wi-Fi or 3G !!!!";

    private WebView webview;

    public static Activity RSS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RSS = this;
        setContentView(R.layout.rss);


        webview = (WebView) findViewById(R.id.webView);
        webview.setWebViewClient(new Callback());

        Context context = this;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected() ){

            webview.getSettings().setJavaScriptEnabled(true);
          //  webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.loadUrl("https://alfabank.ru/_/rss/_rss.html?subtype=1&category=2&city=21");

        } else {
            webview.loadUrl("file:///android_asset/NoConnection.html");
            ToastToast();
        }

        final Button menu = (Button) findViewById(R.id.menu);
        menu.getBackground().setColorFilter(0x5F039BE5, PorterDuff.Mode.MULTIPLY);
        menu.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RSS_Feed.this, SignedInActivity.class);
                RSS_Feed.this.startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0, 0);
            }

        });
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack())
            webview.goBack();
        else  moveTaskToBack(true);

    }

    private void ToastToast() {
          Toast.makeText(getApplicationContext(), TOAST_TEXT, Toast.LENGTH_LONG).show();
     }

    private class Callback extends WebViewClient {  //HERE IS THE MAIN CHANGE.
        @Override
        public void onPageFinished(WebView view, String url)
        {

        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
            return (false);
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(RSS_Feed.this,R.style.PopUpTheme);
            builder.setMessage(R.string.notification_error_ssl_cert_invalid);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                Intent intent = new Intent(RSS_Feed.this, SignedInActivity.class);
                RSS_Feed.this.startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0, 0);
                return true;
        }
        return super.onKeyDown(keycode, e);
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, RSS_Feed.class);
        return in;
    }
}
