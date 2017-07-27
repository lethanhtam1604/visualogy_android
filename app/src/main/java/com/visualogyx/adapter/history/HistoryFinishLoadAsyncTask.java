package com.visualogyx.adapter.history;

import com.visualogyx.apirequest.history.HistorySuccessResponseModel;

import java.util.ArrayList;

public interface HistoryFinishLoadAsyncTask {
    void result(ArrayList<HistorySuccessResponseModel> historySuccessResponseModels);
}
