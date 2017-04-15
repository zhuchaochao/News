package com.zcc.news.activity;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zcc.news.R;

public class NewsDetailActivity extends Activity implements View.OnClickListener {
    private TextView tvTitle;
    private ImageButton ibMenu;
    private ImageButton ibback;
    private ImageButton ibTextsize;
    private ImageButton ibShare;
    private WebView webview;
    private ProgressBar pbLoading;
    private String url;
    private WebSettings webSettings;


    private void findViews() {
        tvTitle = (TextView)findViewById( R.id.tv_title );
        ibMenu = (ImageButton)findViewById( R.id.ib_menu );
        ibback = (ImageButton)findViewById( R.id.ib_back );
        ibTextsize = (ImageButton)findViewById( R.id.ib_textsize );
        ibShare = (ImageButton)findViewById( R.id.ib_share );
        webview = (WebView)findViewById( R.id.webview );
        pbLoading = (ProgressBar)findViewById( R.id.pb_loading );

        ibback.setOnClickListener( this );
        ibTextsize.setOnClickListener(this);
        ibShare.setOnClickListener(this);
        pbLoading.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        ibMenu.setVisibility(View.GONE);
        ibback.setVisibility(View.VISIBLE);
        ibTextsize.setVisibility(View.VISIBLE);
        ibShare.setVisibility(View.VISIBLE);


    }

    @Override
    public void onClick(View v) {
        if (v == ibback) {
           finish();
        } else if ( v == ibTextsize ) {
            showChangeTextSizeDialog();
        } else if ( v == ibShare ) {
            Toast.makeText(this,"分享",Toast.LENGTH_SHORT).show();
        }
    }
    private int tempSize = 2;
    private int realSize = tempSize;
    private void showChangeTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置字体大小");
        String[] item = {"超大字体","大字体","正常字体","小字体","超小字体"};

        builder.setSingleChoiceItems(item, realSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempSize = which;
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realSize = tempSize;
                changeTextSize(realSize);
            }
        });
        builder.show();
    }

    private void changeTextSize(int realSize) {
        switch (realSize){
            case 0:
                webSettings.setTextZoom(200);
                break;
            case 1:
                webSettings.setTextZoom(150);
                break;
            case 2:
                webSettings.setTextZoom(100);
                break;
            case 3:
                webSettings.setTextZoom(75);
                break;
            case 4:
                webSettings.setTextZoom(50);
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        findViews();
        load();
    }

    private void load() {
        url = getIntent().getStringExtra("url");
        webSettings = webview.getSettings();
        //设置支持JS
        webSettings.setJavaScriptEnabled(true);
        //双击变大变小
        webSettings.setUseWideViewPort(true);
        //增加缩放按钮
        webSettings.setBuiltInZoomControls(true);
        //不让从当前网页跳转到系统浏览器中
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);
            }
        });
        webview.loadUrl(url);

    }
}
