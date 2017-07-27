package com.visualogyx.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.testfairy.TestFairy;
import com.visualogyx.R;

import io.fabric.sdk.android.Fabric;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_forgot_password);

        createActionBar();
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_forgot_password);
        setSupportActionBar(toolbar);

        TextView titleBar = (TextView) toolbar.findViewById(R.id.title_text);
        titleBar.setText("Forgot Password");
        titleBar.setTypeface(null, Typeface.BOLD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void newUserClicked(View view) {
        this.finish();
    }

    public void sendANewPasswordBtnClicked(View view) {
        this.finish();
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
