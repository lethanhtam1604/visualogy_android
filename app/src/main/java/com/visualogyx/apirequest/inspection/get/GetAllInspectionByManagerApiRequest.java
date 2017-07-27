package com.visualogyx.apirequest.inspection.get;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GetAllInspectionByManagerApiRequest extends ApiRequest {

    private Gson gson;
    private String email;

    public GetAllInspectionByManagerApiRequest(Context context, ApiRequest.RequestResponse callback, String email) {
        super(context, callback);
        gson = new Gson();
        this.email = email;
    }

    public String getLoadingMessage() {
        return "";
    }

    public String getRequestUrl() {
        return "/get_manager_new_jobs?manager_name=" + email;
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
            Type listType = new TypeToken<ArrayList<InspectionSuccessResponseModel>>() {
            }.getType();
            ArrayList<InspectionSuccessResponseModel> result = gson.fromJson(response, listType);
            return result;
        }
        return new ArrayList<InspectionSuccessResponseModel>();
    }
}

