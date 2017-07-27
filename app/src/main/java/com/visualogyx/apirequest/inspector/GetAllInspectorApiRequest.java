package com.visualogyx.apirequest.inspector;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GetAllInspectorApiRequest extends ApiRequest {

    private Gson gson;

    public GetAllInspectorApiRequest(Context context, ApiRequest.RequestResponse callback) {
        super(context, callback);
        gson = new Gson();
    }

    public String getLoadingMessage() {
        return "";
    }

    public String getRequestUrl() {
        return "/get_inspectors";
    }

    public String getContentType() {
        return "application/json";
    }

    public String getRequestType() {
        return "GET";
    }

    public boolean isOpenProgressBar() {
        return false;
    }

    public String parseBody() {
        return null;
    }

    public boolean isImageProcessing() {
        return false;
    }

    public Object resultReceiver(String response) {
        if (responseSuccess) {
            Type listType = new TypeToken<ArrayList<AccountSuccessResponseModel>>() {
            }.getType();
            ArrayList<AccountSuccessResponseModel> result = gson.fromJson(response, listType);
            return result;
        }
        return new ArrayList<AccountSuccessResponseModel>();
    }
}
