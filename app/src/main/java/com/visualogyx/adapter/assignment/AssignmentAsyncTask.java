package com.visualogyx.adapter.assignment;


import android.location.Location;
import android.os.AsyncTask;

import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;
import com.visualogyx.manager.Global;

import java.util.ArrayList;

public class AssignmentAsyncTask extends AsyncTask<Void, InspectionSuccessResponseModel, Void> {

    private AssignmentListViewAdapter assignmentListViewAdapter;
    private AssignmentFinishLoadAsyncTask assignmentFinishLoadAsyncTask;
    private ArrayList<InspectionSuccessResponseModel> accountSuccessResponseModels;

    public AssignmentAsyncTask(AssignmentListViewAdapter assignmentListViewAdapter, AssignmentFinishLoadAsyncTask assignmentFinishLoadAsyncTask, ArrayList<InspectionSuccessResponseModel> accountSuccessResponseModels) {
        this.assignmentListViewAdapter = assignmentListViewAdapter;
        this.assignmentFinishLoadAsyncTask = assignmentFinishLoadAsyncTask;
        this.accountSuccessResponseModels = accountSuccessResponseModels;
    }

    @Override
    protected void onPreExecute() {
        assignmentListViewAdapter.clear();
        assignmentListViewAdapter.notifyDataSetChanged();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < accountSuccessResponseModels.size(); i++) {

            InspectionSuccessResponseModel inspectionSuccessResponseModel = accountSuccessResponseModels.get(i);
            String[] locationList = inspectionSuccessResponseModel.location.split(",");

            if (locationList.length > 1) {
                Location location = new Location("");
                location.setLatitude(Double.parseDouble(locationList[0]));
                location.setLongitude(Double.parseDouble(locationList[1]));
                inspectionSuccessResponseModel.address = Utils.addressFromLocation(Global.context, location);
            } else
                inspectionSuccessResponseModel.address = "";

            publishProgress(inspectionSuccessResponseModel);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(InspectionSuccessResponseModel... values) {
        assignmentListViewAdapter.addAssignment(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        assignmentListViewAdapter.notifyDataSetChanged();
        if (assignmentFinishLoadAsyncTask != null)
            assignmentFinishLoadAsyncTask.result(accountSuccessResponseModels);
        super.onPostExecute(result);
    }
}
