package com.visualogyx.adapter.history;

import android.location.Location;
import android.os.AsyncTask;

import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.history.HistorySuccessResponseModel;
import com.visualogyx.manager.Global;

import java.util.ArrayList;

public class HistoryAsyncTask extends AsyncTask<Void, HistorySuccessResponseModel, Void> {

    private HistoryListViewAdapter historyListViewAdapter;
    private ArrayList<HistorySuccessResponseModel> historySuccessResponseModels;
    private HistoryFinishLoadAsyncTask historyFinishLoadAsyncTask;

    public HistoryAsyncTask(HistoryListViewAdapter historyListViewAdapter, ArrayList<HistorySuccessResponseModel> historySuccessResponseModels, HistoryFinishLoadAsyncTask historyFinishLoadAsyncTask) {
        this.historyListViewAdapter = historyListViewAdapter;
        this.historySuccessResponseModels = historySuccessResponseModels;
        this.historyFinishLoadAsyncTask = historyFinishLoadAsyncTask;
    }

    @Override
    protected void onPreExecute() {
        historyListViewAdapter.clear();
        historyListViewAdapter.notifyDataSetChanged();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < historySuccessResponseModels.size(); i++) {
            HistorySuccessResponseModel historySuccessResponseModel = historySuccessResponseModels.get(i);
            String[] locationList = historySuccessResponseModel.location.split(",");

            if (locationList.length > 1) {
                Location location = new Location("");
                location.setLatitude(Double.parseDouble(locationList[0]));
                location.setLongitude(Double.parseDouble(locationList[1]));
                historySuccessResponseModel.address = Utils.addressFromLocation(Global.context, location);
            } else
                historySuccessResponseModel.address = "";

            publishProgress(historySuccessResponseModel);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(HistorySuccessResponseModel... values) {
        historyListViewAdapter.addHistory(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        historyListViewAdapter.notifyDataSetChanged();
        if (historyFinishLoadAsyncTask != null)
            historyFinishLoadAsyncTask.result(historySuccessResponseModels);
        super.onPostExecute(result);
    }
}
