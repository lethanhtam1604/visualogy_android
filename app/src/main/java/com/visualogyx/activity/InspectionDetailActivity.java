package com.visualogyx.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testfairy.TestFairy;
import com.visualogyx.R;

public class InspectionDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTV, dateTV, idTV, timeTV, contentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        createActionBar();
        loadData();
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_inspection_detail);
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

        Picasso.with(this).load("http://taihinhanhdep.xyz/wp-content/uploads/2016/04/album-anh-girl-xinh.jpg").into(imageView);
        contentTV.setText("This EULA grants you the following rights:\u2028a. Per User License. The SOFTWARE PRODUCT permits a single user to use the SOFTWARE PRODUCT under the terms of the license. If the SOFTWARE PRODUCT is installed on a single computer used by multiple users, a customer must purchase additional licenses for each user that accesses the SOFTWARE PRODUCT. Further, if the SOFTWARE PRODUCT is installed or accessed through a network, the customer must purchase additional licenses for each user that accesses the SOFTWARE PRODUCT through the network.\u2028b. Use of Generated Output. You may distribute the output of your custom templates or the included templates in any way. You may make copies of the SOFTWARE PRODUCT provided that any such copy: (i) is created as an essential step in the utilization of the SOFTWARE PRODUCT as licensed under this EULA, or (ii) is only used for archival purposes to back-up the Software. All trademark, copyright and proprietary rights notices must be faithfully reproduced and included by you on such copies. You may not make any other copies of the Software.");
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
