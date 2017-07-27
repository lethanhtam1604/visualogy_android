package com.visualogyx.apirequest.processimage;

import android.content.Context;

import com.google.gson.Gson;
import com.visualogyx.apirequest.ApiRequest;

import java.util.ArrayList;


public class ProcessImageApiRequest extends ApiRequest {

    private Gson gson;

    public ProcessImageApiRequest(Context context, RequestResponse callback) {
        super(context, callback);
        gson = new Gson();
    }

    public String getLoadingMessage() {
        return "";
    }

    public String getRequestUrl() {
        return "/process_image";
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
        return true;
    }

    public Object resultReceiver(String response) {
        if (responseSuccess) {

            ArrayList<ProcessImageSuccessResponseModel> result = new ArrayList<>();
            if (response.compareTo("") != 0) {
                response = response.replaceAll("\\D+", ",");
                String[] listPoint = response.split(",");
                for (int i = 1; i < listPoint.length; i++) {
                    ProcessImageSuccessResponseModel processImageSuccessResponseModel = new ProcessImageSuccessResponseModel();
                    processImageSuccessResponseModel.id = listPoint[i];
                    processImageSuccessResponseModel.centre_x = listPoint[i + 1];
                    processImageSuccessResponseModel.centre_y = listPoint[i + 2];
                    processImageSuccessResponseModel.radius = listPoint[i + 3];
                    i = i + 4;
                    result.add(processImageSuccessResponseModel);
                }
            }
            return result;
        } else
            return response;
    }
}
