package com.visualogyx.activity;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.testfairy.TestFairy;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.adapter.assignment.AssignmentAsyncTask;
import com.visualogyx.adapter.assignment.AssignmentFinishLoadAsyncTask;
import com.visualogyx.adapter.assignment.AssignmentListViewAdapter;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;
import com.visualogyx.apirequest.inspection.get.GetAllInspectionByInspectorApiRequest;
import com.visualogyx.apirequest.inspection.get.GetAllInspectionByManagerApiRequest;
import com.visualogyx.apirequest.inspection.update.UpdateInspectionApiRequest;
import com.visualogyx.apirequest.inspection.update.UpdateInspectionModel;
import com.visualogyx.fragment.HomeFragment;
import com.visualogyx.manager.Global;
import com.visualogyx.model.PipeType;

import java.util.ArrayList;

public class TotalAccountActivity extends AppCompatActivity {

    private Button sendForTrainingBtn;
    private ImageView addImgView, subtractImgView;
    private int pipeCount = 0;
    private ListView assignmentLV;
    private LinearLayout emptyInspectionLL;
    private TextView refreshTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_count);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        } else {
            pipeCount = extras.getInt("pipeCount");
        }

        createActionBar();
        loadData();
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_total_count);
        setSupportActionBar(toolbar);

        TextView titleBar = (TextView) toolbar.findViewById(R.id.title_text);
        titleBar.setText("TOTAL COUNT " + pipeCount);
        titleBar.setTypeface(null, Typeface.BOLD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadData() {

        addImgView = (ImageView) findViewById(R.id.addImgView);
        addImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.pipeType = PipeType.Added;
                finish();
            }
        });

        subtractImgView = (ImageView) findViewById(R.id.subtractImgView);
        subtractImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.pipeType = PipeType.Removed;
                finish();
            }
        });

        sendForTrainingBtn = (Button) findViewById(R.id.sendForTrainingBtn);
        sendForTrainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.sendForTraining = true;
                finish();
            }
        });

        assignmentLV = (ListView) findViewById(R.id.assignmentLV);
        assignmentLV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        assignmentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                InspectionSuccessResponseModel accountSuccessResponseModel = (InspectionSuccessResponseModel) view.getTag();
                Utils.showHubNoTitle(TotalAccountActivity.this);
                ApiRequest apiRequest = new UpdateInspectionApiRequest(TotalAccountActivity.this, new SendTrainingRequestResponse());
                int countPipes = HomeFragment.drawingPad.getCountPipes();
                Bitmap bitmap = HomeFragment.drawingPad.drawPipesImageProcessing();
                UpdateInspectionModel trainingModel = new UpdateInspectionModel(accountSuccessResponseModel.request_id, "completed", String.valueOf(countPipes), accountSuccessResponseModel.location, "processed_file_name.jpg", Utils.base64FromBitMap(bitmap));
                apiRequest.setPostObject(trainingModel);
                apiRequest.execute();
            }
        });

        emptyInspectionLL = (LinearLayout) findViewById(R.id.emptyInspectionLL);
        refreshTV = (TextView) findViewById(R.id.refreshTV);
        refreshTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAssignmentList();
            }
        });
        loadAssignmentList();
    }

    private void loadAssignmentList() {
        AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
        if (accountSuccessResponseModel.user_type.compareTo("manager") == 0) {
            ApiRequest request = new GetAllInspectionByManagerApiRequest(TotalAccountActivity.this, new GetAllAssignmentRequestResponse(), accountSuccessResponseModel.email);
            request.execute();
        } else {
            ApiRequest request = new GetAllInspectionByInspectorApiRequest(TotalAccountActivity.this, new GetAllAssignmentRequestResponse(), accountSuccessResponseModel.email);
            request.execute();
        }
    }

    public class GetAllAssignmentRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if(status) {
                ArrayList<InspectionSuccessResponseModel> result = (ArrayList<InspectionSuccessResponseModel>) responseObject;
                AssignmentListViewAdapter adapter = new AssignmentListViewAdapter(TotalAccountActivity.this);
                assignmentLV.setAdapter(adapter);
                AssignmentAsyncTask inspectorAsyncTask = new AssignmentAsyncTask(adapter, new FinishLoadAsyncTask(), result);
                inspectorAsyncTask.execute();
            }
            else {
                Utils.showDisconnectNetworkMessage(TotalAccountActivity.this, "Can't fetch data. Please try again!");
            }
        }
    }

    private class FinishLoadAsyncTask implements AssignmentFinishLoadAsyncTask {
        @Override
        public void result(ArrayList<InspectionSuccessResponseModel> inspectionSuccessResponseModels) {
            if (inspectionSuccessResponseModels.size() > 0)
                emptyInspectionLL.setVisibility(View.GONE);
        }
    }

    public class SendTrainingRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                Intent intent = new Intent(TotalAccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                TotalAccountActivity.this.finish();
            } else {
                Utils.showDisconnectNetworkMessage(TotalAccountActivity.this, "Cannot upload image: The internet connection appears to be offline.");
            }
            Utils.hideHub();
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