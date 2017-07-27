package com.visualogyx.adapter.inspector;

import com.visualogyx.apirequest.account.AccountSuccessResponseModel;

import java.util.ArrayList;

public interface InspectorFinishLoadAsyncTask {
    void result(ArrayList<AccountSuccessResponseModel> accountSuccessResponseModels);
}
