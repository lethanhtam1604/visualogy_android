package com.visualogyx.adapter.assignment;

import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;

import java.util.ArrayList;

public interface AssignmentFinishLoadAsyncTask {
    void result(ArrayList<InspectionSuccessResponseModel> inspectionSuccessResponseModels);
}
