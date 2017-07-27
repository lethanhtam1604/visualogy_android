package com.visualogyx.adapter.inspection;

import android.location.Location;
import android.os.AsyncTask;

import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;
import com.visualogyx.manager.Global;

import java.util.ArrayList;

public class InspectionAsyncTask extends AsyncTask<Void, InspectionSuccessResponseModel, Void> {

    private InspectionListViewAdapter inspectionListViewAdapter;
    private ArrayList<InspectionSuccessResponseModel> inspectionSuccessResponseModels;
    private InspectionFinishLoadAsyncTask inspectionFinishLoadAsyncTask;

    public InspectionAsyncTask(InspectionListViewAdapter inspectionListViewAdapter, ArrayList<InspectionSuccessResponseModel> inspectionSuccessResponseModels, InspectionFinishLoadAsyncTask inspectionFinishLoadAsyncTask) {
        this.inspectionListViewAdapter = inspectionListViewAdapter;
        this.inspectionSuccessResponseModels = inspectionSuccessResponseModels;
        this.inspectionFinishLoadAsyncTask = inspectionFinishLoadAsyncTask;
    }

    @Override
    protected void onPreExecute() {
        inspectionListViewAdapter.clear();
        inspectionListViewAdapter.notifyDataSetChanged();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < inspectionSuccessResponseModels.size(); i++) {
            InspectionSuccessResponseModel inspectionSuccessResponseModel = inspectionSuccessResponseModels.get(i);
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
        inspectionListViewAdapter.addInspection(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        inspectionListViewAdapter.notifyDataSetChanged();
        if (inspectionFinishLoadAsyncTask != null)
            inspectionFinishLoadAsyncTask.result(inspectionSuccessResponseModels);
        super.onPostExecute(result);
    }
}
