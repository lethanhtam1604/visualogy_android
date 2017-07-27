package com.visualogyx.adapter.inspector;

import android.os.AsyncTask;

import com.visualogyx.apirequest.account.AccountSuccessResponseModel;

import java.util.ArrayList;

public class InspectorAsyncTask extends AsyncTask<Void, AccountSuccessResponseModel, Void> {

    private InspectorListViewAdapter inspectorListViewAdapter;
    private InspectorFinishLoadAsyncTask inspectorFinishLoadAsyncTask;
    private ArrayList<AccountSuccessResponseModel> accountSuccessResponseModels;

    public InspectorAsyncTask(InspectorListViewAdapter inspectorListViewAdapter, InspectorFinishLoadAsyncTask inspectorFinishLoadAsyncTask, ArrayList<AccountSuccessResponseModel> accountSuccessResponseModels) {
        this.inspectorListViewAdapter = inspectorListViewAdapter;
        this.inspectorFinishLoadAsyncTask = inspectorFinishLoadAsyncTask;
        this.accountSuccessResponseModels = accountSuccessResponseModels;
    }

    @Override
    protected void onPreExecute() {
        inspectorListViewAdapter.clear();
        inspectorListViewAdapter.notifyDataSetChanged();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < accountSuccessResponseModels.size(); i++) {
            publishProgress(accountSuccessResponseModels.get(i));
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(AccountSuccessResponseModel... values) {
        inspectorListViewAdapter.addInspector(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        inspectorListViewAdapter.notifyDataSetChanged();
        if (inspectorFinishLoadAsyncTask != null)
            inspectorFinishLoadAsyncTask.result(accountSuccessResponseModels);
        super.onPostExecute(result);
    }
}
