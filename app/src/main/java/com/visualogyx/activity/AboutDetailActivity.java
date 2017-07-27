package com.visualogyx.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.testfairy.TestFairy;
import com.visualogyx.R;

public class AboutDetailActivity extends AppCompatActivity {

    private String aboutType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        } else {
            aboutType = extras.getString("AboutType");
        }

        createActionBar();

        WebView wv = (WebView) findViewById(R.id.webView);
        if (aboutType.compareTo("Term") == 0)
            wv.loadUrl("file:///android_asset/terms.html");
        else
            wv.loadUrl("file:///android_asset/eula.html");
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about_detail);
        setSupportActionBar(toolbar);

        TextView titleBar = (TextView) toolbar.findViewById(R.id.title_text);
        if (aboutType.compareTo("Term") == 0)
            titleBar.setText("Term And Conditions");
        else
            titleBar.setText("License");
        titleBar.setTypeface(null, Typeface.BOLD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
