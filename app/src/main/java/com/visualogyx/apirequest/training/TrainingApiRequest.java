package com.visualogyx.apirequest.training;

import android.content.Context;

import com.google.gson.Gson;
import com.visualogyx.apirequest.ApiRequest;


public class TrainingApiRequest extends ApiRequest {

    private Gson gson;

    public TrainingApiRequest(Context context, RequestResponse callback) {
        super(context, callback);
        gson = new Gson();
    }

    public String getLoadingMessage() {
        return "";
    }

    public String getRequestUrl() {
        return "/post_training_image";
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
            TrainingSuccessResponseModel object = gson.fromJson(response, TrainingSuccessResponseModel.class);
            return object;
        } else
            return response;
    }
}
