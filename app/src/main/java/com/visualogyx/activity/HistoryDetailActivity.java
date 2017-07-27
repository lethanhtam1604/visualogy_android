package com.visualogyx.activity;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.testfairy.TestFairy;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.history.HistorySuccessResponseModel;
import com.visualogyx.manager.GoogleMapManager;
import com.visualogyx.manager.ViewExtras;

public class HistoryDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTV, dateTV, idTV, timeTV, contentTV;
    private ScrollView mainScrollView;

    private MapFragment mapFragment;
    private GoogleMapManager googleMapManager;
    private HistorySuccessResponseModel historySuccessResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        historySuccessResponseModel = (HistorySuccessResponseModel) getIntent().getSerializableExtra("HistorySuccessResponseModel");

        createActionBar();
        loadData();

        String[] locationList = historySuccessResponseModel.location.split(",");

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        if (locationList.length > 1)
            googleMapManager = new GoogleMapManager(this, mapFragment, new LatLng(Double.parseDouble(locationList[0]), Double.parseDouble(locationList[1])));
        else
            googleMapManager = new GoogleMapManager(this, mapFragment, null);

        googleMapManager.checkInHistoryDetail = true;

        mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    case MotionEvent.ACTION_UP:
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        });
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_history_detail);
        setSupportActionBar(toolbar);

        TextView titleBar = (TextView) toolbar.findViewById(R.id.title_text);
        titleBar.setText("Details");
        titleBar.setTypeface(null, Typeface.BOLD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadData() {
        imageView = (ImageView) findViewById(R.id.imageView);
        titleTV = (TextView) findViewById(R.id.titleTV);
        dateTV = (TextView) findViewById(R.id.dateTV);
        idTV = (TextView) findViewById(R.id.idTV);
        timeTV = (TextView) findViewById(R.id.timeTV);
        contentTV = (TextView) findViewById(R.id.contentTV);

        final RelativeLayout progressBarRL = (RelativeLayout) findViewById(R.id.progressBarRL);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        progressBarRL.setVisibility(View.VISIBLE);
        if (historySuccessResponseModel.proc_image_data.compareTo("") == 0) {
            imageView.setImageDrawable(ViewExtras.getDrawable(this, R.drawable.square));
        } else {
            Glide.with(this)
                    .load(historySuccessResponseModel.proc_image_data)
                    .asBitmap()
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            progressBarRL.setVisibility(View.GONE);
                            imageView.setImageBitmap(resource);
                            imageView.requestLayout();

                        }
                    });

        }

        contentTV.setText(historySuccessResponseModel.description);
        titleTV.setText(historySuccessResponseModel.name);
        dateTV.setText(Utils.getDate(historySuccessResponseModel.start_date));
        timeTV.setText(Utils.getTime(historySuccessResponseModel.start_date));
        if (historySuccessResponseModel.count == null) {
            idTV.setText("0 Pipe");
        } else {
            if (historySuccessResponseModel.count <= 1)
                idTV.setText(historySuccessResponseModel.count + " Pipe");
            else
                idTV.setText(String.valueOf(historySuccessResponseModel.count + " Pipes"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
