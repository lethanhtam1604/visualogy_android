package com.visualogyx.apirequest.inspection.update;

import android.content.Context;

import com.google.gson.Gson;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;


public class UpdateInspectionApiRequest extends ApiRequest {

    private Gson gson;

    public UpdateInspectionApiRequest(Context context, RequestResponse callback) {
        super(context, callback);
        gson = new Gson();
    }

    public String getLoadingMessage() {
        return "";
    }

    public String getRequestUrl() {
        return "/update_inspection_results";
    }

    public String getContentType() {
        return "application/json";
    }

    public String getRequestType() {
        return "POST";
    }

    public boolean isOpenProgressBar() {
        return false;
    }

    public String parseBody() {
        return gson.toJson(postObject);
    }

    public boolean isImageProcessing() {
        return false;
    }

    public Object resultReceiver(String response) {
        if (responseSuccess) {
            InspectionSuccessResponseModel object = gson.fromJson(response, InspectionSuccessResponseModel.class);
            return object;
        } else
            return response;
    }
}
