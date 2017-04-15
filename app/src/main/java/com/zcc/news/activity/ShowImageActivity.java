package com.zcc.news.activity;

import android.app.Activity;

import android.os.Bundle;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.zcc.news.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowImageActivity extends Activity {
    private String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        imageUrl =getIntent().getStringExtra("imageUrl");
        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        Picasso.with(this)
                .load(imageUrl)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}
