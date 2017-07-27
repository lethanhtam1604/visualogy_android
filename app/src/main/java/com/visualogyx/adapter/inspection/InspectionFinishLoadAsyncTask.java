package com.visualogyx.adapter.inspection;

import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;

import java.util.ArrayList;

public interface InspectionFinishLoadAsyncTask {
    void result(ArrayList<InspectionSuccessResponseModel> inspectionSuccessResponseModels);
}
